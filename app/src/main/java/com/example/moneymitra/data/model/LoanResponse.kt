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

data class LoansResponse(
    val property_value: Double,
    val loan_by_ltv: Double,
    val loan_by_income: Double,
    val eligible_loan: Double,
    val interest_rate: String,
    val tenure_years: Int,
    val emi_per_month: Double,
    val total_interest: Double,
    val total_payment: Double,
    val approval_status: String
)

data class GoldLoanResponse(
    val weight: Double,
    val price_per_gram: Double,
    val gold_value: Double,
    val loan_amount: Double,
    val interest_rate: String,
    val tenure_months: Int,
    val emi: Double,
    val total_interest: Double,
    val total_payment: Double
)

data class VehicleLoanResponse(
    val vehicle_price: Double,
    val vehicle_type: String,
    val loan_by_ltv: Double,
    val loan_by_income: Double,
    val eligible_loan: Double,
    val interest_rate: String,
    val tenure_years: Int,
    val emi_per_month: Double,
    val total_interest: Double,
    val total_payment: Double,
    val approval_status: String
)