package com.example.moneymitra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Loan
import com.example.moneymitra.viewmodel.LoanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
    onBack: () -> Unit,
    onAddLoan: () -> Unit,
    onEditLoan: (Loan) -> Unit
) {

    val vm: LoanViewModel = viewModel()
    val list by vm.loans.collectAsState()

    var expandedId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        vm.loadLoans()
    }

    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loans") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddLoan() }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        containerColor = colors.background
    ) { padding ->

        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No Loans Yet")
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(list, key = { it.id }) { loan ->
                    LoanExpandableCard(
                        loan = loan,
                        expanded = expandedId == loan.id,
                        onClick = {
                            expandedId =
                                if (expandedId == loan.id) null else loan.id
                        },
                        onEditLoan={Loan->
                            onEditLoan(Loan)
                        },
                        onDeleteLoan={
                            loanId -> vm.deleteLoan(loanId)
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun LoanExpandableCard(
    loan: Loan,
    expanded: Boolean,
    onEditLoan: (Loan) -> Unit,
    onDeleteLoan: (String) -> Unit,
    onClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {



        /* MAIN ROW */

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* ICON BOX */

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = loan.loanType.take(1),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = loan.loanType,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )

                Text(
                    text = loan.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "EMI ₹${"%.0f".format(loan.emi)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${loan.principal}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${loan.interestRate}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }

        /* EXPANDED DETAILS */

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                DetailRow("Tenure", "${loan.tenureYears} Years")
                DetailRow("Type", loan.loanType)
                DetailRow("Frequency", loan.repaymentFrequency)

                val date = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date(loan.nextDueDate))

                DetailRow("Next Due", date)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(onClick = { onEditLoan(loan)}) {
                        Text("Edit", color = Color.Green)
                    }

                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text("Delete", color = Color.Red)
                    }
                }
            }
        }
        Divider(
            modifier = Modifier.padding(top = 12.dp),
            color = colors.outlineVariant
        )
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Loan") },
            text = { Text("Are you sure you want to delete this loan?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteLoan(loan.id) // call delete
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}