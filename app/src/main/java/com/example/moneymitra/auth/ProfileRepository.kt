package com.example.moneymitra.auth

import com.google.firebase.firestore.FirebaseFirestore

object ProfileRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveProfile(
        uid: String,
        data: Map<String, Any>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("users")
            .document(uid)
            .set(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to save profile") }
    }
}
