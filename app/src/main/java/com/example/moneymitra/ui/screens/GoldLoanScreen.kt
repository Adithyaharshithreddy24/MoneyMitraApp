package com.example.moneymitra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.viewmodel.LoanPredictionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldLoanScreen(
    onBack: () -> Unit,
    viewModel: LoanPredictionViewModel = viewModel()
) {

    var weight by remember { mutableStateOf("") }

    val result = viewModel.goldResult
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gold Loan Predictor") },
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
                text = "Enter gold weight to check eligibility",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )

            // Gold Weight Input
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Gold Weight (grams)") },
                leadingIcon = {
                    Icon(Icons.Default.Toll, contentDescription = null, tint = colors.primary)
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
                    viewModel.calculateGoldLoan(
                        weight.toDoubleOrNull() ?: 0.0
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Calculate Gold Loan", style = MaterialTheme.typography.titleMedium)
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

                            Spacer(modifier = Modifier.height(4.dp))

                            ResultRow(label = "Loan Amount", value = "₹${it.loan_amount}")
                            ResultRow(label = "Interest Rate", value = "${it.interest_rate}")
                            ResultRow(label = "Tenure", value = "${it.tenure_months} months")
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

