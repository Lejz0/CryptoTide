package com.example.cryptotide.model.service.impl

import com.example.cryptotide.model.service.ChainBaseWalletApiService
import com.example.cryptotide.model.service.CryptocurrencyApiService
import com.example.cryptotide.model.service.NewsApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitInstance {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    val api: CryptocurrencyApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CryptocurrencyApiService::class.java)
    }

    val wallet_api: ChainBaseWalletApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.chainbase.online/v1/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ChainBaseWalletApiService::class.java)
    }

    val news_api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NewsApiService::class.java)
    }
}