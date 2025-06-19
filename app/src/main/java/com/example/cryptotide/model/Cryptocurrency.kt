package com.example.cryptotide.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cryptocurrency(
    val id: String,
    val symbol: String,
    val name: String,
    @SerialName("image") val imgSrc: String,
    @SerialName("current_price") val price: Double
)