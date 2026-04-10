package com.example.moneymitra.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneymitra.data.model.Goal

@Composable
fun GoalCard(
    goal: Goal,
    onAddMoney: (Double) -> Unit,
    onDelete: () -> Unit,
    onUpdate: (Goal) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    val progress = if (goal.targetAmount == 0.0) 0f
    else (goal.savedAmount / goal.targetAmount).toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {

        /* MAIN ROW */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* ICON BOX */
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = goal.title.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(14.dp))

            /* CENTER TEXT */
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "Priority: ${goal.priority}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )

                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium
                )

                if (goal.isCompleted) {
                    Text(
                        text = "✅ Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF388E3C) // Success Green
                    )
                }
            }

            /* TRAILING TEXT (AMOUNTS) */
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${"%.0f".format(goal.savedAmount)}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "of ₹${"%.0f".format(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* PROGRESS BAR (Always visible) */
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = colors.primary,
            trackColor = colors.surfaceVariant
        )

        /* EXPANDED DETAILS */
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = { onAddMoney(500.0) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add ₹500 to Goal")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showDialog = true }) {
                        Text("Edit", color = Color(0xFF388E3C)) // Green
                    }

                    TextButton(onClick = onDelete) {
                        Text("Delete", color = Color.Red) // Red
                    }
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            color = colors.outlineVariant
        )
    }

    // ✏️ EDIT DIALOG
    if (showDialog) {
        var newTitle by remember { mutableStateOf(goal.title) }
        var newTarget by remember { mutableStateOf(goal.targetAmount.toString()) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    val updated = goal.copy(
                        title = newTitle,
                        targetAmount = newTarget.toDoubleOrNull() ?: goal.targetAmount
                    )
                    onUpdate(updated)
                    showDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Edit Goal") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newTarget,
                        onValueChange = { newTarget = it },
                        label = { Text("Target Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        )
    }
}