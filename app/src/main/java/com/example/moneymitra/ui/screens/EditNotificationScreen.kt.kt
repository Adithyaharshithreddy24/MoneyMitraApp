package com.example.moneymitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.repository.Transaction
import com.example.moneymitra.data.model.Response
import com.example.moneymitra.ui.viewmodel.TransactionsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNotificationScreen(
    notification: Response,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {

    val vm: TransactionsViewModel = viewModel()
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    /* ---------- FORM STATE ---------- */

    var name by remember { mutableStateOf(notification.name) }
    var amount by remember { mutableStateOf(notification.amount.toString()) }
    var category by remember { mutableStateOf(notification.category) }
    var note by remember { mutableStateOf(notification.note) }

    var type by remember {
        mutableStateOf(
            if (notification.type == "DEBIT") "EXPENSE"
            else "INCOME"
        )
    }

    /* ---------- ACCOUNTS ---------- */

    var accounts by remember {
        mutableStateOf<List<Pair<String, String>>>(emptyList())
    }

    var accountId by remember { mutableStateOf("") }
    var accountLabel by remember { mutableStateOf("") }

    /* ---------- LOADING ---------- */

    var loading by remember { mutableStateOf(false) }

    /* ---------- LOAD ACCOUNTS ---------- */

    LaunchedEffect(Unit) {

        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("users")
            .document(uid)
            .collection("accounts")
            .get()
            .addOnSuccessListener { snapshot ->

                val list = snapshot.documents.map { doc ->

                    val label =
                        "${doc.getString("accName")} | ${doc.getString("bankName")}"

                    Pair(doc.id, label)
                }

                accounts = list

                // DEFAULT ACCOUNT = FIRST ACCOUNT
                if (list.isNotEmpty()) {
                    accountId = list.first().first
                    accountLabel = list.first().second
                }
            }
    }

    /* ---------- UI ---------- */
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) {padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {


            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            /* AMOUNT */

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            /* TYPE */

            DropdownField(
                label = "Transaction Type",
                value = type,
                options = listOf("EXPENSE", "INCOME"),
                onSelected = { type = it }
            )

            Spacer(Modifier.height(12.dp))

            /* CATEGORY */

            DropdownField(
                label = "Category",
                value = category,
                options = listOf(
                    "Shopping",
                    "Medicine",
                    "Sport",
                    "Food",
                    "Transport",
                    "Entertainment",
                    "Bills",
                    "Income",
                    "Others"
                ),
                onSelected = { category = it }
            )

            Spacer(Modifier.height(12.dp))

            /* ACCOUNT */

            DropdownField(
                label = "Account",
                value = accountLabel,
                options = accounts.map { it.second },
                onSelected = { selected ->

                    accountLabel = selected
                    accountId = accounts.first { it.second == selected }.first

                }
            )

            Spacer(Modifier.height(12.dp))

            /* NOTE */

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            /* SAVE BUTTON */

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = {

                    if (accounts.isEmpty()) {
                        Toast.makeText(
                            context,
                            "No account found",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    loading = true

                    // 🔹 Always use first account if not selected
                    if (accountId.isEmpty()) {
                        accountId = accounts.first().first
                        accountLabel = accounts.first().second
                    }

                    val transaction = Transaction(
                        accountId = accountId,
                        accountLabel = accountLabel,
                        name = name,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = category,
                        type = type,
                        note = note,
                        createdAt = notification.createdAt
                    )

                    vm.saveFromNotification(
                        notificationId = notification.id,
                        transaction = transaction
                    )

                    loading = false

                    Toast.makeText(
                        context,
                        "Transaction saved",
                        Toast.LENGTH_SHORT
                    ).show()

                    onSaved()
                }
            ) {

                if (loading) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                } else {

                    Text("SAVE TRANSACTION")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}