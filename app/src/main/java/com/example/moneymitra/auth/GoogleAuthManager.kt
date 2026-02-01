package com.example.moneymitra.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class GoogleAuthManager(
    private val activity: Activity,
    private val webClientId: String
) {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
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
                .addOnSuccessListener {
                    val user = auth.currentUser ?: return@addOnSuccessListener

                    db.collection("users")
                        .document(user.uid)
                        .set(
                            mapOf(
                                "uid" to user.uid,
                                "name" to user.displayName,
                                "email" to user.email
                            )
                        )

                    onSuccess()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        activity,
                        "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        } catch (e: Exception) {
            Toast.makeText(
                activity,
                "Google Sign-In failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
