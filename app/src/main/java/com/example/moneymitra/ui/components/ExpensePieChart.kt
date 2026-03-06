package com.example.moneymitra.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymitra.ui.viewmodel.ExpenseCategory

@Composable
fun ExpensePieChart(
    categories: List<ExpenseCategory>,
    totalExpense: Double
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.size(170.dp)
        ) {

            var startAngle = -90f

            categories.forEach {

                val sweep =
                    (it.amount / totalExpense * 360).toFloat()

                drawArc(
                    color = it.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = 45f)
                )

                startAngle += sweep
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "Total Expenses",
                fontSize = 12.sp
            )

            Text(
                "₹%.2f".format(totalExpense),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}