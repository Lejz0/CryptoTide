package com.example.cryptotide.screens.coin_detail

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

    internal fun getCoinDetails(coinId: String) {
        launchCatching {
            val result = RetrofitInstance.api.getCoinDetails(coinId)
            coin = result

            fetchTopHolders(coinId)
        }
    }

    private fun fetchTopHolders(coinId: String) {
        launchCatching {
            // Reset previous data
            topHolders = null
            topHoldersError = null

            try {
                val platformContract = findSupportedPlatform(coin)
                Log.d("supportedPlatform", platformContract.toString())
                if (platformContract != null) {
                    val response = RetrofitInstance.wallet_api.getTopHolders(
                        chainId = platformContract.second,
                        contractAddress = platformContract.first,
                        apiKey = BuildConfig.WALLET_API_KEY // Consider moving to secure storage
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
}