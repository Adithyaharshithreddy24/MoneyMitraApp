package com.example.moneymitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.ui.viewmodel.AddTransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val vm: AddTransactionViewModel = viewModel()

    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") }
    var category by remember { mutableStateOf("Food") }
    var note by remember { mutableStateOf("") }

    var accountId by remember { mutableStateOf("") }
    var accountLabel by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val accounts = remember { mutableStateListOf<Pair<String, String>>() }

    /* ---------------- LOAD ACCOUNTS ---------------- */
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("accounts")
            .get()
            .addOnSuccessListener {
                accounts.clear()
                it.documents.forEach { doc ->
                    val label =
                        "${doc.getString("accName")} | ${doc.getString("bankName")}"
                    accounts.add(doc.id to label)
                }
                if (accounts.isNotEmpty()) {
                    accountId = accounts[0].first
                    accountLabel = accounts[0].second
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            /* ---------- AMOUNT ---------- */
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- TYPE DROPDOWN ---------- */
            var typeExpanded by remember { mutableStateOf(false) }
            val typeOptions = listOf("EXPENSE", "INCOME")

            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Transaction Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    typeOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                type = it
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* ---------- CATEGORY DROPDOWN ---------- */
            var catExpanded by remember { mutableStateOf(false) }
            val categories = listOf(
                "Shopping", "Medicine", "Sport", "Food",
                "Transport", "Entertainment", "Bills",
                "Income", "Others"
            )

            ExposedDropdownMenuBox(
                expanded = catExpanded,
                onExpandedChange = { catExpanded = !catExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(catExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = catExpanded,
                    onDismissRequest = { catExpanded = false }
                ) {
                    categories.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                category = it
                                catExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* ---------- ACCOUNT DROPDOWN ---------- */
            var accExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = accExpanded,
                onExpandedChange = { accExpanded = !accExpanded }
            ) {
                OutlinedTextField(
                    value = accountLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Account") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(accExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = accExpanded,
                    onDismissRequest = { accExpanded = false }
                ) {
                    accounts.forEach { (id, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                accountId = id
                                accountLabel = label
                                accExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            /* ---------- NOTE ---------- */
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(28.dp))

            /* ---------- SAVE BUTTON (GRADIENT) ---------- */
            Button(
                onClick = {
                    loading = true
                    vm.addTransaction(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = type,
                        category = category,
                        accountId = accountId,
                        accountLabel = accountLabel,
                        note = note,
                        onSuccess = {
                            loading = false
                            onSaved()
                        },
                        onError = {
                            loading = false
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                enabled = !loading &&
                        amount.isNotBlank() &&
                        accountId.isNotBlank()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF000000),
                                    Color(0xFF282B8C)
                                )
                            ),
                            shape = RoundedCornerShape(26.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (loading) "Saving..." else "SAVE TRANSACTION",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}
