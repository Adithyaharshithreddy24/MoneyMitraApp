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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.viewmodel.LoanPredictionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLoanScreen(
    onBack: () -> Unit,
    viewModel: LoanPredictionViewModel = viewModel()
) {
    var propertyValue by remember { mutableStateOf("") }
    var monthlyIncome by remember { mutableStateOf("") }
    var cibilScore by remember { mutableStateOf("") }

    val result = viewModel.results
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Loan Predictor") },
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
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header Text
            Text(
                text = "Enter details to check your eligibility",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )

            // Property Value Input
            OutlinedTextField(
                value = propertyValue,
                onValueChange = { propertyValue = it },
                label = { Text("Property Value (₹)") },
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = null, tint = colors.primary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Monthly Income Input
            OutlinedTextField(
                value = monthlyIncome,
                onValueChange = { monthlyIncome = it },
                label = { Text("Monthly Income (₹)") },
                leadingIcon = {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = colors.primary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // CIBIL Score Input
            OutlinedTextField(
                value = cibilScore,
                onValueChange = { cibilScore = it },
                label = { Text("CIBIL Score") },
                leadingIcon = {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = colors.primary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Calculate Button
            Button(
                onClick = {
                    viewModel.calculatehomeLoan(
                        propertyValue.toDoubleOrNull() ?: 0.0,
                        monthlyIncome.toDoubleOrNull() ?: 0.0,
                        cibilScore.toIntOrNull() ?: 0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Calculate Eligibility", style = MaterialTheme.typography.titleMedium)
            }

            // Loading Indicator
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.primary)
                }
            }

            // Error Message
            viewModel.error?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colors.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = colors.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Beautiful Result Card
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
                            val isApproved = it.approval_status.contains("Approv", ignoreCase = true)
                            val statusColor = if (isApproved) Color(0xFF388E3C) else Color(0xFFD32F2F)
                            val statusBg = if (isApproved) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(statusBg)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Status: ${it.approval_status}",
                                    color = statusColor,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            ResultRow(label = "Eligible Loan", value = "₹${it.eligible_loan}")
                            ResultRow(label = "Monthly EMI", value = "₹${it.emi_per_month}")
                            ResultRow(label = "Interest Rate", value = "${it.interest_rate}%")
                            ResultRow(label = "Total Interest", value = "₹${it.total_interest}")

                            HorizontalDivider(color = colors.outlineVariant, modifier = Modifier.padding(vertical = 4.dp))

                            ResultRow(
                                label = "Total Payment",
                                value = "₹${it.total_payment}",
                                isHighlight = true
                            )
                        }
                    }
                }
            }

            // Bottom padding spacer
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Reusable component for the result rows
@Composable
fun ResultRow(label: String, value: String, isHighlight: Boolean = false) {
    val style = if (isHighlight) {
        MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    } else {
        MaterialTheme.typography.bodyMedium
    }

    val color = if (isHighlight) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = style, color = color)
        Text(text = value, style = style, color = MaterialTheme.colorScheme.onSurface)
    }
}