package com.example.moneymitra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.moneymitra.ui.viewmodel.ExpenseCategory

@Composable
fun ExpensePieChart(
    categories: List<ExpenseCategory>,
    totalExpense: Double
) {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            var startAngle = -90f

            categories.forEach { category ->

                val sweep =
                    (category.amount / totalExpense * 360).toFloat()

                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 40f)
                )

                startAngle += sweep
            }
        }

        Text(
            text = "₹%.0f".format(totalExpense),
            fontWeight = FontWeight.Bold
        )
    }
}