package com.example.moneymitra.data.model

data class LoanRequest(
    val no_of_dependents: Int,
    val education: String,
    val self_employed: String,
    val income_annum: Double,
    val loan_amount: Double,
    val loan_term: Int,
    val cibil_score: Int,
    val residential_assets_value: Double,
    val commercial_assets_value: Double,
    val luxury_assets_value: Double,
    val bank_asset_value: Double
)