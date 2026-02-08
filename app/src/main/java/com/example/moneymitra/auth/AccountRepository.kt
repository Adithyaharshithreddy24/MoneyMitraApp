package com.example.moneymitra.auth

import com.example.moneymitra.data.model.Account
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AccountRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun addAccount(
        account: Account,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onError("User not logged in")
            return
        }

        db.collection("users")
            .document(uid)
            .collection("accounts")
            .add(account)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Firestore error")
            }
    }


    fun fetchAccounts(onResult: (List<Account>) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("accounts")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    onResult(it.toObjects(Account::class.java))
                }
            }
    }
}
