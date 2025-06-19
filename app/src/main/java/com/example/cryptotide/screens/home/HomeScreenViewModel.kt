package com.example.cryptotide.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cryptotide.CryptoTideAppViewModel
import com.example.cryptotide.model.Cryptocurrency
import com.example.cryptotide.model.service.impl.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor() : CryptoTideAppViewModel() {
    var coins by mutableStateOf<List<Cryptocurrency>>(emptyList())
        private set

    init {
        fetchCryptocurrencies()
    }

    private fun fetchCryptocurrencies() {
        launchCatching {
            val result = RetrofitInstance.api.getCryptocurrencies()
            coins = result
        }
    }
}