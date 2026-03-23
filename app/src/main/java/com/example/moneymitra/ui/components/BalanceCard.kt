package com.example.moneymitra.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneymitra.auth.Transaction
import com.example.moneymitra.ui.screens.toCamelCase
import com.example.moneymitra.ui.viewmodel.ExpenseCategory
import java.text.SimpleDateFormat
import java.util.*

// Standardized Theme Colors
private val TextDark = Color(0xFF1E1E1E)
private val TextGray = Color(0xFF6B7280)
private val PrimaryDark = Color(0xFF311B92)
private val TrackGray = Color(0xFFF3F4F6)

@Composable
fun BalanceCard(
    balance: Double,
    categories: List<ExpenseCategory>,
    totalExpense: Double
) {
    var expanded by remember { mutableStateOf(false) }

    val topCategories = categories.take(3)
    val hiddenCategories = categories.drop(3)

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)), // Replaces default elevation
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White) // Forces pure white background
                .padding(24.dp)
        ) {
            Text(
                text = "TOTAL BALANCE",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "₹%.2f".format(balance),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryDark
            )

            Spacer(modifier = Modifier.height(24.dp))

            ExpensePieChart(
                categories = categories,
                totalExpense = totalExpense
            )

            Spacer(modifier = Modifier.height(32.dp))

            topCategories.forEach { category ->
                CategoryItem(category = category, totalExpense = totalExpense)
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column {
                    hiddenCategories.forEach { category ->
                        CategoryItem(category = category, totalExpense = totalExpense)
                    }
                }
            }

            if (categories.size > 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "Show Less" else "Show More",
                        color = TextGray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(category: ExpenseCategory, totalExpense: Double) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = category.color)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = category.name,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            Text(
                text = "₹${category.amount}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val progressVal = if (totalExpense > 0) (category.amount / totalExpense).toFloat() else 0f

        LinearProgressIndicator(
            progress = { progressVal.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            strokeCap = StrokeCap.Round,
            color = category.color,
            trackColor = TrackGray
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

