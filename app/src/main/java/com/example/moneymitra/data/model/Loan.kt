package com.example.moneymitra.data.model

data class Loan(
    val id: String = "",
    val name: String = "",

    val principal: Double = 0.0,
    val interestRate: Double = 0.0,
    val tenureYears: Int = 0,

    val emi: Double = 0.0,

    val loanType: String = "",
    val repaymentFrequency: String = "Monthly",

    val startDate: Long = System.currentTimeMillis(),
    val nextDueDate: Long = 0L,

    val createdAt: Long = System.currentTimeMillis()
)