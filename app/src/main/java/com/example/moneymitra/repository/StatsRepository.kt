package com.example.moneymitra.repository

import com.example.moneymitra.data.model.*
import com.example.moneymitra.ui.viewmodel.TimeFilter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class StatsRepository(
    private val chitRepo: ChitRepository
) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val categoryColors = listOf(
        0xFF1A237E, 0xFF60A5FA, 0xFF34D399,
        0xFFF87171, 0xFFD97706, 0xFFA855F7
    )

    // --- NEW: Fetch User Accounts ---
    suspend fun getAccounts(): List<Account> {
        val uid = auth.currentUser?.uid ?: throw Exception("No user")
        val snapshot = db.collection("users").document(uid).collection("accounts").get().await()
        return snapshot.documents.mapNotNull { it.toObject(Account::class.java)?.copy(id = it.id) }
    }

    suspend fun getStats(
        filter: TimeFilter,
        customStart: Long?,
        customEnd: Long?,
        accountId: String?
    ): StatsData {
        val uid = auth.currentUser?.uid ?: throw Exception("No user")

        // 1. Fetch All Transactions
        var allTransactions = getTransactionsSuspend(uid)

        // 2. Filter by Account (If specific account is selected)
        if (accountId != null) {
            allTransactions = allTransactions.filter { it.accountId == accountId }
        }

        // 3. Set Time Boundary
        val now = System.currentTimeMillis()
        val startTime = when (filter) {
            TimeFilter.WEEK -> now - (7L * 24 * 60 * 60 * 1000)
            TimeFilter.MONTH -> now - (30L * 24 * 60 * 60 * 1000)
            TimeFilter.YEAR -> now - (365L * 24 * 60 * 60 * 1000)
            TimeFilter.CUSTOM -> customStart ?: 0L
        }
        val endTime = if (filter == TimeFilter.CUSTOM && customEnd != null) customEnd else now

        // 4. Filter transactions
        val filteredTx = allTransactions.filter { it.createdAt in startTime..endTime }
        val incomeTx = filteredTx.filter { it.type == "INCOME" }
        val expenseTx = filteredTx.filter { it.type == "EXPENSE" }

        val totalIncome = incomeTx.sumOf { it.amount }
        val totalExpense = expenseTx.sumOf { it.amount }
        val totalSavings = totalIncome - totalExpense

        // 5. Monthly Averages (Scaled to fit the filter)
        val monthsInFilter = when (filter) {
            TimeFilter.WEEK -> 0.25
            TimeFilter.MONTH -> 1.0
            TimeFilter.YEAR -> 12.0
            TimeFilter.CUSTOM -> {
                val diffDays = ((endTime - startTime) / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
                maxOf(0.1, diffDays / 30.0)
            }
        }

        val monthlyIncome = totalIncome / monthsInFilter
        val monthlyExpense = totalExpense / monthsInFilter

        // 6. Category Distribution
        val totalExpenseFloat = totalExpense.toFloat().takeIf { it > 0 } ?: 1f
        val categoryData = expenseTx
            .groupBy { it.category.ifEmpty { "Others" } }
            .entries.mapIndexed { index, entry ->
                val sum = entry.value.sumOf { t -> t.amount }
                CategoryData(
                    name = entry.key,
                    amount = sum,
                    percentage = (sum / totalExpenseFloat).toFloat(),
                    colorHex = categoryColors[index % categoryColors.size]
                )
            }.sortedByDescending { it.amount }

        // 7. Lending & Borrowing (Filtered by account, but ignoring time filters)
        val lentTx = allTransactions.filter { it.type == "LENT" }
        val borrowedTx = allTransactions.filter { it.type == "BORROWED" }

        val moneyLent = lentTx.sumOf { it.amount }
        val moneyBorrowed = borrowedTx.sumOf { it.amount }
        val moneyLentPending = moneyLent
        val moneyBorrowedPending = moneyBorrowed

        // 8. Dynamic Graph Data Construction
        val (graphIncome, graphExpense, graphLabels) = generateChartData(incomeTx, expenseTx, filter, startTime, endTime)

        val chitDue = chitRepo.getTotalChitDue()

        return StatsData(
            totalSavings = totalSavings,
            netOutstanding = chitDue + moneyBorrowedPending,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            incomeTrend = graphIncome,
            expenseTrend = graphExpense,
            trendLabels = graphLabels,
            weeklyIncome = graphIncome,
            weeklyExpense = graphExpense,
            performanceLabels = graphLabels,
            categorySpending = categoryData,
            moneyLent = moneyLent,
            moneyLentPending = moneyLentPending,
            moneyBorrowed = moneyBorrowed,
            moneyBorrowedPending = moneyBorrowedPending,
            chitDue = chitDue
        )
    }

    private fun generateChartData(
        incomeTx: List<Transaction>,
        expenseTx: List<Transaction>,
        filter: TimeFilter,
        startTime: Long,
        endTime: Long
    ): Triple<List<Float>, List<Float>, List<String>> {
        val calendar = Calendar.getInstance()
        val now = System.currentTimeMillis()

        return when (filter) {
            TimeFilter.WEEK -> {
                val income = MutableList(7) { 0f }
                val expense = MutableList(7) { 0f }
                val labels = MutableList(7) { "" }
                for (i in 6 downTo 0) {
                    calendar.timeInMillis = now - (i * 24L * 60 * 60 * 1000)
                    labels[6 - i] = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time).uppercase()
                }
                incomeTx.forEach { tx ->
                    val diffDays = ((now - tx.createdAt) / (1000 * 60 * 60 * 24)).toInt()
                    if (diffDays in 0..6) income[6 - diffDays] += tx.amount.toFloat()
                }
                expenseTx.forEach { tx ->
                    val diffDays = ((now - tx.createdAt) / (1000 * 60 * 60 * 24)).toInt()
                    if (diffDays in 0..6) expense[6 - diffDays] += tx.amount.toFloat()
                }
                Triple(income, expense, labels)
            }
            TimeFilter.MONTH -> {
                val income = MutableList(4) { 0f }
                val expense = MutableList(4) { 0f }
                val labels = listOf("WK 1", "WK 2", "WK 3", "WK 4")
                incomeTx.forEach { tx ->
                    val weekIdx = ((now - tx.createdAt) / (1000 * 60 * 60 * 24) / 7).toInt()
                    if (weekIdx in 0..3) income[3 - weekIdx] += tx.amount.toFloat()
                }
                expenseTx.forEach { tx ->
                    val weekIdx = ((now - tx.createdAt) / (1000 * 60 * 60 * 24) / 7).toInt()
                    if (weekIdx in 0..3) expense[3 - weekIdx] += tx.amount.toFloat()
                }
                Triple(income, expense, labels)
            }
            TimeFilter.YEAR, TimeFilter.CUSTOM -> {
                // Determine buckets based on date difference
                val diffDays = ((endTime - startTime) / (1000 * 60 * 60 * 24)).toInt()
                val buckets = when {
                    diffDays <= 14 -> diffDays.coerceAtLeast(1) // Daily
                    diffDays <= 60 -> (diffDays / 7).coerceAtLeast(1) // Weekly
                    else -> 6 // Max 6 months to prevent graph overcrowding
                }

                val income = MutableList(buckets) { 0f }
                val expense = MutableList(buckets) { 0f }
                val labels = MutableList(buckets) { "P${it+1}" } // generic labels for custom

                val bucketDuration = (endTime - startTime) / buckets

                incomeTx.forEach { tx ->
                    val bucketIdx = ((tx.createdAt - startTime) / bucketDuration).toInt().coerceIn(0, buckets - 1)
                    income[bucketIdx] += tx.amount.toFloat()
                }
                expenseTx.forEach { tx ->
                    val bucketIdx = ((tx.createdAt - startTime) / bucketDuration).toInt().coerceIn(0, buckets - 1)
                    expense[bucketIdx] += tx.amount.toFloat()
                }
                Triple(income, expense, labels)
            }
        }
    }

    private suspend fun getTransactionsSuspend(uid: String): List<Transaction> {
        val snapshot = db.collection("users").document(uid).collection("transactions").get().await()
        return snapshot.documents.mapNotNull { it.toObject(Transaction::class.java)?.copy(id = it.id) }
    }
}