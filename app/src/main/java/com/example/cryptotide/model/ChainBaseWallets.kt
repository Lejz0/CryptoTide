package com.example.cryptotide.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChainBaseResponse<T>(
    val code: Int,
    val message: String,
    val data: List<T>,
    @SerialName("next_page") val nextPage: Int? = null,
    val count: Int? = null
)

@Serializable
data class TopHolder(
    val amount: String,
    @SerialName("original_amount") val originalAmount: String,
    @SerialName("usd_value") val usdValue: String,
    @SerialName("wallet_address") val walletAddress: String
)

@Serializable
data class TopTransaction(
    @SerialName("block_number") val blockNumber: Long,
    @SerialName("block_timestamp") val blockTimestamp: String,
    @SerialName("burnt_fee") val burntFee: Long,
    @SerialName("contract_address") val contractAddress: String,
    @SerialName("cumulative_gas_used") val cumulativeGasUsed: Long,
    @SerialName("effective_gas_price") val effectiveGasPrice: Long,
    @SerialName("from_address") val fromAddress: String,
    val gas: Long,
    @SerialName("gas_price") val gasPrice: Long,
    @SerialName("gas_used") val gasUsed: Long,
    val input: String,
    @SerialName("max_fee_per_gas") val maxFeePerGas: Long,
    @SerialName("max_priority_fee_per_gas") val maxPriorityFeePerGas: Long,
    val nonce: Long,
    @SerialName("saving_fee") val savingFee: Long,
    val status: Int,
    @SerialName("to_address") val toAddress: String,
    @SerialName("transaction_hash") val transactionHash: String,
    @SerialName("transaction_index") val transactionIndex: Int,
    @SerialName("tx_fee") val txFee: Long,
    val type: Int,
    val value: String
)