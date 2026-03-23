package com.example.moneymitra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Chit
import com.example.moneymitra.ui.viewmodel.AddTransactionViewModel
import com.example.moneymitra.viewmodel.ChitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberChits(
    navController: NavController,
    viewModel: ChitViewModel = viewModel()
) {

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.outline,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.outline
    )

    val chits by viewModel.chits.collectAsState()
    val memberChits = chits.filter { !it.manager }

    var selectedChit by remember { mutableStateOf<Chit?>(null) }

    var showDelete by remember { mutableStateOf(false) }
    var showPay by remember { mutableStateOf(false) }
    var showEdit by remember { mutableStateOf(false) }

    // 🔥 NEW: State to track which card is currently expanded
    // Note: Assuming chit.id is a String. If it's an Int or Long, update the type accordingly.
    var expandedChitId by remember { mutableStateOf<String?>(null) }

    val transactionVM: AddTransactionViewModel = viewModel()

    Box(Modifier.fillMaxSize()) {

        LazyColumn {
            items(memberChits) { chit ->

                ChitMemberCard(
                    chit = chit,

                    // Pass the state down to the card
                    isExpanded = expandedChitId == chit.id,

                    // Handle the click event to expand/collapse
                    onClick = {
                        // If clicking the already expanded card, close it (set to null)
                        // Otherwise, expand this card by setting the state to its ID
                        expandedChitId = if (expandedChitId == chit.id) null else chit.id
                    },

                    onShowDetails = {
                        navController.navigate("memberChitDetail/${chit.id}")
                    },

                    onEdit = {
                        selectedChit = chit
                        showEdit = true
                    },

                    onPay = {
                        selectedChit = chit
                        showPay = true
                    },

                    onDelete = {
                        selectedChit = chit
                        showDelete = true
                    }
                )
            }
        }

        // 🔥 DELETE POPUP
        if (showDelete && selectedChit != null) {

            AlertDialog(
                onDismissRequest = {
                    showDelete = false
                    selectedChit = null
                },

                confirmButton = {
                    Button(onClick = {
                        viewModel.deleteChit(selectedChit!!.id) {
                            showDelete = false
                            selectedChit = null
                        }
                    }) {
                        Text("Delete")
                    }
                },

                dismissButton = {
                    TextButton(onClick = {
                        showDelete = false
                        selectedChit = null
                    }) {
                        Text("Cancel")
                    }
                },

                title = { Text("Delete Chit") },
                text = { Text("Are you sure you want to delete this chit?") }
            )
        }

        // 🔥 PAY POPUP
        if (showPay && selectedChit != null) {

            var amount by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = {
                    showPay = false
                    selectedChit = null
                },

                confirmButton = {
                    Button(onClick = {

                        val amt = amount.toDoubleOrNull() ?: return@Button

                        viewModel.payChit(
                            chit = selectedChit!!,
                            amount = amt,
                            transactionVM = transactionVM
                        ) {
                            showPay = false
                            selectedChit = null
                        }

                    }) {
                        Text("Pay")
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            showPay = false
                            selectedChit = null
                        }
                    ) {
                        Text("Cancel")
                    }
                },

                title = { Text("Pay Chit") },

                text = {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Enter Amount") },
                        colors = textFieldColors
                    )
                }
            )
        }

        // 🔥 EDIT BOTTOM SHEET
        if (showEdit && selectedChit != null) {

            var name by remember { mutableStateOf(selectedChit!!.name) }
            var amount by remember { mutableStateOf(selectedChit!!.totalAmount.toString()) }

            ModalBottomSheet(
                onDismissRequest = {
                    showEdit = false
                    selectedChit = null
                }
            ) {

                Column(Modifier.padding(16.dp)) {

                    Text("Edit Chit", style = MaterialTheme.typography.titleLarge)

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Chit Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {

                            val updated = selectedChit!!.copy(
                                name = name,
                                totalAmount = amount.toDoubleOrNull()
                                    ?: selectedChit!!.totalAmount
                            )

                            viewModel.updateChit(updated) {
                                showEdit = false
                                selectedChit = null
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}