package com.example.moneymitra.data.model

data class LoanResponse(
    val loan_status: String,
    val approval_probability: Double,
    val risk_level: String,
    val recommendations: List<String>,
    val explanation: List<Explanation>
)

data class Explanation(
    val feature: String,
    val impact: Double
)