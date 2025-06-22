package com.example.cryptotide.model.service

import com.example.cryptotide.model.ChainBaseResponse
import com.example.cryptotide.model.TopHolder
import com.example.cryptotide.model.TopTransaction
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ChainBaseWalletApiService {
    @GET("token/top-holders")
    suspend fun getTopHolders(
        @Query("chain_id") chainId: String,
        @Query("contract_address") contractAddress: String,
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1,
        @Header("x-api-key") apiKey: String
    ): ChainBaseResponse<TopHolder>

    @GET("account/txs")
    suspend fun getTopTransactionsForWallet(
        @Query("chain_id") chainId: String,
        @Query("address") address: String,
        @Query("limit") limit: Int = 5,
        @Query("page") page: Int = 1,
        @Header("x-api-key") apiKey: String
    ): ChainBaseResponse<TopTransaction>
}