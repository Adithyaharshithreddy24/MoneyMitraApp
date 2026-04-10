package com.example.moneymitra.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.LoanRequest
import com.example.moneymitra.viewmodel.LoanPredictionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalLoanPredictionScreen(
    onBack: () -> Unit,
    viewModel: LoanPredictionViewModel = viewModel()
) {

    // 🔹 STATES
    var dependents by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("Graduate") }
    var selfEmployed by remember { mutableStateOf("No") }
    var income by remember { mutableStateOf("") }
    var loanAmount by remember { mutableStateOf("") }
    var loanTerm by remember { mutableStateOf("") }
    var cibil by remember { mutableStateOf("") }
    var residential by remember { mutableStateOf("") }
    var commercial by remember { mutableStateOf("") }
    var luxury by remember { mutableStateOf("") }
    var bank by remember { mutableStateOf("") }

    var inputError by remember { mutableStateOf<String?>(null) }

    val result = viewModel.result
    val loading = viewModel.isLoading
    val error = viewModel.error

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Loan Prediction") },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔹 INPUT FIELDS
            InputField("Dependents", dependents) { dependents = it }
            DropdownField(
                label = "Education",
                options = listOf("Graduate", "Not Graduate"),
                selected = education
            ) { education = it }

            DropdownField(
                label = "Self Employed",
                options = listOf("Yes", "No"),
                selected = selfEmployed
            ) { selfEmployed = it }
            InputField("Annual Income", income) { income = it }
            InputField("Loan Amount", loanAmount) { loanAmount = it }
            InputField("Loan Term (months)", loanTerm) { loanTerm = it }
            InputField("CIBIL Score", cibil) { cibil = it }

            InputField("Residential Assets", residential) { residential = it }
            InputField("Commercial Assets", commercial) { commercial = it }
            InputField("Luxury Assets", luxury) { luxury = it }
            InputField("Bank Balance", bank) { bank = it }

            // 🔽 DROPDOWNS

            // ❌ INPUT ERROR
            inputError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // 🔥 BUTTON
            Button(
                onClick = {
                    try {
                        inputError = null

                        if (
                            dependents.isEmpty() ||
                            income.isEmpty() ||
                            loanAmount.isEmpty() ||
                            loanTerm.isEmpty() ||
                            cibil.isEmpty()
                        ) {
                            inputError = "Please fill all required fields"
                            return@Button
                        }

                        val request = LoanRequest(
                            no_of_dependents = dependents.toInt(),
                            education = education,
                            self_employed = selfEmployed,
                            income_annum = income.toDouble(),
                            loan_amount = loanAmount.toDouble(),
                            loan_term = loanTerm.toInt(),
                            cibil_score = cibil.toInt(),
                            residential_assets_value = residential.toDoubleOrNull() ?: 0.0,
                            commercial_assets_value = commercial.toDoubleOrNull() ?: 0.0,
                            luxury_assets_value = luxury.toDoubleOrNull() ?: 0.0,
                            bank_asset_value = bank.toDoubleOrNull() ?: 0.0
                        )

                        viewModel.personalloanprediction(request)

                    } catch (e: Exception) {
                        inputError = "Invalid input values"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Predict Loan")
            }

            // 🔄 LOADING
            if (loading) {
                CircularProgressIndicator()
            }

            // ❌ API ERROR
            error?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }

            // ✅ RESULT CARD
            result?.let {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            if (it.loan_status == "Approved")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Status: ${it.loan_status}")
                        Text("Probability: ${it.approval_probability}%")
                        Text("Risk Level: ${it.risk_level}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Recommendations:")
                        it.recommendations.forEach {
                            Text("• $it")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Top Factors:")
                        it.explanation.forEach {
                            Text("${it.feature}: ${it.impact}")
                        }
                    }
                }
            }
        }
    }
}


// =============================
// 🔹 REUSABLE INPUT FIELD
// =============================
@Composable
fun InputField(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}


// =============================
// 🔽 DROPDOWN FIELD
// =============================
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label)

        Box {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, null)
                }
            )

            DropdownMenu(
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

            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
        }
    }
}