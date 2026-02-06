package com.example.moneymitra.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source

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

    fun isUsernameAvailable(
        username: String,
        onResult: (Boolean) -> Unit
    ) {
        val normalized = username.trim().lowercase()

        db.collection("users")
            .whereEqualTo("username", normalized)
            .get(Source.SERVER)
            .addOnSuccessListener { snapshot ->
                Log.d("USERNAME_CHECK", "Docs found = ${snapshot.size()}")
                onResult(snapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                Log.e("USERNAME_CHECK", "FAILED", e)
                // ⛔ DO NOT MARK AS USED ON FAILURE
                onResult(true)
            }
    }

}
