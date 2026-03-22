package com.example.moneymitra.data.model

data class Chit(
    val id: String = "",
    val name: String = "",
    val totalAmount: Double = 0.0,
    val months: Int = 0,
    val monthlyAmount: Double = 0.0,
    val startDate: Long = 0L,

    val manager: Boolean = false,   // ✅ FIXED
    val managerId: String = "",
    val managerName: String = "",
    val due : Int = 0
)