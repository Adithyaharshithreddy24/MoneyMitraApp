package com.example.moneymitra.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

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
            .set(
                data,
                SetOptions.merge()   // 🔥 THIS IS THE FIX
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Profile update failed")
            }
    }
}
