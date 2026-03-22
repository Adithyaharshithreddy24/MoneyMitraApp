package com.example.moneymitra.data.model

data class Member(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val due: Int = 0,
    val payout: Boolean =false,
    val payoutmonth : Int = 0
)