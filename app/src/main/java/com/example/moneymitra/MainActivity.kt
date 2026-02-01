package com.example.moneymitra

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.example.moneymitra.auth.GoogleAuthManager
import com.example.moneymitra.auth.EmailAuthManager
import android.widget.Toast


import com.example.moneymitra.ui.screens.HomeScreen
import com.example.moneymitra.ui.screens.LoginScreen
import com.example.moneymitra.ui.theme.MoneyMitraTheme
import com.example.moneymitra.utils.sendMail
import com.example.moneymitra.auth.sendPasswordReset


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authManager = GoogleAuthManager(
            this,
            getString(R.string.default_web_client_id)
        )
        val emailAuthManager = EmailAuthManager()

        setContent {
            MoneyMitraTheme {

                var isLoggedIn by remember {
                    mutableStateOf(authManager.auth.currentUser != null)
                }

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        authManager.handleSignInResult(result.data) {
                            isLoggedIn = true
                        }
                    }
                }

                if (!isLoggedIn) {
                    LoginScreen(
                        onSignInClick = { email, password ->
                            emailAuthManager.signInWithEmail(
                                email = email,
                                password = password,
                                onSuccess = {
                                    isLoggedIn = true
                                },
                                onError = { message ->
                                    Toast.makeText(
                                        this,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        onGoogleClick = {
                            launcher.launch(
                                authManager.googleSignInClient.signInIntent
                            )
                        },
                        onForgotPassword = {email -> sendPasswordReset(this, email)},
                        onSignUpClick = { }
                    )
                } else {
                    HomeScreen(
                        onSendMail = { sendMail(this) },
                        onLogout = {
                            authManager.auth.signOut()
                            authManager.googleSignInClient.signOut()
                            isLoggedIn = false
                        }
                    )
                }
            }
        }
    }
}
