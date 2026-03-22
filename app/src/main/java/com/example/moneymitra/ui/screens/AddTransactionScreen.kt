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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.ui.viewmodel.AddTransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    scannedName: String? = null,
    scannedAmount: String? = null,
    scannedCategory: String? = null,
    scannedNote: String? = null
) {
    val context = LocalContext.current
    val vm: AddTransactionViewModel = viewModel()
    var paidto by remember { mutableStateOf(scannedName ?: "") }
    var amount by remember { mutableStateOf(scannedAmount ?: "") }
    var category by remember { mutableStateOf(scannedCategory ?: "Food") }
    var note by remember { mutableStateOf(scannedNote ?: "") }
    var type by remember { mutableStateOf("EXPENSE") }

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
                title = {
                    Text(
                        "Add Transaction",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = paidto,
                onValueChange = { paidto = it },
                label = { Text(if (type =="EXPENSE" ) "Paid To" else "Recived From")  },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.outline
                ),
            )

            /* ---------- AMOUNT ---------- */
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.outline
                ),
            )

            /* ---------- TYPE DROPDOWN ---------- */
            DropdownField(
                label = "Transaction Type",
                value = type,
                options = listOf("EXPENSE", "INCOME"),
                onSelected = { type = it }
            )

            /* ---------- CATEGORY DROPDOWN ---------- */
            DropdownField(
                label = "Category",
                value = category,
                options = listOf(
                    "Shopping", "Medicine", "Sport", "Food",
                    "Transport", "Entertainment", "Bills",
                    "Income", "Others" ,"Chits"
                ),
                onSelected = { category = it }
            )

            /* ---------- ACCOUNT DROPDOWN ---------- */
            DropdownField(
                label = "Account",
                value = accountLabel,
                options = accounts.map { it.second },
                onSelected = { selected ->
                    val pair = accounts.find { it.second == selected }
                    accountId = pair?.first ?: ""
                    accountLabel = selected
                }
            )

            /* ---------- NOTE ---------- */
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.outline
                ),
            )

            Spacer(Modifier.height(12.dp))

            /* ---------- SAVE BUTTON ---------- */
            Button(
                onClick = {
                    loading = true
                    vm.addTransaction(
                        name = paidto,
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
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !loading &&
                        paidto.isNotEmpty() &&
                        amount.isNotBlank() &&
                        accountId.isNotBlank()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        "Save Transaction",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/* ---------------- REUSABLE DROPDOWN ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.outline
            ),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}