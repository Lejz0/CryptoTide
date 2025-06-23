package com.example.cryptotide.screens.coin_detail

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.CryptoDetailed
import com.example.cryptotide.model.TopHolder
import com.example.cryptotide.model.TopTransaction
import com.example.cryptotide.model.service.impl.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import com.example.cryptotide.BuildConfig
import com.example.cryptotide.ai.AiAnalysisCache
import com.example.cryptotide.ai.GemmaAnalysisService

@HiltViewModel
class CoinDetailScreenViewModel @Inject constructor() : CryptoTideAppViewModel() {
    var coin by mutableStateOf<CryptoDetailed?>(null)
        private set

    var topHolders by mutableStateOf<List<TopHolder>?>(null)
        private set

    var topHoldersError by mutableStateOf<String?>(null)
        private set

    var selectedWalletTransactions by mutableStateOf<List<TopTransaction>?>(null)
        private set

    var selectedWalletAddress by mutableStateOf<String?>(null)
        private set

    internal fun getCoinDetails(coinId: String, context: Context, createAnalysisCheck: Boolean= false, getTopHoldersCheck: Boolean = false) {
        launchCatching {
            val result = RetrofitInstance.api.getCoinDetails(coinId)
            coin = result

            val cachedAnalysis = AiAnalysisCache.getAnalysis(coinId)
            if (cachedAnalysis != null) {
                aiResult = cachedAnalysis
            } else if (createAnalysisCheck) {
                generateAiPrompt(context, result, coinId)
            }

            if (getTopHoldersCheck) {
                fetchTopHolders(coinId)
            }
        }
    }

    private fun fetchTopHolders(coinId: String) {
        launchCatching {
            topHolders = null
            topHoldersError = null

            try {
                val platformContract = findSupportedPlatform(coin)
                Log.d("supportedPlatform", platformContract.toString())
                if (platformContract != null) {
                    val response = RetrofitInstance.wallet_api.getTopHolders(
                        chainId = platformContract.second,
                        contractAddress = platformContract.first,
                        apiKey = BuildConfig.WALLET_API_KEY
                    )

                    if (response.code == 0) {
                        topHolders = response.data
                        Log.d("topHolders", response.data.toString())
                    } else {
                        topHoldersError = response.message
                    }
                } else {
                    topHoldersError = "No supported blockchain found for this coin"
                }
            } catch (e: Exception) {
                topHoldersError = e.message ?: "Unknown error fetching wallet data"
            }
        }
    }

    fun getWalletTransactions(walletAddress: String) {
        selectedWalletAddress = walletAddress

        launchCatching {
            selectedWalletTransactions = null

            val platformContract = findSupportedPlatform(coin)
            if (platformContract != null) {
                val response = RetrofitInstance.wallet_api.getTopTransactionsForWallet(
                    chainId = platformContract.second,
                    address = walletAddress,
                    apiKey = BuildConfig.WALLET_API_KEY
                )

                if (response.code == 0) {
                    selectedWalletTransactions = response.data
                }
            }
        }
    }

    fun clearSelectedWallet() {
        selectedWalletAddress = null
        selectedWalletTransactions = null
    }

    private fun findSupportedPlatform(coin: CryptoDetailed?): Pair<String, String>? {
        if (coin?.platforms == null) return null

        val supportedPlatforms = mapOf(
            "ethereum" to "1",
            "polygon" to "137",
            "binance-smart-chain" to "56",
            "avalanche" to "43114",
            "arbitrum-one" to "42161",
            "optimism" to "10",
            "base" to "8453",
            "zksync" to "324",
            "merlin" to "4200"
        )

        for ((platform, contract) in coin.platforms) {
            val normalizedPlatform = platform.lowercase().replace(" ", "-")
            if (contract.isNotEmpty() && supportedPlatforms.containsKey(normalizedPlatform)) {
                return Pair(contract, supportedPlatforms[normalizedPlatform]!!)
            }
        }

        return null
    }

    var aiResult by mutableStateOf<String?>(null)
        private set

    var isAnalyzing by mutableStateOf(false)
        private set

    fun resetAiState() {
        aiResult = null
        isAnalyzing = false
    }

    fun startAiAnalysis(context: Context, prompt: String, coinId: String) {
        isAnalyzing = true
        aiResult = null

        val intent = Intent(context, GemmaAnalysisService::class.java).apply {
            putExtra("prompt", prompt)
            putExtra("coinId", coinId)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun onAiResultReceived(result: String) {
        aiResult = result
        isAnalyzing = false

        coin?.id?.let { coinId ->
            AiAnalysisCache.saveAnalysis(coinId, result)
        }
    }

    private fun generateAiPrompt(context: Context, coin: CryptoDetailed, coinId: String) {
        launchCatching {
            val symbol = coin.symbol.uppercase()
            val name = coin.name
            val desc = coin.description.en.take(300)
            val marketCap = coin.marketData?.marketCap?.get("usd") ?: "unknown"

            val articles = try {
                RetrofitInstance.news_api.getNews(
                    query = "$name cryptocurrency news",
                ).articles.take(3)
            } catch (e: Exception) {
                Log.e("NewsFetch", "Error fetching news: ${e.message}")
                emptyList()
            }

            val prompt = buildString {
                append("Analyse the cryptocurrency $name ($symbol).\n")
                append("Market cap in USD: $marketCap\n")
                append("Short description: $desc\n")

                if (articles.isNotEmpty()) {
                    append("Recent news highlights:\n")
                    articles.forEachIndexed { index, article ->
                        append("${index + 1}. ${article.title}\n")
                        article.description?.let {
                            append("   Summary: $it\n")
                        }
                    }
                }

                append("Your analysis should strongly include the news highlights.\n")
            }

            Log.d("AiPrompt", "Generated prompt: $prompt")

            startAiAnalysis(context, prompt, coinId)
        }
    }
}