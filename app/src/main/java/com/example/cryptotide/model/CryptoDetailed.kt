package com.example.cryptotide.model

import androidx.annotation.Nullable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoDetailed(
    val symbol: String,
    val name: String,
    val image: Image,
    @SerialName("genesis_date") val genesisDate: String,
    val description: Description,
    val links: Links
)

@Serializable
data class Links(
    @SerialName("repos_url") val reposUrl: ReposUrl,
    @SerialName("homepage") val homePage: List<String>,
    val whitepaper: String,
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
data class Description(
    val en: String
)

// TODO: Finish
@Serializable
data class MarketData(
    @SerialName("current_price") val currentPrice: List<String>
)