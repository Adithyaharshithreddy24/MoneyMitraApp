package com.example.moneymitra.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createUserIfNotExists(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
            ?: return onError("User not logged in")

        val ref = db.collection("users").document(user.uid)

        ref.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    onSuccess()
                } else {
                    ref.set(
                        mapOf(
                            "uid" to user.uid,
                            "email" to user.email,
                            "profileCompleted" to false,
                            "createdAt" to System.currentTimeMillis()
                        ),
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        onSuccess()
                    }.addOnFailureListener {
                        onError(it.message ?: "User creation failed")
                    }
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Firestore read failed")
            }
    }

    fun isProfileCompleted(
        onResult: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
            ?: return onError("User not logged in")

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener {
                onResult(it.getBoolean("profileCompleted") ?: false)
            }
            .addOnFailureListener {
                onError(it.message ?: "Profile fetch failed")
            }
    }
}
