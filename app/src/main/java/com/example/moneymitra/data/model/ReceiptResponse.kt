package com.example.moneymitra.data.model

data class ReceiptResponse(
    val name: String,
    val amount: Double,
    val category: String,
    val note: String,
    val type: String,
    val createdAt: Long
)
