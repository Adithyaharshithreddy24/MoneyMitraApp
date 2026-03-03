package com.example.moneymitra.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExpenseCategory(
    val name: String,
    val amount: Double,
    val color: Color
)

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance

    private val _categories =
        MutableStateFlow<List<ExpenseCategory>>(emptyList())
    val categories: StateFlow<List<ExpenseCategory>> = _categories

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    init {
        fetchAccounts()
        fetchTransactions()
    }

    private fun fetchAccounts() {
        userId?.let { uid ->

            db.collection("users")
                .document(uid)
                .collection("accounts")
                .addSnapshotListener { snapshot, _ ->

                    var total = 0.0

                    snapshot?.documents?.forEach {
                        total += it.getDouble("balance") ?: 0.0
                    }

                    _balance.value = total
                }
        }
    }

    private fun fetchTransactions() {
        userId?.let { uid ->

            db.collection("users")
                .document(uid)
                .collection("transactions")
                .whereEqualTo("type", "EXPENSE")
                .addSnapshotListener { snapshot, _ ->

                    val map = mutableMapOf<String, Double>()

                    snapshot?.documents?.forEach {
                        val category =
                            it.getString("category") ?: "Other"

                        val amount =
                            it.getDouble("amount") ?: 0.0

                        map[category] =
                            map.getOrDefault(category, 0.0) + amount
                    }

                    val categoryList = map.map { (name, amount) ->
                        ExpenseCategory(
                            name = name,
                            amount = amount,
                            color = getColorForCategory(name)
                        )
                    }

                    _categories.value =
                        categoryList.filter { it.amount > 0 }

                    _totalExpense.value =
                        categoryList.sumOf { it.amount }
                }
        }
    }

    private fun getColorForCategory(name: String): Color {
        return when (name.lowercase()) {

            "food" ->
                Color(0xFFFF7043)     // Orange

            "transport" ->
                Color(0xFF26A69A)     // Teal

            "medicine" ->
                Color(0xFFAB47BC)     // Purple

            "shopping" ->
                Color(0xFF42A5F5)     // Blue

            "entertainment" ->
                Color(0xFFEF5350)     // Red

            "bills" ->
                Color(0xFFFFCA28)     // Yellow

            "sport" ->
                Color(0xFF5C6BC0)     // Indigo

            "income" ->
                Color(0xFF8D6E63)     // Brown

            "others" ->
                Color(0xFF78909C)     // Grey

            else ->
                Color(0xFF7E57C2)     // Default fallback
        }
    }
}