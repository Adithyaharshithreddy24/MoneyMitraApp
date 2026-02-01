package com.example.moneymitra.auth

import com.google.firebase.auth.FirebaseAuth

class EmailAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Email and password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Login failed")
            }
    }
}
