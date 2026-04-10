package com.example.moneymitra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionSelectionScreen(
    onBack: () -> Unit,
    onPersonalLoanClick: () -> Unit,
    onHomeLoanClick: () -> Unit,
    onGoldLoanClick: () -> Unit,
    onVehicleLoanClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Prediction") },
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

            Text(
                text = "Select Loan Type",
                style = MaterialTheme.typography.titleMedium
            )

            // 🔥 PERSONAL LOAN
            PredictionCard(
                title = "Personal Loan",
                subtitle = "Check eligibility using AI",
                icon = Icons.Default.Person,
                onClick = onPersonalLoanClick
            )

            // 🏠 HOME LOAN
            PredictionCard(
                title = "Home Loan",
                subtitle = "Predict property financing",
                icon = Icons.Default.Home,
                onClick = onHomeLoanClick
            )

            // 💰 GOLD LOAN
            PredictionCard(
                title = "Gold Loan",
                subtitle = "Loan against gold assets",
                icon = Icons.Default.AccountBalanceWallet,
                onClick = onGoldLoanClick
            )

            // 🚗 VEHICLE LOAN
            PredictionCard(
                title = "Vehicle Loan",
                subtitle = "Car / Bike loan eligibility",
                icon = Icons.Default.DirectionsCar,
                onClick = onVehicleLoanClick
            )
        }
    }
}
@Composable
fun PredictionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ICON BOX
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // TEXT
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}