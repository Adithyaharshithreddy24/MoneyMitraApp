package com.example.moneymitra.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

data class ExpenseCategory(
    val name: String,
    val amount: Double,
    val color: Color
)

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    /* ---------------- USER ---------------- */

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _upiId = MutableStateFlow("")
    val upiId: StateFlow<String> = _upiId

    /* ---------------- BALANCE ---------------- */

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance

    /* ---------------- EXPENSES ---------------- */

    private val _categories =
        MutableStateFlow<List<ExpenseCategory>>(emptyList())
    val categories: StateFlow<List<ExpenseCategory>> = _categories

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense

    /* ---------------- BUDGET ---------------- */

    private val _budget = MutableStateFlow<Double?>(null)
    val budget: StateFlow<Double?> = _budget

    private val _budgetProgress = MutableStateFlow(0f)
    val budgetProgress: StateFlow<Float> = _budgetProgress

    /* ---------------- CURRENT MONTH RANGE ---------------- */

    private val startOfMonth: Long
    private val endOfMonth: Long

    init {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        startOfMonth = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)

        endOfMonth = calendar.timeInMillis

        fetchUserData()
        fetchAccounts()
        fetchTransactions()
        fetchBudget()
    }

    /* ---------------- USER DATA ---------------- */

    private fun fetchUserData() {

        userId?.let { uid ->

            db.collection("users")
                .document(uid)
                .addSnapshotListener { snapshot, _ ->

                    val first =
                        snapshot?.getString("firstName") ?: ""

                    val upi =
                        snapshot?.getString("upiid") ?: ""

                    _name.value = "$first".trim()
                    _upiId.value = upi
                }
        }
    }

    /* ---------------- ACCOUNTS BALANCE ---------------- */

    private fun fetchAccounts() {

        userId?.let { uid ->

            db.collection("users")
                .document(uid)
                .collection("accounts")
                .addSnapshotListener { snapshot, _ ->

                    var total = 0.0

                    snapshot?.documents?.forEach {

                        total +=
                            it.getDouble("balance") ?: 0.0
                    }

                    _balance.value = total
                }
        }
    }

    /* ---------------- FETCH TRANSACTIONS ---------------- */

    private fun fetchTransactions() {

        userId?.let { uid ->

            db.collection("users")
                .document(uid)
                .collection("transactions")
                .whereEqualTo("type", "EXPENSE")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) return@addSnapshotListener

                    val map = mutableMapOf<String, Double>()

                    snapshot?.documents?.forEach { doc ->

                        val createdAt =
                            doc.getLong("createdAt") ?: 0L

                        /* 🔹 FILTER CURRENT MONTH */

                        if (createdAt in startOfMonth until endOfMonth) {

                            val category =
                                doc.getString("category") ?: "Other"

                            val amount =
                                doc.getDouble("amount") ?: 0.0

                            map[category] =
                                map.getOrDefault(category, 0.0) + amount
                        }
                    }

                    val categoryList =
                        map.map { (name, amount) ->

                            ExpenseCategory(
                                name = name,
                                amount = amount,
                                color = getColorForCategory(name)
                            )
                        }

                    val sorted =
                        categoryList
                            .filter { it.amount > 0 }
                            .sortedByDescending { it.amount }

                    _categories.value = sorted

                    val total =
                        sorted.sumOf { it.amount }

                    _totalExpense.value = total

                    updateBudgetProgress()
                }
        }
    }

    /* ---------------- FETCH BUDGET ---------------- */

    private fun fetchBudget() {

        val calendar = Calendar.getInstance()

        val monthId =
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}"

        userId?.let { uid ->

            db.collection("users")
                .document(uid)
                .collection("budgets")
                .document(monthId)
                .addSnapshotListener { snapshot, _ ->

                    if (snapshot != null && snapshot.exists()) {

                        _budget.value =
                            snapshot.getDouble("amount")

                    } else {

                        _budget.value = null
                    }

                    updateBudgetProgress()
                }
        }
    }

    /* ---------------- SET BUDGET ---------------- */

    fun setBudget(amount: Double) {

        val uid = userId ?: return

        val calendar = Calendar.getInstance()

        val monthId =
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}"

        val data = mapOf(
            "amount" to amount,
            "month" to monthId
        )

        db.collection("users")
            .document(uid)
            .collection("budgets")
            .document(monthId)
            .set(data)
            .addOnSuccessListener {

                _budget.value = amount
                updateBudgetProgress()
            }
    }

    fun updateBudget(amount: Double) {
        setBudget(amount)
    }

    /* ---------------- BUDGET PROGRESS ---------------- */

    private fun updateBudgetProgress() {

        val budgetValue = _budget.value
        val expense = _totalExpense.value

        if (budgetValue != null && budgetValue > 0) {

            _budgetProgress.value =
                (expense / budgetValue)
                    .toFloat()
                    .coerceIn(0f, 1f)

        } else {

            _budgetProgress.value = 0f
        }
    }

    /* ---------------- CATEGORY COLORS ---------------- */

    private fun getColorForCategory(name: String): Color {

        return when (name.lowercase()) {

            "food" -> Color(0xFFFF7043)
            "transport" -> Color(0xFF26A69A)
            "medicine" -> Color(0xFFAB47BC)
            "shopping" -> Color(0xFF42A5F5)
            "entertainment" -> Color(0xFFEF5350)
            "bills" -> Color(0xFFFFCA28)
            "sport" -> Color(0xFF5C6BC0)
            "income" -> Color(0xFF8D6E63)
            "others" -> Color(0xFF78909C)

            else -> Color(0xFF7E57C2)
        }
    }
}