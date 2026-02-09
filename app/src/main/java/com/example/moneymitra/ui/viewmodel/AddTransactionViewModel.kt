package com.example.moneymitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddTransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun addTransaction(
        amount: Double,
        type: String, // INCOME / EXPENSE
        category: String,
        accountId: String,
        accountLabel: String,
        note: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (uid.isBlank()) {
            onError("User not logged in")
            return
        }

        val txRef = db.collection("users")
            .document(uid)
            .collection("transactions")
            .document()

        val accountRef = db.collection("users")
            .document(uid)
            .collection("accounts")
            .document(accountId)

        db.runTransaction { transaction ->

            val accSnap = transaction.get(accountRef)
            val currentBalance = accSnap.getDouble("balance") ?: 0.0

            val newBalance =
                if (type == "INCOME") currentBalance + amount
                else currentBalance - amount

            // update account balance
            transaction.update(accountRef, "balance", newBalance)

            // save transaction
            transaction.set(
                txRef,
                mapOf(
                    "amount" to amount,
                    "type" to type,
                    "category" to category,
                    "accountId" to accountId,
                    "accountLabel" to accountLabel,
                    "note" to note,
                    "createdAt" to System.currentTimeMillis()
                )
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.message ?: "Transaction failed")
        }
    }
}
