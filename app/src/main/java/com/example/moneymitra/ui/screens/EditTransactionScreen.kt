package com.example.moneymitra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.auth.Transaction
import com.example.moneymitra.ui.viewmodel.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    transaction: Transaction,
    onBack: () -> Unit
) {
    val vm: TransactionsViewModel = viewModel()

    var paidTo by remember { mutableStateOf(transaction.name) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var type by remember { mutableStateOf(transaction.type) }
    var category by remember { mutableStateOf(transaction.category) }
    var note by remember { mutableStateOf(transaction.note) }
    var accountLabel by remember { mutableStateOf(transaction.accountLabel) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* ---- PAID TO / RECEIVED FROM ---- */
            OutlinedTextField(
                value = paidTo,
                onValueChange = { paidTo = it },
                label = {
                    Text(
                        if (type == "EXPENSE") "Paid To"
                        else "Received From"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            /* ---- AMOUNT ---- */
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Transaction Type",
                value = type,
                options = listOf("EXPENSE", "INCOME"),
                onSelected = { type = it }
            )

            /* ---- CATEGORY ---- */
            DropdownField(
                label = "Category",
                value = category,
                options = listOf(
                    "Shopping", "Medicine", "Sport", "Food",
                    "Transport", "Entertainment", "Bills",
                    "Income", "Others"
                ),
                onSelected = { category = it }
            )

            /* ---- ACCOUNT ---- */
            DropdownField(
                label = "Account",
                value = accountLabel,
                options = listOf(accountLabel), // replace later with real accounts list
                onSelected = { accountLabel = it }
            )


            /* ---- NOTE ---- */
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            /* ---- UPDATE BUTTON ---- */
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    vm.update(
                        oldTx = transaction,
                        newTx = transaction.copy(
                            name = paidTo,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            type = type,
                            category = category,
                            note = note
                        )
                    )
                    onBack()
                }
            ) {
                Text("Update Transaction")
            }
        }
    }
}
