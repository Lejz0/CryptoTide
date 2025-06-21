package com.example.cryptotide.model.service

import com.example.cryptotide.model.CryptoDetailed
import com.example.cryptotide.model.Cryptocurrency
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptocurrencyApiService {
    @GET("coins/markets")
    suspend fun getCryptocurrencies(@Query("vs_currency") currency: String = "usd"): List<Cryptocurrency>

    @GET("coins/{id}")
    suspend fun getCoinDetails(
        @Path("id") coinId: String
    ): CryptoDetailed
}