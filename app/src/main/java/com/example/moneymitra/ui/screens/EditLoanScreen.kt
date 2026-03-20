package com.example.moneymitra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Loan
import com.example.moneymitra.utils.calculateEMI
import com.example.moneymitra.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLoanScreen(
    loan: Loan,
    onBack: () -> Unit
) {

    val vm: LoanViewModel = viewModel()

    var name by remember { mutableStateOf(loan.name) }
    var principal by remember { mutableStateOf(loan.principal.toString()) }
    var interest by remember { mutableStateOf(loan.interestRate.toString()) }
    var tenure by remember { mutableStateOf(loan.tenureYears.toString()) }

    var loanType by remember { mutableStateOf(loan.loanType) }
    var frequency by remember { mutableStateOf(loan.repaymentFrequency) }

    val loanTypes = listOf("Personal", "Home", "Car", "Gold")
    val frequencies = listOf("Monthly", "Quarterly")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Loan") },
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

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Loan Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = principal,
                onValueChange = { principal = it },
                label = { Text("Principal") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = interest,
                onValueChange = { interest = it },
                label = { Text("Interest %") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tenure,
                onValueChange = { tenure = it },
                label = { Text("Tenure (months)") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Loan Type",
                value = loanType,
                options = loanTypes,
                onSelected = { loanType = it }
            )

            DropdownField(
                label = "Repayment Frequency",
                value = frequency,
                options = frequencies,
                onSelected = { frequency = it }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    val p = principal.toDoubleOrNull() ?: 0.0
                    val r = interest.toDoubleOrNull() ?: 0.0
                    val t = tenure.toIntOrNull() ?: 0

                    val newEmi = calculateEMI(p, r, t)

                    val updatedLoan = loan.copy(
                        name = name,
                        principal = p,
                        interestRate = r,
                        tenureYears = t,
                        emi = newEmi,
                        loanType = loanType,
                        repaymentFrequency = frequency
                    )

                    vm.updateLoan(
                        updatedLoan,
                        onSuccess = { onBack() },
                        onError = {}
                    )
                }
            ) {
                Text("Update Loan")
            }
        }
    }
}
