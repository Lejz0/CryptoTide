package com.example.cryptotide.screens.home

import android.util.Log
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
    var allCoins by mutableStateOf<List<Cryptocurrency>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    val coins: List<Cryptocurrency>
        get() = if (searchQuery.isBlank()) allCoins
        else allCoins.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.symbol.contains(searchQuery, ignoreCase = true)
        }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    init {
        fetchCryptocurrencies()
    }

    private fun fetchCryptocurrencies() {
        launchCatching {
            val result = RetrofitInstance.api.getCryptocurrencies()
            Log.d("all coins", result.toString())
            allCoins = result
        }
    }
}