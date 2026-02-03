package com.example.moneymitra.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthManager(
    activity: Activity,
    webClientId: String
) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    fun handleSignInResult(
        data: Intent?,
        onSuccess: () -> Unit
    ) {
        try {
            val account = GoogleSignIn
                .getSignedInAccountFromIntent(data)
                .getResult(Exception::class.java)

            val credential =
                GoogleAuthProvider.getCredential(account.idToken, null)

            auth.signInWithCredential(credential)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener {
                    Toast.makeText(
                        googleSignInClient.applicationContext,
                        "Google login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } catch (e: Exception) {
            Toast.makeText(
                googleSignInClient.applicationContext,
                "Google Sign-In error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
