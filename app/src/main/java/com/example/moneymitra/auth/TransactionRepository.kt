package com.example.moneymitra.auth

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Transaction(
    val id: String = "",
    val accountId: String = "",
    val amount: Double = 0.0,
    val type: String = "DEBIT",
    val accountLabel: String = "",// CREDIT / DEBIT
    val category: String = "",
    val customCategory: String? = null,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

object TransactionRepository {

    private val db = FirebaseFirestore.getInstance()

    /* ---------- FETCH ALL TRANSACTIONS ---------- */
    fun getTransactions(
        uid: String,
        onSuccess: (List<Transaction>) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .collection("transactions")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Transaction::class.java)
                        ?.copy(id = doc.id)
                }
                onSuccess(list)
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to load transactions")
            }
    }

    /* ---------- GET SINGLE TRANSACTION ---------- */
    fun getTransactionById(
        uid: String,
        txId: String,
        onSuccess: (Transaction) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .collection("transactions")
            .document(txId)
            .get()
            .addOnSuccessListener { doc ->
                val tx = doc.toObject(Transaction::class.java)
                    ?.copy(id = doc.id)
                if (tx != null) onSuccess(tx)
                else onError("Transaction not found")
            }
            .addOnFailureListener {
                onError(it.message ?: "Failed to load transaction")
            }
    }

    /* ---------- ADD TRANSACTION ---------- */
    fun addTransaction(
        uid: String,
        tx: Transaction,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val accRef = db.collection("users")
            .document(uid)
            .collection("accounts")
            .document(tx.accountId)

        val txRef = db.collection("users")
            .document(uid)
            .collection("transactions")
            .document()

        db.runTransaction { tr ->
            val accSnap = tr.get(accRef)
            val balance = accSnap.getDouble("balance") ?: 0.0

            val updatedBalance =
                if (tx.type == "CREDIT") balance + tx.amount
                else balance - tx.amount

            tr.update(accRef, "balance", updatedBalance)
            tr.set(txRef, tx.copy(createdAt = System.currentTimeMillis()))
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.message ?: "Transaction failed")
        }
    }

    /* ---------- DELETE TRANSACTION ---------- */
    fun deleteTransaction(
        uid: String,
        tx: Transaction,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val accRef = db.collection("users")
            .document(uid)
            .collection("accounts")
            .document(tx.accountId)

        val txRef = db.collection("users")
            .document(uid)
            .collection("transactions")
            .document(tx.id)

        db.runTransaction { tr ->
            val accSnap = tr.get(accRef)
            val balance = accSnap.getDouble("balance") ?: 0.0

            val updatedBalance =
                if (tx.type == "CREDIT") balance - tx.amount
                else balance + tx.amount

            tr.update(accRef, "balance", updatedBalance)
            tr.delete(txRef)
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.message ?: "Delete failed")
        }
    }

    /* ---------- UPDATE TRANSACTION ---------- */
    fun updateTransaction(
        uid: String,
        oldTx: Transaction,
        newTx: Transaction,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val accRef = db.collection("users")
            .document(uid)
            .collection("accounts")
            .document(oldTx.accountId)

        val txRef = db.collection("users")
            .document(uid)
            .collection("transactions")
            .document(oldTx.id)

        db.runTransaction { tr ->
            val accSnap = tr.get(accRef)
            val balance = accSnap.getDouble("balance") ?: 0.0

            val rollback =
                if (oldTx.type == "CREDIT") balance - oldTx.amount
                else balance + oldTx.amount

            val finalBalance =
                if (newTx.type == "CREDIT") rollback + newTx.amount
                else rollback - newTx.amount

            tr.update(accRef, "balance", finalBalance)
            tr.set(txRef, newTx.copy(createdAt = oldTx.createdAt))
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError(it.message ?: "Update failed")
        }
    }
}
