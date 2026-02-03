package com.example.moneymitra.auth

import com.google.firebase.auth.FirebaseAuth

class EmailAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /* --------------------------------------------------
       SIGN IN WITH EMAIL & PASSWORD
       -------------------------------------------------- */
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
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Login failed")
            }
    }

    /* --------------------------------------------------
       SIGN UP (REGISTER) WITH EMAIL & PASSWORD
       CALLED ONLY AFTER OTP VERIFICATION
       -------------------------------------------------- */
    fun createUserWithEmail(
        email: String,
        password: String,
        onVerificationSent: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Email and password cannot be empty")
            return
        }

        if (password.length < 6) {
            onError("Password must be at least 6 characters")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val user = result.user
                if (user == null) {
                    onError("User creation failed")
                    return@addOnSuccessListener
                }

                // ✅ SEND VERIFICATION EMAIL
                user.sendEmailVerification()
                    .addOnSuccessListener {
                        auth.signOut() // important!
                        onVerificationSent()
                    }
                    .addOnFailureListener {
                        onError("Failed to send verification email")
                    }
            }
            .addOnFailureListener {
                onError(it.message ?: "Signup failed")
            }
    }

    /* --------------------------------------------------
       LOGOUT
       -------------------------------------------------- */
    fun logout() {
        auth.signOut()
    }

    /* --------------------------------------------------
       CHECK LOGIN STATE
       -------------------------------------------------- */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /* --------------------------------------------------
       GET CURRENT USER EMAIL (OPTIONAL)
       -------------------------------------------------- */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}
