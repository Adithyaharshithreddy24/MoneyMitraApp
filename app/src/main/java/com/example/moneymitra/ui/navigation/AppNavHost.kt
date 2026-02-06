package com.example.moneymitra.ui.navigation

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.moneymitra.R
import com.example.moneymitra.auth.*
import com.example.moneymitra.ui.screens.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(activity: Activity) {

    val navController = rememberNavController()

    val googleAuth = GoogleAuthManager(
        activity,
        activity.getString(R.string.default_web_client_id)
    )
    val emailAuth = EmailAuthManager()

    /* ---------- GOOGLE SIGN-IN ---------- */
    val googleLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                googleAuth.handleSignInResult(result.data) {
                    UserRepository.createUserIfNotExists(
                        onSuccess = {
                            navController.navigate("authCheck") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = {
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

    /* ---------- START DESTINATION ---------- */
    val startDestination = remember {
        if (FirebaseAuth.getInstance().currentUser != null)
            "authCheck"
        else
            "login"
    }

    NavHost(navController, startDestination = startDestination) {

        /* ======================================================
           AUTH CHECK (VERY IMPORTANT)
           ====================================================== */
        composable("authCheck") {
            val user = FirebaseAuth.getInstance().currentUser

            LaunchedEffect(Unit) {
                if (user == null) {
                    navController.navigate("login") {
                        popUpTo("authCheck") { inclusive = true }
                    }
                } else {
                    UserRepository.isProfileCompleted(
                        onResult = { completed ->
                            navController.navigate(
                                if (completed) "home" else "editProfile"
                            ) {
                                popUpTo("authCheck") { inclusive = true }
                            }
                        },
                        onError = {
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("authCheck") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        /* ---------------- LOGIN ---------------- */
        composable("login") {
            LoginScreen(
                onSignInClick = { email, password ->
                    emailAuth.signInWithEmail(
                        email, password,
                        onSuccess = {
                            navController.navigate("authCheck") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = {
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onGoogleClick = {
                    googleLauncher.launch(
                        googleAuth.googleSignInClient.signInIntent
                    )
                },
                onForgotPassword = {},
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }

        /* ---------------- SIGNUP ---------------- */
        composable("signup") {
            SignupScreen(
                onGoogleClick = {
                    googleLauncher.launch(
                        googleAuth.googleSignInClient.signInIntent
                    )
                },
                onSignupClick = { e, p, _ ->
                    emailAuth.createUserWithEmail(
                        e, p,
                        onVerificationSent = {
                            Toast.makeText(
                                activity,
                                "Verify email and login",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.popBackStack()
                        },
                        onError = {
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onSignInClick = {
                    navController.popBackStack()
                }
            )
        }

        /* ---------------- EDIT PROFILE ---------------- */
        composable(route = "editProfile") {
            EditProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProfileSaved = {
                    navController.navigate("home") {
                        popUpTo("editProfile") { inclusive = true }
                    }
                }
            )
        }


        /* ---------------- HOME ---------------- */
        composable("home") {
            HomeScreen(
                onSendMail = {},
                onEditProfile = {
                    navController.navigate("editProfile") // ✅ FIXED
                },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    googleAuth.googleSignInClient.signOut()

                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
