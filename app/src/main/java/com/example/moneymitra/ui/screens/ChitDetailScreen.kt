package com.example.moneymitra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Member
import com.example.moneymitra.ui.components.SendReminderDialog
import com.example.moneymitra.ui.viewmodel.AddTransactionViewModel
import com.example.moneymitra.utils.MailUtils
import com.example.moneymitra.viewmodel.ChitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChitDetailScreen(
    navController: NavController,
    chitId: String,
    viewModel: ChitViewModel = viewModel()
) {

    var showDialog by remember { mutableStateOf(false) }

    val transactionVM: AddTransactionViewModel = viewModel()
    val chits by viewModel.chits.collectAsState()
    val selectedChit = chits.find { it.id == chitId }

    var members by remember { mutableStateOf<List<Member>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val membersWithDue = members.filter { it.due > 0 }

    fun refreshMembers() {
        loading = true
        viewModel.getMembers(chitId) {
            members = it
            loading = false
        }
    }

    LaunchedEffect(chitId) {
        refreshMembers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedChit?.name ?: "Chit Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ✅ BUTTON FIXED POSITION
            Button(
                onClick = { showDialog = true },
                enabled = membersWithDue.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Send Reminder")
            }

            if (showDialog) {
                SendReminderDialog(
                    members = membersWithDue,
                    onSend = {
                        MailUtils.sendReminder(it)
                        showDialog = false
                    },
                    onDismiss = { showDialog = false }
                )
            }

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {

                    item {
                        selectedChit?.let { chit ->

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(Modifier.padding(16.dp)) {

                                    Text(chit.name, style = MaterialTheme.typography.titleLarge)

                                    Spacer(Modifier.height(8.dp))

                                    Text("Total Amount: ₹${chit.totalAmount}")
                                    Text("Months: ${chit.months}")
                                    Text("Monthly: ₹${chit.monthlyAmount}")

                                    Spacer(Modifier.height(10.dp))

                                    val paidCount = members.count { it.payout }
                                    val progress =
                                        if (members.isNotEmpty())
                                            paidCount / members.size.toFloat()
                                        else 0f

                                    LinearProgressIndicator(progress = progress)

                                    Text("Progress: $paidCount / ${members.size}")
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            Text("Members", style = MaterialTheme.typography.titleMedium)

                            Spacer(Modifier.height(10.dp))
                        }
                    }

                    items(members) { member ->

                        var showEdit by remember { mutableStateOf(false) }
                        var showSettle by remember { mutableStateOf(false) }
                        var showPayout by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(14.dp)) {

                                Text(member.name, style = MaterialTheme.typography.titleMedium)

                                Spacer(Modifier.height(4.dp))

                                Text("📧 ${member.email}")
                                Text("📱 ${member.phone}")

                                Spacer(Modifier.height(6.dp))

                                Text("Due: ₹${member.due}")

                                Spacer(Modifier.height(6.dp))

                                if (member.payout) {
                                    Text(
                                        "✅ Taken (Month ${member.payoutmonth})",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text(
                                        "⏳ Not Taken",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Button(onClick = { showEdit = true }) {
                                        Text("Edit")
                                    }

                                    Button(onClick = { showSettle = true }) {
                                        Text("Settle")
                                    }

                                    Button(onClick = { showPayout = true }) {
                                        Text("Payout")
                                    }
                                }
                            }
                        }

                        // 🔥 EDIT
                        if (showEdit) {
                            var name by remember { mutableStateOf(member.name) }
                            var email by remember { mutableStateOf(member.email) }
                            var phone by remember { mutableStateOf(member.phone) }

                            AlertDialog(
                                onDismissRequest = { showEdit = false },
                                confirmButton = {
                                    Button(onClick = {
                                        val updated = member.copy(
                                            name = name,
                                            email = email,
                                            phone = phone
                                        )
                                        viewModel.updateMember(chitId, updated) {
                                            showEdit = false
                                            refreshMembers()
                                        }
                                    }) { Text("Update") }
                                },
                                dismissButton = {
                                    TextButton({ showEdit = false }) { Text("Cancel") }
                                },
                                title = { Text("Edit Member") },
                                text = {
                                    Column {
                                        OutlinedTextField(name, { name = it }, label = { Text("Name") })
                                        OutlinedTextField(email, { email = it }, label = { Text("Email") })
                                        OutlinedTextField(phone, { phone = it }, label = { Text("Phone") })
                                    }
                                }
                            )
                        }

                        // 🔥 SETTLE
                        if (showSettle) {
                            var amount by remember { mutableStateOf("") }

                            AlertDialog(
                                onDismissRequest = { showSettle = false },
                                confirmButton = {
                                    Button(onClick = {
                                        val amt = amount.toDoubleOrNull() ?: 0.0
                                        viewModel.settleAmount(
                                            chitId,
                                            member,
                                            amt,
                                            transactionVM
                                        ) {
                                            showSettle = false
                                            refreshMembers()
                                        }
                                    }) { Text("Confirm") }
                                },
                                dismissButton = {
                                    TextButton({ showSettle = false }) { Text("Cancel") }
                                },
                                title = { Text("Settle Amount") },
                                text = {
                                    OutlinedTextField(
                                        value = amount,
                                        onValueChange = { amount = it },
                                        label = { Text("Enter Amount") }
                                    )
                                }
                            )
                        }

                        // 🔥 PAYOUT
                        if (showPayout) {
                            var payoutMonth by remember { mutableStateOf("") }

                            AlertDialog(
                                onDismissRequest = { showPayout = false },
                                confirmButton = {
                                    Button(onClick = {

                                        val month = payoutMonth.toIntOrNull()
                                        if (month == null) return@Button

                                        var updated = member.copy(
                                            payout = false,
                                            payoutmonth = 0
                                        )

                                        if (month > 0) {
                                            updated = member.copy(
                                                payout = true,
                                                payoutmonth = month
                                            )
                                        }

                                        viewModel.updateMember(chitId, updated) {
                                            showPayout = false
                                            refreshMembers()
                                        }

                                    }) { Text("Confirm") }
                                },
                                dismissButton = {
                                    TextButton({ showPayout = false }) { Text("Cancel") }
                                },
                                title = { Text("Enter Payout Month") },
                                text = {
                                    OutlinedTextField(
                                        value = payoutMonth,
                                        onValueChange = { payoutMonth = it },
                                        label = { Text("Month Number") },
                                        singleLine = true
                                    )
                                }
                            )
                        }
                    }

                    if (members.isEmpty()) {
                        item {
                            Text("No members found", modifier = Modifier.padding(20.dp))
                        }
                    }
                }
            }
        }
    }
}