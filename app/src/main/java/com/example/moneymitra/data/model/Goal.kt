package com.example.moneymitra.data.model

data class Goal(
    val id: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val savedAmount: Double = 0.0,
    val deadline: Long = 0L,
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)