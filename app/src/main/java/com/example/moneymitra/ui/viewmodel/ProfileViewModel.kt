package com.example.moneymitra.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.moneymitra.auth.AccountRepository
import com.example.moneymitra.data.model.Account
import com.example.moneymitra.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val accountRepo = AccountRepository()

    var user by mutableStateOf(User())
        private set

    var accounts by mutableStateOf<List<Account>>(emptyList())
        private set

    init {
        loadUser()
        loadAccounts()
    }

    /** 🔒 Load ONLY logged-in user's details */
    private fun loadUser() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                user = doc.toObject(User::class.java) ?: User()
            }
    }
    val fullName: String
        get() = listOf(user.firstName.trim()," ", user.lastName.trim())
            .filter { it.isNotBlank() }
            .joinToString(" ")

    private fun loadAccounts() {
        accountRepo.fetchAccounts {
            accounts = it
        }
    }
}
