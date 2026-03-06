package com.example.moneymitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.moneymitra.ui.viewmodel.ExpenseCategory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon

@Composable
fun BalanceCard(
    balance: Double,
    categories: List<ExpenseCategory>,
    totalExpense: Double
) {

    var expanded by remember { mutableStateOf(false) }

    val visibleCategories =
        if (expanded) categories else categories.take(3)

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-60).dp)
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                "Your total balance",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "₹%.2f".format(balance),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3192)
            )

            Spacer(modifier = Modifier.height(20.dp))

            ExpensePieChart(
                categories = categories,
                totalExpense = totalExpense
            )

            Spacer(modifier = Modifier.height(20.dp))

            visibleCategories.forEach {

                Column {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(it.color, RoundedCornerShape(50))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            it.name,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp
                        )

                        Text(
                            "₹${it.amount}",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = (it.amount / totalExpense).toFloat(),
                        color = it.color,
                        trackColor = Color.LightGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (categories.size > 3) {

                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = if (expanded) "less" else "more",
                            color = Color.Gray
                        )

                        Icon(
                            imageVector = if (expanded)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}