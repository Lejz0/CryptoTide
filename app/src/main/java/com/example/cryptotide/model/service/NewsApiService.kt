package com.example.cryptotide.model.service

import com.example.cryptotide.BuildConfig
import com.example.cryptotide.model.NewsApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("language") language: String = "en",
        @Query("pageSize") pageSize: Int = 3
    ): NewsApiResponse
}