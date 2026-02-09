package com.example.moneymitra.ui.navigation

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.moneymitra.R
import com.example.moneymitra.auth.*
import com.example.moneymitra.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.example.moneymitra.auth.InstallStateManager

@Composable
fun AppNavHost(activity: Activity) {

    val navController = rememberNavController()
    val installStateManager = remember {
        InstallStateManager(activity)
    }
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        if (installStateManager.isFirstLaunch()) {
            FirebaseAuth.getInstance().signOut()
        }
    }

    val googleAuth = GoogleAuthManager(
        activity,
        activity.getString(R.string.default_web_client_id)
    )
    val emailAuth = EmailAuthManager()

    /* ---------------- GOOGLE SIGN-IN ---------------- */
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

    /* ---------------- START DESTINATION ---------------- */
    val startDestination =
        if (FirebaseAuth.getInstance().currentUser == null)
            "login"
        else
            "authCheck"


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        /* ======================================================
           AUTH CHECK
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
                    emailAuth.signInWithEmailOrUsername(
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
                onForgotPassword = {email -> sendPasswordReset(context ,email)},
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

                            // 🔥 LOGIN AFTER VERIFICATION
                            FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(e, p)
                                .addOnSuccessListener {

                                    // 🔥 CREATE FIRESTORE USER (EMAIL SAVED HERE)
                                    UserRepository.createUserIfNotExists(
                                        onSuccess = {
                                            Toast.makeText(
                                                activity,
                                                "Account created. Complete profile.",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            navController.navigate("authCheck") {
                                                popUpTo("signup") { inclusive = true }
                                            }
                                        },
                                        onError = {
                                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
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
        composable("editProfile") {
            EditProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProfileSaved = {
                    navController.navigate("profile") {
                        popUpTo("editProfile") { inclusive = true }
                    }
                }
            )
        }

        /* ---------------- HOME ---------------- */
        composable("home") {
            HomeScreen(
                onProfileClick = {
                    navController.navigate("profile")
                },
                onHomeClick = {},
                onGridClick = {},
                onAddClick = {},
                onNotificationClick = {},
                onTransactionClick = {},
                onChitFunds = {},
                onGoals = {},
                onLoans = {}
            )
        }

        /* ---------------- PROFILE ---------------- */
        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onEditProfile = { navController.navigate("editProfile") },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    googleAuth.googleSignInClient.signOut()

                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
    }

}
