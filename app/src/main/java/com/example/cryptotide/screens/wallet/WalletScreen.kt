package com.example.cryptotide.screens.wallet

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cryptotide.model.TopHolder
import com.example.cryptotide.model.TopTransaction
import com.example.cryptotide.screens.coin_detail.CoinDetailScreenViewModel
import com.example.cryptotide.screens.coin_detail.formatLargeNumber
import java.math.BigDecimal
import java.math.RoundingMode
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletsScreen(
    coinId: String,
    navController: NavController,
    viewModel: CoinDetailScreenViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val coin by remember { derivedStateOf { viewModel.coin } }
    val topHolders = viewModel.topHolders
    val topHoldersError = viewModel.topHoldersError
    val selectedWalletAddress = viewModel.selectedWalletAddress

    LaunchedEffect(coinId) {
        if (viewModel.coin == null) {
            viewModel.getCoinDetails(coinId, context, getTopHoldersCheck = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Wallet Holders for ${coin?.name ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            when {
                topHoldersError != null -> {
                    ErrorView(topHoldersError)
                }

                topHolders == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                topHolders.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No wallet data available for this token",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    val items = buildList {
                        add(HeaderItem("Showing top ${topHolders.size} wallets by balance"))

                        topHolders.forEach { holder ->
                            add(WalletItem(holder))
                            if (holder.walletAddress == selectedWalletAddress) {
                                add(TransactionsItem(viewModel.selectedWalletTransactions))
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(items) { item ->
                            when (item) {
                                is HeaderItem -> {
                                    Text(
                                        text = item.text,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                }
                                is WalletItem -> {
                                    val isSelected = item.holder.walletAddress == selectedWalletAddress
                                    WalletListItem(
                                        holder = item.holder,
                                        isSelected = isSelected,
                                        onClick = {
                                            if (isSelected) {
                                                viewModel.clearSelectedWallet()
                                            } else {
                                                viewModel.getWalletTransactions(item.holder.walletAddress)
                                            }
                                        }
                                    )
                                }
                                is TransactionsItem -> {
                                    TransactionsList(transactions = item.transactions)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private sealed class ListItem
private class HeaderItem(val text: String) : ListItem()
private class WalletItem(val holder: TopHolder) : ListItem()
private class TransactionsItem(val transactions: List<TopTransaction>?) : ListItem()

@Composable
private fun ErrorView(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Error Loading Wallet Data",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun WalletListItem(
    holder: TopHolder,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = shortenAddress(holder.walletAddress),
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "$${formatLargeNumber(holder.usdValue?.toDoubleOrNull())}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Amount: ${formatLargeNumber(holder.amount.toDoubleOrNull())}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = if (isSelected) "Tap to hide transactions" else "Tap to view transactions",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TransactionsList(transactions: List<TopTransaction>?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when {
                transactions == null -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                transactions.isEmpty() -> {
                    Text(
                        text = "No transactions found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                    ) {
                        items(transactions.take(5)) { tx ->
                            TransactionItem(tx)
                            if (tx != transactions.take(5).last()) {
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: TopTransaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            val timestamp = transaction.blockTimestamp.split("T")[0]
            Text(
                text = "Date: $timestamp",
                style = MaterialTheme.typography.bodySmall
            )

            val shortHash = transaction.transactionHash.take(8) + "..." +
                    transaction.transactionHash.takeLast(8)
            Text(
                text = "TX: $shortHash",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val shortFrom = transaction.fromAddress.take(6) + "..." +
                    transaction.fromAddress.takeLast(6)
            val shortTo = transaction.toAddress.take(6) + "..." +
                    transaction.toAddress.takeLast(6)

            Text(
                text = "From: $shortFrom",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "To: $shortTo",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            val valueInEth = (transaction.value.toBigDecimalOrNull() ?: BigDecimal.ZERO)
                .divide(BigDecimal("1000000000000000000"), 6, RoundingMode.HALF_UP)
                .toPlainString()

            Text(
                text = "Value: $valueInEth ETH",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun shortenAddress(address: String): String {
    return if (address.length > 10) {
        "${address.take(6)}...${address.takeLast(4)}"
    } else {
        address
    }
}

private fun formatWeiToEth(weiStr: String): String {
    return try {
        val wei = weiStr.toBigDecimal()
        val eth = wei.divide(BigDecimal("1000000000000000000"))
        if (eth < BigDecimal("0.000001")) {
            "<0.000001 ETH"
        } else {
            "%.6f ETH".format(eth)
        }
    } catch (e: Exception) {
        "0 ETH"
    }
}