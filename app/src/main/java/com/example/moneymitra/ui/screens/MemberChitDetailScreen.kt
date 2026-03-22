package com.example.moneymitra.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneymitra.data.model.Chit

@Composable
fun MemberChitDetailScreen(chit: Chit) {

    val months = chit.months
    val base = chit.monthlyAmount
    val borderColor = MaterialTheme.colorScheme.outline

    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(chit.name, style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(8.dp))

        Text("Total Amount: ₹${chit.totalAmount}")
        Text("Months: ${chit.months}")

        Spacer(Modifier.height(16.dp))

        // 🔥 HEADER (FIXED)
        Row(
            modifier = Modifier.horizontalScroll(horizontalScroll)
        ) {
            TableRow(
                borderColor = borderColor,
                cells = listOf("Month", "Payout", "Taken", "Not Taken", "Profit"),
                isHeader = true
            )
        }

        // 🔥 BODY (SCROLLABLE)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScroll)
                .verticalScroll(verticalScroll)
        ) {

            Column {

                repeat(months) { index ->

                    val month = index + 1

                    val payout = chit.totalAmount + ((month - 1) * 1000)

                    val takenAmount = base + 1000
                    val notTakenAmount = base

                    val totalPaid =
                        (month * chit.monthlyAmount) +
                                ((chit.months - month) * (chit.monthlyAmount + 1000))

                    val profit = payout - totalPaid

                    TableRow(
                        borderColor = borderColor,
                        cells = listOf(
                            "$month",
                            "₹${payout.toInt()}",
                            "₹${takenAmount.toInt()}",
                            "₹${notTakenAmount.toInt()}",
                            "₹${profit.toInt()}"
                        )
                    )
                }
            }
        }
    }
}
@Composable
fun TableRow(
    cells: List<String>,
    borderColor: androidx.compose.ui.graphics.Color,
    isHeader: Boolean = false
) {

    Row(
        modifier = Modifier.width(700.dp) // required for scroll
    ) {

        cells.forEach { text ->

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, borderColor)
                    .padding(8.dp)
            ) {
                Text(
                    text = text,
                    style = if (isHeader)
                        MaterialTheme.typography.labelLarge
                    else
                        MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}