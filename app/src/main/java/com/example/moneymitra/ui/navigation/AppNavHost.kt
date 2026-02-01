package com.example.moneymitra.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.moneymitra.auth.EmailAuthManager
import com.example.moneymitra.auth.GoogleAuthManager
import com.example.moneymitra.auth.sendPasswordReset
import com.example.moneymitra.ui.screens.HomeScreen
import com.example.moneymitra.ui.screens.LoginScreen
import com.example.moneymitra.ui.screens.SignupScreen
import com.example.moneymitra.utils.sendMail
import com.example.moneymitra.R

@Composable
fun AppNavHost(activity: Activity) {

    val navController = rememberNavController()

    val googleAuthManager = GoogleAuthManager(
        activity,
        activity.getString(R.string.default_web_client_id)
    )
    val emailAuthManager = EmailAuthManager()

    var isLoggedIn by remember {
        mutableStateOf(googleAuthManager.auth.currentUser != null)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            googleAuthManager.handleSignInResult(result.data) {
                isLoggedIn = true
            }
        }
    }

    if (isLoggedIn) {
        HomeScreen(
            onSendMail = { sendMail(activity) },
            onLogout = {
                googleAuthManager.auth.signOut()
                googleAuthManager.googleSignInClient.signOut()
                isLoggedIn = false
            }
        )
    } else {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {

            composable("login") {
                LoginScreen(
                    onSignInClick = { email, password ->
                        emailAuthManager.signInWithEmail(
                            email,
                            password,
                            onSuccess = {
                                isLoggedIn = true
                            },
                            onError = { message ->
                                Toast.makeText(
                                    activity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    onGoogleClick = {
                        launcher.launch(
                            googleAuthManager.googleSignInClient.signInIntent
                        )
                    },
                    onForgotPassword = { email ->
                        sendPasswordReset(activity, email)
                    },
                    onSignUpClick = {
                        navController.navigate("signup")
                    }
                )
            }

            composable("signup") {
                SignupScreen(
                    onGoogleClick = {
                        launcher.launch(
                            googleAuthManager.googleSignInClient.signInIntent
                        )
                    },
                    onSignupClick = { email, password, confirmPassword ->
                        // signup logic will go here
                    },
                    onSignInClick = {
                        navController.navigate("login")
                    }
                )
            }
        }
    }
}
