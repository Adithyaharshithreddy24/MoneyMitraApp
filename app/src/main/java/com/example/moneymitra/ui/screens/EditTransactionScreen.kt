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

    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var category by remember { mutableStateOf(transaction.category) }
    var type by remember { mutableStateOf(transaction.type) }

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
        ) {

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Row {
                FilterChip(
                    selected = type == "DEBIT",
                    onClick = { type = "DEBIT" },
                    label = { Text("Expense") }
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = type == "CREDIT",
                    onClick = { type = "CREDIT" },
                    label = { Text("Income") }
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    vm.update(
                        oldTx = transaction,
                        newTx = transaction.copy(
                            amount = amount.toDouble(),
                            category = category,
                            type = type
                        )
                    )

                    onBack()
                }
            ) {
                Text("UPDATE")
            }
        }
    }
}
