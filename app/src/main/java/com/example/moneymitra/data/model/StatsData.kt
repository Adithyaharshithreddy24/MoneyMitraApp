package com.example.moneymitra.data.model

data class CategoryData(
    val name: String,
    val amount: Double,
    val percentage: Float,
    val colorHex: Long
)

data class StatsData(
    val totalSavings: Double,
    val netOutstanding: Double,
    val monthlyIncome: Double,
    val monthlyExpense: Double,

    // Dynamic Graph Data
    val incomeTrend: List<Float>,
    val expenseTrend: List<Float>,
    val trendLabels: List<String>,

    val weeklyIncome: List<Float>,
    val weeklyExpense: List<Float>,
    val performanceLabels: List<String>,

    val categorySpending: List<CategoryData>,

    val moneyLent: Double,
    val moneyLentPending: Double,
    val moneyBorrowed: Double,
    val moneyBorrowedPending: Double,

    val chitDue: Double
)