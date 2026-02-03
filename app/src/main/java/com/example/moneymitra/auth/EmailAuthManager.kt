package com.example.moneymitra.auth

import com.google.firebase.auth.FirebaseAuth

class EmailAuthManager {

    private val auth = FirebaseAuth.getInstance()

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
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Login failed") }
    }

    fun createUserWithEmail(
        email: String,
        password: String,
        onVerificationSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result.user?.sendEmailVerification()
                    ?.addOnSuccessListener {
                        auth.signOut()
                        onVerificationSent()
                    }
            }
            .addOnFailureListener {
                onError(it.message ?: "Signup failed")
            }
    }

    fun logout() = auth.signOut()

    companion object
}
