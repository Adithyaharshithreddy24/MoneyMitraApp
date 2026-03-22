package com.example.moneymitra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneymitra.data.model.Chit

@Composable
fun ChitManagerCard(
    chit: Chit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(chit.name, style = MaterialTheme.typography.titleLarge)
            Text("₹${chit.totalAmount}")
            Text("${chit.months} months")
            LinearProgressIndicator(progress = 0.4f)
        }
    }
}
@Composable
fun ChitMemberCard(
    chit: Chit,
    onShowDetails: () -> Unit,
    onEdit: () -> Unit,
    onPay: () -> Unit,
    onDelete: () -> Unit
) {

    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            // 🔹 TITLE
            Text(
                chit.name,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(6.dp))

            Text("₹${chit.totalAmount}")
            Text("${chit.months} months")
            Text("${chit.due} due")
            Spacer(Modifier.height(10.dp))

            // 🔹 PROGRESS (you can make dynamic later)
            LinearProgressIndicator(
                progress = 0.4f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            // 🔥 BUTTON ROW 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onShowDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Details")
                }

                Spacer(Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }
            }

            Spacer(Modifier.height(8.dp))

            // 🔥 BUTTON ROW 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onPay,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text("Pay")
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.error
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}