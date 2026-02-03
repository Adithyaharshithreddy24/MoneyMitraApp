package com.example.moneymitra.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createUserIfNotExists(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onError("User not logged in")
            return
        }

        val userRef = db.collection("users").document(user.uid)

        userRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // User already exists
                    onSuccess()
                } else {
                    // Create user document
                    val data = hashMapOf(
                        "uid" to user.uid,
                        "email" to user.email,
                        "provider" to "email",
                        "createdAt" to System.currentTimeMillis()
                    )

                    userRef.set(data)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener {
                            onError(it.message ?: "Firestore write failed")
                        }
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Firestore read failed")
            }
    }
}
