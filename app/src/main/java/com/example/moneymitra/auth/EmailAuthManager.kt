package com.example.moneymitra.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmailAuthManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun signInWithEmailOrUsername(
        input: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanInput = input.trim().lowercase()

        if (cleanInput.isBlank() || password.isBlank()) {
            onError("Email / Username and password cannot be empty")
            return
        }

        // EMAIL LOGIN
        if (cleanInput.contains("@")) {
            auth.signInWithEmailAndPassword(cleanInput, password)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener {
                    onError(it.message ?: "Login failed")
                }
        }
        // USERNAME LOGIN
        else {
            db.collection("users")
                .whereEqualTo("username", cleanInput)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        onError("Username not found")
                        return@addOnSuccessListener
                    }

                    val email = snapshot.documents[0].getString("email")
                    if (email.isNullOrBlank()) {
                        onError("Email not linked with username")
                        return@addOnSuccessListener
                    }

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener {
                            onError("Invalid password")
                        }
                }
                .addOnFailureListener {
                    onError("Unable to login. Try again.")
                }
        }
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
}
