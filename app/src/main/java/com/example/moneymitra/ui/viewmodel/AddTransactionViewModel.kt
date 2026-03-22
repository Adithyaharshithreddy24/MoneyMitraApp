package com.example.moneymitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AddTransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun addTransaction(
        name : String,
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
                    "name" to name,
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
    suspend fun getFirstAccount(): Pair<String, String>? {

        if (uid.isBlank()) return null

        val snapshot = db.collection("users")
            .document(uid)
            .collection("accounts")
            .limit(1)
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull() ?: return null

        val accountId = doc.id
        val accountLabel = doc.getString("accType") +" | "+ doc.getString("bankName")

        return Pair(accountId, accountLabel)
    }
    suspend fun addTransactionSuspend(
        name: String,
        amount: Double,
        type: String,
        category: String,
        note: String
    ) {
        if (uid.isBlank()) return

        val (accountId, accountLabel) = getFirstAccount() ?: return

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

            // update balance
            transaction.update(accountRef, "balance", newBalance)

            // save transaction
            transaction.set(
                txRef,
                mapOf(
                    "id" to txRef.id,
                    "name" to name,
                    "amount" to amount,
                    "type" to type,
                    "category" to category,
                    "accountId" to accountId,
                    "accountLabel" to accountLabel,
                    "note" to note,
                    "createdAt" to System.currentTimeMillis()
                )
            )
        }.await()
    }
}
