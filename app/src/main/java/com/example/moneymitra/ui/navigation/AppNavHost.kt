package com.example.moneymitra.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.moneymitra.R
import com.example.moneymitra.auth.EmailAuthManager
import com.example.moneymitra.auth.GoogleAuthManager
import com.example.moneymitra.auth.UserRepository
import com.example.moneymitra.auth.sendPasswordReset
import com.example.moneymitra.ui.screens.HomeScreen
import com.example.moneymitra.ui.screens.LoginScreen
import com.example.moneymitra.ui.screens.SignupScreen
import com.example.moneymitra.utils.sendMail
import com.google.firebase.auth.FirebaseAuth

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

    // 🔐 TEMP STORAGE FOR SIGNUP (UNTIL OTP VERIFIED)
    var signupEmail by remember { mutableStateOf("") }
    var signupPassword by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            googleAuthManager.handleSignInResult(result.data) {
                isLoggedIn = true
            }
        }
    }

    /* ====================== HOME ====================== */
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

        /* ====================== AUTH FLOW ====================== */
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {

            /* ---------------- LOGIN ---------------- */
            composable("login") {
                LoginScreen(
                    onSignInClick = { email, password ->
                        emailAuthManager.signInWithEmail(
                            email,
                            password,
                            onSuccess = {

                                val user = FirebaseAuth.getInstance().currentUser

                                if (user != null && user.isEmailVerified) {

                                    // ✅ WRITE TO FIRESTORE HERE
                                    UserRepository.createUserIfNotExists(
                                        onSuccess = {
                                            isLoggedIn = true
                                        },
                                        onError = { msg ->
                                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                                        }
                                    )

                                } else {
                                    FirebaseAuth.getInstance().signOut()
                                    Toast.makeText(
                                        activity,
                                        "Please verify your email before logging in",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            onError = { message ->
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    ,
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

            /* ---------------- SIGNUP ---------------- */
            composable("signup") {
                SignupScreen(
                    onGoogleClick = {
                        launcher.launch(
                            googleAuthManager.googleSignInClient.signInIntent
                        )
                    },
                    onSignupClick = { email, password, confirmPassword ->

                        if (password != confirmPassword) {
                            Toast.makeText(activity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                            return@SignupScreen
                        }

                        emailAuthManager.createUserWithEmail(
                            email = email,
                            password = password,
                            onVerificationSent = {
                                Toast.makeText(
                                    activity,
                                    "Verification email sent. Please verify and login.",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.popBackStack("login", inclusive = false)
                            },
                            onError = { message ->
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },

                            onSignInClick = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}
