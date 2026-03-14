package com.example.moneymitra.ui.navigation

import android.R.attr.defaultValue
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.moneymitra.R
import com.example.moneymitra.auth.*
import com.example.moneymitra.ui.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.example.moneymitra.auth.InstallStateManager
import com.example.moneymitra.data.model.Response
import com.example.moneymitra.ui.viewmodel.TransactionsViewModel

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
    var selectedNotification by remember { mutableStateOf<Response?>(null) }
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

                            val user = FirebaseAuth.getInstance().currentUser

                            if (user != null && user.isEmailVerified) {

                                UserRepository.createUserIfNotExists(
                                    onSuccess = {
                                        navController.navigate("authCheck") {
                                            popUpTo("login") { inclusive = true }
                                        }
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

                onSignupClick = { email, password, _ ->

                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                val user = FirebaseAuth.getInstance().currentUser

                                user?.sendEmailVerification()
                                    ?.addOnSuccessListener {

                                        Toast.makeText(
                                            activity,
                                            "Verification email sent. Please verify before logging in.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        FirebaseAuth.getInstance().signOut()

                                        navController.navigate("login") {
                                            popUpTo("signup") { inclusive = true }
                                        }
                                    }
                                    ?.addOnFailureListener {

                                        Toast.makeText(
                                            activity,
                                            "Failed to send verification email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                            } else {

                                Toast.makeText(
                                    activity,
                                    task.exception?.message ?: "Signup failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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
                onManual = {navController.navigate("addTransaction")},
                onScan = {navController.navigate("scanReceipt")},
                onUpload = {navController.navigate("uploadReceipt") },
                onNotificationClick = {navController.navigate("notifications")},
                onTransactionClick = {navController.navigate("transactions")},
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
        composable("transactions") {
            TransactionsScreen(
                onBack = { navController.popBackStack() },
                onEdit = {tx->navController.navigate("editTransaction/${tx.id}")}
            )
        }


        composable(
            route = "addTransaction?name={name}&amount={amount}&category={category}&note={note}",
            arguments = listOf(
                navArgument("name") { defaultValue = "" },
                navArgument("amount") { defaultValue = "0" },
                navArgument("category") { defaultValue = "Food" },
                navArgument("note") { defaultValue = "" }
            )
        ) { backStackEntry ->

            AddTransactionScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                scannedName = backStackEntry.arguments?.getString("name"),
                scannedAmount = backStackEntry.arguments?.getString("amount"),
                scannedCategory = backStackEntry.arguments?.getString("category"),
                scannedNote = backStackEntry.arguments?.getString("note")
            )
        }
        composable("editTransaction/{txId}") { backStack ->
            val txId = backStack.arguments?.getString("txId") ?: return@composable
            val vm: TransactionsViewModel = viewModel()

            var tx by remember { mutableStateOf<Transaction?>(null) }
            var error by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(txId) {
                vm.getTransaction(
                    txId = txId,
                    onSuccess = { tx = it },
                    onError = { error = it }
                )
            }

            when {
                error != null -> {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                tx == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    EditTransactionScreen(
                        transaction = tx!!,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
        composable("scanReceipt") {
            ScanReceiptScreen(navController)
        }
        composable("uploadReceipt") {
            UploadReceiptScreen(navController)
        }
        composable("notifications") {
            NotificationScreen(
                onBack = { navController.popBackStack() },
                onEdit = { notification ->

                    selectedNotification = notification

                    navController.navigate("editNotification")
                }
            )
        }
        composable("editNotification") {

            selectedNotification?.let {

                EditNotificationScreen(
                    notification = it,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )

            }
        }
    }

}
