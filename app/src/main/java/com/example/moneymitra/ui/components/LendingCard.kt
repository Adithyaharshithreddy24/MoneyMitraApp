package com.example.moneymitra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

@Composable
fun LendingBorrowingSection() {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE57373)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Lending")
                Text("₹399", fontWeight = FontWeight.Bold)
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFB74D)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Borrowing")
                Text("₹375", fontWeight = FontWeight.Bold)
            }
        }
    }
}