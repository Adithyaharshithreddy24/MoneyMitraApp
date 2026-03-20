package com.example.moneymitra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymitra.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLoanScreen(
    navController: NavController,
    viewModel: LoanViewModel = viewModel()
) {

    var name by remember { mutableStateOf("") }
    var principal by remember { mutableStateOf("") }
    var interest by remember { mutableStateOf("") }
    var tenure by remember { mutableStateOf("") }

    val loanTypes = listOf("Personal", "Home", "Car", "Gold")
    val frequencies = listOf("Monthly", "Quarterly")

    var selectedType by remember { mutableStateOf(loanTypes[0]) }
    var selectedFrequency by remember { mutableStateOf(frequencies[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Loan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding) // IMPORTANT
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {


            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Loan Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = principal,
                onValueChange = { principal = it },
                label = { Text("Principal") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = interest,
                onValueChange = { interest = it },
                label = { Text("Interest %") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = tenure,
                onValueChange = { tenure = it },
                label = { Text("Tenure (Years)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Loan Type",
                value = selectedType,
                options = loanTypes,
                onSelected = { selectedType = it }
            )

            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Repayment Frequency",
                value = selectedFrequency,
                options = frequencies,
                onSelected = { selectedFrequency = it }
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.addLoan(
                        name,
                        principal,
                        interest,
                        tenure,
                        selectedType,
                        selectedFrequency,
                        onSuccess = { navController.popBackStack() },
                        onError = {}
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Loan")
            }
        }
    }
}