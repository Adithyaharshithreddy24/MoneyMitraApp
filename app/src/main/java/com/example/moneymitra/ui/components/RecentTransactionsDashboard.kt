package com.example.moneymitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.moneymitra.auth.Transaction
import com.example.moneymitra.ui.screens.toCamelCase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecentTransactionsDashboard(
    transactions: List<Transaction>,
    onViewAll: () -> Unit
) {
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
                .fillMaxWidth()
                .background(Color.White) // Forces pure white background
                .padding(top = 24.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E1E1E))

                Row(
                    modifier = Modifier.clickable { onViewAll() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "View All", fontSize = 13.sp, color = Color(0xFF311B92), fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF311B92), modifier = Modifier.size(14.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFA2A4A8), modifier = Modifier.padding(horizontal = 24.dp))
            Column {
                transactions.take(4).forEachIndexed { index, tx ->
                    RecentTransactionItem(tx)
                    if (index < transactions.take(4).size - 1) {
                        HorizontalDivider(color = Color(0xFFA2A4A8), modifier = Modifier.padding(horizontal = 24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RecentTransactionItem(tx: Transaction) {
    val date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(tx.createdAt))
    val isIncome = tx.type.equals("INCOME", true) || tx.type.equals("Credit", true)

    // 🔹 FIXED: Bright Red and Emerald Green colors for amounts
    val amountColor = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
    val amountPrefix = if (isIncome) "+₹" else "-₹"

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = toCamelCase(tx.name.ifBlank { tx.category }),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E1E1E)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${tx.category} • $date",
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }

        Text(
            text = "$amountPrefix${tx.amount}",
            fontSize = 16.sp,
            color = amountColor,
            fontWeight = FontWeight.Bold
        )
    }
}