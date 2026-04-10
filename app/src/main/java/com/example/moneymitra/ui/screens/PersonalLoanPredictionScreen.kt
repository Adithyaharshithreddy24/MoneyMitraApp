package com.example.moneymitra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Loan Predictor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = colors.background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header Text
            Text(
                text = "Enter your details for an AI-powered prediction",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )

            // 🔹 INPUT FIELDS
            ModernInputField("Dependents", dependents, Icons.Default.FamilyRestroom) { dependents = it }

            ModernDropdownField(
                label = "Education",
                options = listOf("Graduate", "Not Graduate"),
                selected = education,
                icon = Icons.Default.School
            ) { education = it }

            ModernDropdownField(
                label = "Self Employed",
                options = listOf("Yes", "No"),
                selected = selfEmployed,
                icon = Icons.Default.Work
            ) { selfEmployed = it }

            ModernInputField("Annual Income (₹)", income, Icons.Default.AccountBalanceWallet) { income = it }
            ModernInputField("Loan Amount (₹)", loanAmount, Icons.Default.AttachMoney) { loanAmount = it }
            ModernInputField("Loan Term (Months)", loanTerm, Icons.Default.AccessTime) { loanTerm = it }
            ModernInputField("CIBIL Score", cibil, Icons.Default.Speed) { cibil = it }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Asset Information", style = MaterialTheme.typography.titleMedium, color = colors.primary)

            ModernInputField("Residential Assets (₹)", residential, Icons.Default.Home) { residential = it }
            ModernInputField("Commercial Assets (₹)", commercial, Icons.Default.Storefront) { commercial = it }
            ModernInputField("Luxury Assets (₹)", luxury, Icons.Default.Diamond) { luxury = it }
            ModernInputField("Bank Balance (₹)", bank, Icons.Default.AccountBalance) { bank = it }


            // ❌ INPUT ERROR
            inputError?.let {
                Text(it, color = colors.error, style = MaterialTheme.typography.bodyMedium)
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
                            inputError = "Please fill all required core fields"
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
                        inputError = "Invalid input values. Please check your numbers."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Predict Loan Eligibility", style = MaterialTheme.typography.titleMedium)
            }

            // 🔄 LOADING
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.primary)
                }
            }

            // ❌ API ERROR
            error?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Error: $it",
                        color = colors.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ✅ RESULT CARD
            AnimatedVisibility(visible = result != null) {
                result?.let {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = colors.surface),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Prediction Results",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = colors.primary
                            )

                            HorizontalDivider(color = colors.outlineVariant)

                            // Dynamic Status Color
                            val isApproved = it.loan_status.contains("Approv", ignoreCase = true)
                            val statusColor = if (isApproved) Color(0xFF388E3C) else Color(0xFFD32F2F)
                            val statusBg = if (isApproved) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusBg)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = it.loan_status,
                                        color = statusColor,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Text(
                                    text = "Risk: ${it.risk_level}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = colors.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            ResultRow(label = "Approval Probability", value = "${it.approval_probability}%", isHighlight = true)

                            // Top Factors Section
                            if (it.explanation.isNotEmpty()) {
                                HorizontalDivider(color = colors.outlineVariant, modifier = Modifier.padding(vertical = 4.dp))
                                Text("Key Factors", style = MaterialTheme.typography.titleSmall, color = colors.primary)
                                it.explanation.forEach { factor ->
                                    ResultRow(label = factor.feature, value = factor.impact.toString())
                                }
                            }

                            // Recommendations Section
                            if (it.recommendations.isNotEmpty()) {
                                HorizontalDivider(color = colors.outlineVariant, modifier = Modifier.padding(vertical = 4.dp))
                                Text("Recommendations", style = MaterialTheme.typography.titleSmall, color = colors.primary)
                                it.recommendations.forEach { rec ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text("• ", style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
                                        Text(rec, style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// =============================
// 🔹 MODERN REUSABLE INPUT FIELD
// =============================
@Composable
fun ModernInputField(
    label: String,
    value: String,
    icon: ImageVector,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

// =============================
// 🔽 MODERN DROPDOWN FIELD
// =============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernDropdownField(
    label: String,
    options: List<String>,
    selected: String,
    icon: ImageVector,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
