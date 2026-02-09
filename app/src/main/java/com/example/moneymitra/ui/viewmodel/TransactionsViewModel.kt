package com.example.moneymitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moneymitra.auth.Transaction
import com.example.moneymitra.auth.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionsViewModel : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    fun loadTransactions() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        TransactionRepository.getTransactions(
            uid = uid,
            onSuccess = {
                _transactions.value = it
            },
            onError = {
                _transactions.value = emptyList()
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

}
