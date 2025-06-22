package com.example.cryptotide.screens.coin_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cryptotide.model.CryptoDetailed
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String,
    navController: NavController,
    viewModel: CoinDetailScreenViewModel = hiltViewModel()
) {
    val uriHandler = LocalUriHandler.current
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(coinId) {
        isLoading = true
        error = null
        try {
            viewModel.getCoinDetails(coinId)
        } catch (e: Exception) {
            error = "Failed to load coin data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Force light theme for this screen regardless of system settings
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = Color.White,
            surface = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black,
            primaryContainer = Color(0xFFE6F2FF),
            onPrimaryContainer = Color(0xFF001E36),
            surfaceVariant = Color(0xFFEEF0F2),
            onSurfaceVariant = Color(0xFF41484D)
        )
    ) {
        Scaffold(
            containerColor = Color.White,
            contentColor = Color.Black,
            topBar = {
                TopAppBar(
                    title = { Text(text = viewModel.coin?.name ?: "Coin Details") },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE6F2FF),
                        titleContentColor = Color(0xFF001E36)
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)  // Explicit white background
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> {
                        LoadingView()
                    }
                    error != null -> {
                        ErrorView(error!!)
                    }
                    viewModel.coin != null -> {
                        CoinDetailContent(
                            coin = viewModel.coin!!,
                            onLinkClick = { link -> uriHandler.openUri(link) },
                            // Add navigation to wallet screen
                            onViewWalletsClick = {
                                navController.navigate("wallet_screen/${coinId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CoinDetailContent(
    coin: CryptoDetailed,
    onViewWalletsClick: () -> Unit = {},
    onLinkClick: (String) -> Unit
) {
    var expandedDescription by remember { mutableStateOf(false) }
    val currentPrice = coin.marketData?.currentPrice?.get("usd")
    val sparklineData = coin.marketData?.sparkline7d?.price ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Coin header with image and symbol
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coin.image.small,
                contentDescription = "${coin.name} logo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = coin.symbol.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Divider()

        // Price information section
        InfoCard(
            title = "Market Data",
            icon = Icons.Default.AttachMoney
        ) {
            Column {
                // Current Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Current Price",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "$${currentPrice?.let { "%.2f".format(it) } ?: "N/A"}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 24h Price Change
                if (coin.marketData?.priceChangePercentage24h != null) {
                    val change24h = coin.marketData.priceChangePercentage24h
                    val isPositive = change24h >= 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "24h Change",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "${String.format("%.2f", change24h)}%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "24h Change",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "N/A",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }

                // Price Chart
                if (sparklineData.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "7-Day Price Chart",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    PriceChart(
                        sparklineData = sparklineData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Historical price data not available",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray
                    )
                }

                if (coin.marketData != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Additional market stats in 2 columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left column
                        Column(modifier = Modifier.weight(1f)) {
                            MarketStatItem(
                                label = "Market Cap",
                                value = "$${formatLargeNumber(coin.marketData.marketCap["usd"])}"
                            )

                            MarketStatItem(
                                label = "24h High",
                                value = "$${coin.marketData.high24h?.get("usd")?.let { "%.2f".format(it) } ?: "N/A"}"
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Right column
                        Column(modifier = Modifier.weight(1f)) {
                            MarketStatItem(
                                label = "Volume (24h)",
                                value = "$${formatLargeNumber(coin.marketData.totalVolume["usd"])}"
                            )

                            MarketStatItem(
                                label = "24h Low",
                                value = "$${coin.marketData.low24h?.get("usd")?.let { "%.2f".format(it) } ?: "N/A"}"
                            )
                        }
                    }
                }
            }
        }

        // Genesis Date section
        InfoCard(
            title = "Genesis Date",
            icon = Icons.Default.CalendarMonth
        ) {
            Text(
                text = coin.genesisDate?.takeIf { it.isNotEmpty() } ?: "Not available",
                style = MaterialTheme.typography.bodyMedium,
                color = if (coin.genesisDate.isNullOrEmpty()) Color.Gray else Color.Unspecified
            )
        }

        // Description section
        InfoCard(
            title = "About",
            icon = Icons.Default.Description
        ) {
            Column {
                if (coin.description.en.isNotEmpty()) {
                    Text(
                        text = if (expandedDescription) coin.description.en
                        else coin.description.en.take(150) + if (coin.description.en.length > 150) "..." else "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (coin.description.en.length > 150) {
                        TextButton(
                            onClick = { expandedDescription = !expandedDescription },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = if (expandedDescription) "Show less" else "Show more")
                        }
                    }
                } else {
                    Text(
                        text = "No description available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        // Links section
        InfoCard(
            title = "Resources",
            icon = Icons.Default.Link
        ) {
            Column {
                // Website
                if (!coin.links.homePage.isNullOrEmpty() && coin.links.homePage[0].isNotEmpty()) {
                    LinkButton(
                        text = "Official Website",
                        onClick = { onLinkClick(coin.links.homePage[0]) }
                    )
                }

                // GitHub
                if (!coin.links.reposUrl.github.isNullOrEmpty() && coin.links.reposUrl.github[0].isNotEmpty()) {
                    LinkButton(
                        text = "GitHub Repository",
                        onClick = { onLinkClick(coin.links.reposUrl.github[0]) }
                    )
                }

                // Whitepaper
                if (!coin.links.whitepaper.isNullOrEmpty()) {
                    LinkButton(
                        text = "Whitepaper",
                        onClick = { onLinkClick(coin.links.whitepaper) }
                    )
                }

                // If no links are available
                if ((coin.links.homePage.isNullOrEmpty() || coin.links.homePage[0].isEmpty()) &&
                    (coin.links.reposUrl.github.isNullOrEmpty() || coin.links.reposUrl.github[0].isEmpty()) &&
                    coin.links.whitepaper.isNullOrEmpty()) {
                    Text(
                        text = "No resource links available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
        if (coin.platforms?.isNotEmpty() == true) {
            // Add some spacing
            Spacer(modifier = Modifier.height(8.dp))

            // Wallet holders button card
            InfoCard(
                title = "Token Holders",
                icon = Icons.Default.AccountBalance
            ) {
                Button(
                    onClick = onViewWalletsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Top Wallet Holders")
                }
            }
        }
    }
}

@Composable
fun PriceChart(
    sparklineData: List<Double>,
    modifier: Modifier = Modifier
) {
    val minValue = sparklineData.minOrNull() ?: 0.0
    val maxValue = sparklineData.maxOrNull() ?: 0.0
    val range = maxValue - minValue
    val lastValue = sparklineData.lastOrNull() ?: 0.0
    val firstValue = sparklineData.firstOrNull() ?: 0.0
    val isPositiveTrend = lastValue > firstValue  // Changed to strictly greater than

    // Define colors explicitly
    val positiveColor = Color(0xFF4CAF50)  // Green
    val negativeColor = Color(0xFFF44336)  // Red

    // Correctly assign color based on trend
    val lineColor = if (isPositiveTrend) positiveColor else negativeColor

    Column(modifier = modifier) {

        Text(
            text = "First: $${String.format("%.2f", firstValue)} → Last: $${String.format("%.2f", lastValue)} (${if(isPositiveTrend) "UP" else "DOWN"})",
            style = MaterialTheme.typography.bodySmall,
            color = lineColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "$${String.format("%.2f", firstValue)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )


            val changeAmount = lastValue - firstValue
            val changePercent = if (firstValue != 0.0) (changeAmount / firstValue) * 100 else 0.0

            Text(
                text = "${if (changeAmount >= 0) "+" else ""}${String.format("%.2f", changeAmount)} (${String.format("%.2f", changePercent)}%)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (changeAmount >= 0) positiveColor else negativeColor
            )


            Text(
                text = "$${String.format("%.2f", lastValue)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }


        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFF5F9FF))
            .clip(RoundedCornerShape(8.dp))
        ) {
            val width = size.width
            val height = size.height
            val paddingHorizontal = width * 0.08f  // Increased padding for labels
            val paddingVertical = height * 0.1f
            val chartWidth = width - (paddingHorizontal * 2)
            val chartHeight = height - (paddingVertical * 2)


            val gridLineCount = 3
            val gridLineStep = chartHeight / (gridLineCount - 1)

            for (i in 0 until gridLineCount) {

                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(paddingHorizontal, paddingVertical + i * gridLineStep),
                    end = Offset(width - paddingHorizontal, paddingVertical + i * gridLineStep),
                    strokeWidth = 0.5f
                )

                val gridLinePrice = maxValue - (i * (range / (gridLineCount - 1)))

                val formattedPrice = when {
                    gridLinePrice >= 10000 -> "${(gridLinePrice / 1000).toInt()}K"
                    gridLinePrice >= 1000 -> "%.1fK".format(gridLinePrice / 1000)
                    gridLinePrice >= 100 -> "%.0f".format(gridLinePrice)
                    else -> "%.2f".format(gridLinePrice)
                }

                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 10.dp.toPx()
                        textAlign = android.graphics.Paint.Align.RIGHT
                    }
                    drawText(
                        "$$formattedPrice",
                        paddingHorizontal - 6.dp.toPx(),  // Increased offset
                        paddingVertical + i * gridLineStep + 4.dp.toPx(),
                        textPaint
                    )
                }
            }

            val timeIndicators = 3
            val timeStep = chartWidth / (timeIndicators - 1)

            for (i in 0 until timeIndicators) {
                val x = paddingHorizontal + i * timeStep
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(x, paddingVertical),
                    end = Offset(x, height - paddingVertical),
                    strokeWidth = 0.5f
                )

                val dayLabel = when(i) {
                    0 -> "7d ago"
                    timeIndicators - 1 -> "Today"
                    else -> ""
                }

                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.DKGRAY
                        textSize = 10.dp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(
                        dayLabel,
                        x,
                        height - paddingVertical + 14.dp.toPx(),
                        textPaint
                    )
                }
            }

            // Draw price line
            if (sparklineData.size > 1) {
                val path = Path()
                val step = chartWidth / (sparklineData.size - 1)

                sparklineData.forEachIndexed { index, price ->
                    val x = paddingHorizontal + (index * step)
                    val normalizedPrice = if (range != 0.0) {
                        ((price - minValue) / range)
                    } else 0.5
                    val y = height - paddingVertical - (normalizedPrice * chartHeight).toFloat()

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }

                // Draw line with correct color
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Fill area below line
                val fillPath = Path()
                fillPath.addPath(path)
                fillPath.lineTo(width - paddingHorizontal, height - paddingVertical)
                fillPath.lineTo(paddingHorizontal, height - paddingVertical)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        startY = paddingVertical,
                        endY = height - paddingVertical
                    )
                )

                // Draw data points
                val pointCount = minOf(7, sparklineData.size)
                val pointStep = if (sparklineData.size > pointCount)
                    sparklineData.size / pointCount else 1

                for (i in 0 until sparklineData.size step pointStep) {
                    if (i >= sparklineData.size) continue

                    val price = sparklineData[i]
                    val x = paddingHorizontal + (i * step)
                    val normalizedPrice = if (range != 0.0) {
                        ((price - minValue) / range)
                    } else 0.5
                    val y = height - paddingVertical - (normalizedPrice * chartHeight).toFloat()

                    drawCircle(
                        color = lineColor,
                        radius = 3.dp.toPx(),
                        center = Offset(x, y)
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 1.5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}

@Composable
fun MarketStatItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Helper function to format large numbers
fun formatLargeNumber(value: Double?): String {
    if (value == null) return "N/A"

    return when {
        value >= 1_000_000_000 -> String.format("%.2fB", value / 1_000_000_000)
        value >= 1_000_000 -> String.format("%.2fM", value / 1_000_000)
        value >= 1_000 -> String.format("%.2fK", value / 1_000)
        else -> String.format("%.2f", value)
    }
}

@Composable
fun InfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            content()
        }
    }
}

@Composable
fun LinkButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = "Open Link"
        )
    }
}



