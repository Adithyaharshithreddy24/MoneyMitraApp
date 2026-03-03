package com.example.moneymitra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.moneymitra.ui.viewmodel.ExpenseCategory

@Composable
fun BalanceCard(
    balance: Double,
    categories: List<ExpenseCategory>,
    totalExpense: Double
) {

    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text("Available Balance")

            Text(
                text = "₹%.2f".format(balance),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            ExpensePieChart(
                categories = categories,
                totalExpense = totalExpense
            )

            Spacer(modifier = Modifier.height(16.dp))

            categories.forEach {
                Text("${it.name} : ₹${it.amount}")
            }
        }
    }
}