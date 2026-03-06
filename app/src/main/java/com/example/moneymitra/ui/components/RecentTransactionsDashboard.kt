package com.example.moneymitra.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .offset(y=(-15).dp)
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Recent Activities",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.clickable { onViewAll() },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "View All",
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View All",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                transactions.take(4).forEach { tx ->
                    RecentTransactionItem(tx)
                }
            }
        }
    }
}

@Composable
fun RecentTransactionItem(
    tx: Transaction
) {

    val date = SimpleDateFormat(
        "dd MMM",
        Locale.getDefault()
    ).format(Date(tx.createdAt))

    val isIncome = tx.type.equals("INCOME", true) ||
            tx.type.equals("Credit", true)

    val amountColor =
        if (isIncome) Color(0xFF2E7D32) else Color.Red

    val amountPrefix =
        if (isIncome) "+₹" else "-₹"

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = toCamelCase( tx.name.ifBlank { tx.category }),
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${tx.category} • $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$amountPrefix${tx.amount}",
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}