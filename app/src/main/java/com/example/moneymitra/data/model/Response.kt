package com.example.moneymitra.data.model

data class Response(
    val id: String,
    val name: String,
    val amount: Double,
    val type: String,
    val category: String,
    val note: String,
    val createdAt: Long
)