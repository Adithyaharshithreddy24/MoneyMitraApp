package com.example.moneymitra.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneymitra.data.model.Member

@Composable
fun SendReminderDialog(
    members: List<Member>,
    onSend: (List<Member>) -> Unit,
    onDismiss: () -> Unit
) {

    // 🔥 Fix: Reset when members change
    var selectedMembers by remember(members) {
        mutableStateOf(members.map { it to true })
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text("Send Payment Reminder")
        },

        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {

                itemsIndexed(selectedMembers) { index, pair ->

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Checkbox(
                            checked = pair.second,
                            onCheckedChange = { isChecked ->
                                selectedMembers = selectedMembers.toMutableList().also {
                                    it[index] = pair.first to isChecked
                                }
                            }
                        )

                        Column {
                            Text(pair.first.name)

                            Text(
                                text = "Due: ₹${pair.first.due}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {
            Button(
                onClick = {
                    val selected = selectedMembers
                        .filter { it.second }
                        .map { it.first }

                    if (selected.isNotEmpty()) {
                        onSend(selected)
                    }
                }
            ) {
                Text("Send Mail")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}