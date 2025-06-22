package com.example.cryptotide.model

import androidx.annotation.Nullable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoDetailed(
    val id: String,
    val symbol: String,
    val name: String,
    val image: Image,
    @SerialName("genesis_date") val genesisDate: String? = null,
    val description: Description = Description(""),
    val links: Links = Links(ReposUrl(emptyList()), emptyList(), ""),
    @SerialName("market_data") val marketData: MarketData? = null,
    val platforms: Map<String, String>? = null
)

@Serializable
data class Links(
    @SerialName("repos_url") val reposUrl: ReposUrl,
    @SerialName("homepage") val homePage: List<String>,
    val whitepaper: String = "",
)

@Serializable
data class Description(
    val en: String
)

@Serializable
data class ReposUrl(
    val github: List<String>
)

@Serializable
data class Image(
    val small: String
)

@Serializable
data class MarketData(
    @SerialName("current_price") val currentPrice: Map<String, Double>,
    @SerialName("market_cap") val marketCap: Map<String, Double> = emptyMap(),
    @SerialName("total_volume") val totalVolume: Map<String, Double> = emptyMap(),
    @SerialName("high_24h") val high24h: Map<String, Double>? = null,
    @SerialName("low_24h") val low24h: Map<String, Double>? = null,
    @SerialName("price_change_24h") val priceChange24h: Double? = null,
    @SerialName("price_change_percentage_24h") val priceChangePercentage24h: Double? = null,
    @SerialName("price_change_percentage_7d") val priceChangePercentage7d: Double? = null,
    @SerialName("price_change_percentage_30d") val priceChangePercentage30d: Double? = null,
    @SerialName("sparkline_7d") val sparkline7d: Sparkline? = null
)

@Serializable
data class Sparkline(
    val price: List<Double>? = null
)