package com.example.moneymitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moneymitra.auth.Transaction
import com.example.moneymitra.auth.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionsViewModel : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())

    private val _recentTransactions =
        MutableStateFlow<List<Transaction>>(emptyList())

    val recentTransactions: StateFlow<List<Transaction>> =
        _recentTransactions
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun loadTransactions() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        TransactionRepository.getTransactions(
            uid = uid,
            onSuccess = {

                val sorted =
                    it.sortedByDescending { tx -> tx.createdAt }

                _transactions.value = sorted

                // 🔹 Only latest 4 for dashboard
                _recentTransactions.value = sorted.take(4)
            },
            onError = {
                _transactions.value = emptyList()
                _recentTransactions.value = emptyList()
            }
        )
    }

    fun delete(tx: Transaction) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        TransactionRepository.deleteTransaction(
            uid = uid,
            tx = tx,
            onSuccess = { loadTransactions() },
            onError = {}
        )
    }


    fun update(oldTx: Transaction, newTx: Transaction) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        TransactionRepository.updateTransaction(
            uid = uid,
            oldTx = oldTx,
            newTx = newTx,
            onSuccess = { loadTransactions() },
            onError = {}
        )
    }
    fun getTransaction(
        txId: String,
        onSuccess: (Transaction) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        TransactionRepository.getTransactionById(
            uid = uid,
            txId = txId,
            onSuccess = onSuccess,
            onError = onError
        )
    }
    fun saveFromNotification(
        notificationId: String,
        transaction: Transaction
    ) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val db = FirebaseFirestore.getInstance()

        // 🔹 If accountId is empty → fetch first account
        if (transaction.accountId.isEmpty()) {

            db.collection("users")
                .document(uid)
                .collection("accounts")
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->

                    val doc = snapshot.documents.firstOrNull()

                    val accountId = doc?.id ?: ""
                    val accountLabel = doc?.getString("name") ?: "Account"

                    val fixedTransaction = transaction.copy(
                        accountId = accountId,
                        accountLabel = accountLabel
                    )

                    addAndDelete(uid, notificationId, fixedTransaction)
                }

        } else {

            addAndDelete(uid, notificationId, transaction)

        }
    }
    private fun addAndDelete(
        uid: String,
        notificationId: String,
        transaction: Transaction
    ) {

        TransactionRepository.addTransaction(
            uid = uid,
            tx = transaction,
            onSuccess = {

                TransactionRepository.deleteNotification(
                    uid = uid,
                    notificationId = notificationId
                )

                loadTransactions()
            },
            onError = {}
        )
    }

}
