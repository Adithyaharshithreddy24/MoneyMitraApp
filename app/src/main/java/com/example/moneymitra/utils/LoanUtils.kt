package com.example.moneymitra.utils

import kotlin.math.pow
import java.util.Calendar

fun calculateEMI(
    principal: Double,
    annualRate: Double,
    tenureYears: Int
): Double {

    val monthlyRate = annualRate / 12 / 100
    val tenureMonths = tenureYears *12

    return if (monthlyRate == 0.0) {
        principal / tenureMonths
    } else {
        (principal * monthlyRate * (1 + monthlyRate).pow(tenureMonths)) /
                ((1 + monthlyRate).pow(tenureMonths) - 1)
    }
}

fun calculateNextDueDate(): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, 1)
    return cal.timeInMillis
}