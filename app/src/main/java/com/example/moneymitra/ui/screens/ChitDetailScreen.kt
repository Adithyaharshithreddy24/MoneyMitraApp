package com.example.moneymitra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Member
import com.example.moneymitra.ui.components.SendReminderDialog
import com.example.moneymitra.ui.viewmodel.AddTransactionViewModel
import com.example.moneymitra.utils.MailUtils
import com.example.moneymitra.viewmodel.ChitViewModel

// THEME COLORS
private val BackgroundColor = Color(0xFFF6F7F9)
private val PrimaryDark = Color(0xFF311B92)
private val PrimaryLight = Color(0xFFEDE6F3)
private val TextDark = Color(0xFF1E1E1E)
private val TextGray = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF10B981)
private val ErrorRed = Color(0xFFEF4444)

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

    var expandedMemberIndex by remember { mutableStateOf<Int?>(null) }

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
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedChit?.name ?: "Chit Details",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    titleContentColor = TextDark
                )
            )
        },
        floatingActionButton = {
            if (membersWithDue.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = PrimaryDark,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null) },
                    text = { Text("Remind Dues", fontWeight = FontWeight.Bold) }
                )
            }
        }
    ) { padding ->

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
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryDark)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    selectedChit?.let { chit ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryDark),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(Modifier.padding(24.dp)) {
                                Text(
                                    text = "TOTAL POOL",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "₹${chit.totalAmount}",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Spacer(Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("DURATION", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                        Text("${chit.months} Months", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("MONTHLY EMI", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                        Text("₹${chit.monthlyAmount}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(Modifier.height(24.dp))

                                val paidCount = members.count { it.payout }
                                val progress = if (members.isNotEmpty()) paidCount / members.size.toFloat() else 0f

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Payout Progress", color = Color.White, fontSize = 12.sp)
                                    Text("$paidCount / ${members.size} Paid", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    strokeCap = StrokeCap.Round,
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.2f)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Members (${members.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                if (members.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No members found", color = TextGray)
                        }
                    }
                } else {
                    itemsIndexed(members) { index, member ->
                        val isExpanded = expandedMemberIndex == index
                        var showEdit by remember { mutableStateOf(false) }
                        var showSettle by remember { mutableStateOf(false) }
                        var showPayout by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    expandedMemberIndex = if (isExpanded) null else index
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(if (isExpanded) 4.dp else 1.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(PrimaryLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark)
                                    }

                                    Spacer(Modifier.width(12.dp))

                                    // 🔹 FIX 1: Added weight and TextOverflow to Name
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = member.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(member.phone, fontSize = 12.sp, color = TextGray)
                                    }

                                    Spacer(Modifier.width(8.dp))

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "₹${member.due}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (member.due > 0) ErrorRed else SuccessGreen
                                        )
                                        Text("DUE", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                                    }
                                }

                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically(animationSpec = tween(300)),
                                    exit = shrinkVertically(animationSpec = tween(300))
                                ) {
                                    Column {
                                        Spacer(Modifier.height(16.dp))
                                        HorizontalDivider(color = Color(0xFFF3F4F6))
                                        Spacer(Modifier.height(16.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // 🔹 FIX 2: Added weight to the Email row to allow status badge to breathe
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                                            ) {
                                                Icon(Icons.Default.Email, null, tint = TextGray, modifier = Modifier.size(16.dp))
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = member.email,
                                                    fontSize = 13.sp,
                                                    color = TextGray,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            // Status Badge
                                            if (member.payout) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.background(SuccessGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("Paid (M-${member.payoutmonth})", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.background(Color(0xFFFFFBEB), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Box(modifier = Modifier.size(8.dp).background(Color(0xFFD97706), CircleShape))
                                                    Spacer(Modifier.width(6.dp))
                                                    Text("Pending", color = Color(0xFFD97706), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(20.dp))

                                        // Action Buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            OutlinedButton(
                                                onClick = { showEdit = true },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Edit", fontSize = 13.sp)
                                            }

                                            Spacer(Modifier.width(8.dp))

                                            OutlinedButton(
                                                onClick = { showSettle = true },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Settle", fontSize = 13.sp)
                                            }

                                            Spacer(Modifier.width(8.dp))

                                            Button(
                                                onClick = { showPayout = true },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Payout", fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 🔥 DIALOGS
                        if (showEdit) {
                            var name by remember { mutableStateOf(member.name) }
                            var email by remember { mutableStateOf(member.email) }
                            var phone by remember { mutableStateOf(member.phone) }

                            AlertDialog(
                                onDismissRequest = { showEdit = false },
                                containerColor = Color.White,
                                confirmButton = {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                                        onClick = {
                                            val updated = member.copy(name = name, email = email, phone = phone)
                                            viewModel.updateMember(chitId, updated) {
                                                showEdit = false
                                                refreshMembers()
                                            }
                                        }) { Text("Update") }
                                },
                                dismissButton = {
                                    TextButton({ showEdit = false }) { Text("Cancel", color = TextGray) }
                                },
                                title = { Text("Edit Member", fontWeight = FontWeight.Bold) },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(name, { name = it }, label = { Text("Name") }, singleLine = true)
                                        OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true)
                                        OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, singleLine = true)
                                    }
                                }
                            )
                        }

                        if (showSettle) {
                            var amount by remember { mutableStateOf("") }
                            AlertDialog(
                                onDismissRequest = { showSettle = false },
                                containerColor = Color.White,
                                confirmButton = {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                                        onClick = {
                                            val amt = amount.toDoubleOrNull() ?: 0.0
                                            viewModel.settleAmount(chitId, member, amt, transactionVM) {
                                                showSettle = false
                                                refreshMembers()
                                            }
                                        }) { Text("Confirm") }
                                },
                                dismissButton = {
                                    TextButton({ showSettle = false }) { Text("Cancel", color = TextGray) }
                                },
                                title = { Text("Settle Amount", fontWeight = FontWeight.Bold) },
                                text = {
                                    OutlinedTextField(amount, { amount = it }, label = { Text("Enter Amount") }, singleLine = true)
                                }
                            )
                        }

                        if (showPayout) {
                            var payoutMonth by remember { mutableStateOf("") }
                            AlertDialog(
                                onDismissRequest = { showPayout = false },
                                containerColor = Color.White,
                                confirmButton = {
                                    Button(
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                                        onClick = {
                                            val month = payoutMonth.toIntOrNull()
                                            if (month == null) return@Button
                                            var updated = member.copy(payout = false, payoutmonth = 0)
                                            if (month > 0) {
                                                updated = member.copy(payout = true, payoutmonth = month)
                                            }
                                            viewModel.updateMember(chitId, updated) {
                                                showPayout = false
                                                refreshMembers()
                                            }
                                        }) { Text("Confirm") }
                                },
                                dismissButton = {
                                    TextButton({ showPayout = false }) { Text("Cancel", color = TextGray) }
                                },
                                title = { Text("Payout Month", fontWeight = FontWeight.Bold) },
                                text = {
                                    OutlinedTextField(payoutMonth, { payoutMonth = it }, label = { Text("Month Number") }, singleLine = true)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}