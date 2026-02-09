package com.example.moneymitra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.auth.Transaction
import com.example.moneymitra.ui.viewmodel.TransactionsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onBack: () -> Unit,
    onEdit: (tx: Transaction) -> Unit
) {
    val vm: TransactionsViewModel = viewModel()
    val list by vm.transactions.collectAsState()

    var expandedId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.loadTransactions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(list, key = { it.id }) { tx ->
                TransactionExpandableCard(
                    tx = tx,
                    expanded = expandedId == tx.id,
                    onClick = {
                        expandedId =
                            if (expandedId == tx.id) null else tx.id
                    },
                    onDelete = { vm.delete(tx) },
                    onEdit = { onEdit(tx)
                        // later you can open edit bottom sheet here
                    }
                )
            }
        }
    }
}
@Composable
fun TransactionExpandableCard(
    tx: Transaction,
    expanded: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(14.dp)) {

            // 🔹 ALWAYS VISIBLE (Collapsed view)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = tx.category,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = tx.type,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = "₹${tx.amount}",
                    fontWeight = FontWeight.Bold
                )
            }

            // 🔹 EXPANDED CONTENT
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Divider()

                    DetailRow("Account", tx.accountLabel)
                    DetailRow(
                        "Date",
                        SimpleDateFormat(
                            "dd MMM yyyy, hh:mm a",
                            Locale.getDefault()
                        ).format(Date(tx.createdAt))
                    )

                    if (tx.note.isNotBlank()) {
                        DetailRow("Note", tx.note)
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onEdit) {
                            Text("EDIT")
                        }
                        TextButton(
                            onClick = onDelete,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("DELETE")
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium
        )
    }
}
