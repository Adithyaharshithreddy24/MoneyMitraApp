package com.example.moneymitra.auth

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

fun sendPasswordReset(
    context: Context,
    email: String
) {
    if (email.isBlank()) {
        Toast.makeText(
            context,
            "Please enter your email",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    FirebaseAuth.getInstance()
        .sendPasswordResetEmail(email)
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "Password reset email sent",
                Toast.LENGTH_LONG
            ).show()
        }
        .addOnFailureListener {
            Toast.makeText(
                context,
                it.message ?: "Error sending reset email",
                Toast.LENGTH_SHORT
            ).show()
        }
}
