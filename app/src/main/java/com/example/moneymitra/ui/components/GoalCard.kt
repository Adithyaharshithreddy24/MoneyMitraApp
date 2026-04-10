package com.example.moneymitra.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    val progress = if (goal.targetAmount == 0.0) 0f
    else (goal.savedAmount / goal.targetAmount).toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(goal.title, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(4.dp))

            Text("₹${goal.savedAmount} / ₹${goal.targetAmount}")

            Text("Priority: ${goal.priority}")

            if (goal.isCompleted) {
                Text("✅ Completed")
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(progress = progress)

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {

                Column {

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onAddMoney(500.0) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add ₹500")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        OutlinedButton(onClick = { showDialog = true }) {
                            Icon(Icons.Default.Edit, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }

                        OutlinedButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }

    // ✏️ EDIT DIALOG (FIXED STATE)
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
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Edit Goal") },
            text = {
                Column {

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newTarget,
                        onValueChange = { newTarget = it },
                        label = { Text("Target Amount") }
                    )
                }
            }
        )
    }
}