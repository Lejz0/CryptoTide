package com.example.cryptotide.screens.coin_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.CryptoDetailed
import com.example.cryptotide.model.service.impl.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class CoinDetailScreenViewModel @Inject constructor() : CryptoTideAppViewModel() {
    var coin by mutableStateOf<CryptoDetailed?>(null)
        private set

    internal fun getCoinDetails(coinId: String) {
        launchCatching {
            val result = RetrofitInstance.api.getCoinDetails(coinId)
            coin = result
        }
    }
}