package com.example.cryptotide.ai

object AiAnalysisCache {
    @Volatile
    private var cache: Map<String, String> = emptyMap()

    fun getAnalysis(coinId: String): String? = cache[coinId]

    fun saveAnalysis(coinId: String, analysis: String) {
        synchronized(this) {
            cache = cache + (coinId to analysis)
        }
    }

    fun clear() {
        synchronized(this) {
            cache = emptyMap()
        }
    }
}