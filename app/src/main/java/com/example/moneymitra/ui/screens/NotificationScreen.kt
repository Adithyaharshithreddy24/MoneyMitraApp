package com.example.moneymitra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneymitra.repository.Transaction
import com.example.moneymitra.data.model.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    onEdit: (notification: Response) -> Unit
) {


    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var notifications by remember { mutableStateOf<List<Response>>(emptyList()) }
    var expandedId by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {

        val userId = auth.currentUser?.uid ?: return@LaunchedEffect

        db.collection("users")
            .document(userId)
            .collection("notifications")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {

                    notifications = snapshot.documents.map {

                        Response(
                            id =it.id,
                            name = it.getString("name") ?: "",
                            amount = it.getDouble("amount") ?: 0.0,
                            type = it.getString("type") ?: "",
                            category = it.getString("category") ?: "",
                            note = it.getString("note") ?: "",
                            createdAt = it.getLong("createdAt") ?: 0L
                        )
                    }
                }
            }
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            items(notifications, key = { it.id }) { tx ->

                NotificationExpandableCard(

                    tx = tx,
                    expanded = expandedId == tx.id,

                    onClick = {
                        expandedId =
                            if (expandedId == tx.id)
                                null
                            else
                                tx.id
                    },

                    onAdd = {

                        val userId = auth.currentUser?.uid ?: return@NotificationExpandableCard

                        db.collection("users")
                            .document(userId)
                            .collection("accounts")
                            .limit(1)
                            .get()
                            .addOnSuccessListener { snapshot ->

                                val doc = snapshot.documents.firstOrNull() ?: return@addOnSuccessListener

                                val accountId = doc.id

                                val accountLabel =
                                    "${doc.getString("accName")} | ${doc.getString("bankName")}"

                                val transaction = Transaction(
                                    accountId = accountId,
                                    accountLabel = accountLabel,
                                    name = tx.name,
                                    amount = tx.amount,
                                    category = tx.category,
                                    type = if (tx.type == "DEBIT") "EXPENSE" else "INCOME",
                                    note = tx.note,
                                    createdAt = tx.createdAt
                                )

                                db.collection("users")
                                    .document(userId)
                                    .collection("transactions")
                                    .add(transaction)
                                    .addOnSuccessListener {

                                        db.collection("users")
                                            .document(userId)
                                            .collection("notifications")
                                            .document(tx.id)
                                            .delete()
                                    }
                            }
                    },

                    onEdit = {
                        onEdit(tx)
                    },

                    onDelete = {

                        val userId =
                            auth.currentUser?.uid ?: return@NotificationExpandableCard

                        db.collection("users")
                            .document(userId)
                            .collection("notifications")
                            .document(tx.id)
                            .delete()
                    }
                )
            }
        }
    }
}
@Composable
fun NotificationExpandableCard(
    tx: Response,
    expanded: Boolean,
    onClick: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    var showDeleteDialog by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        Divider(
            modifier = Modifier.padding(top = 12.dp),
            color = colors.outlineVariant
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector =
                        if (tx.type == "DEBIT")
                            Icons.Default.CallMade
                        else
                            Icons.Default.CallReceived,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text =
                        if (tx.type == "DEBIT")
                            "Paid to"
                        else
                            "Received from",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )

                Text(
                    text = toCamelCase(tx.name),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(Date(tx.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "₹${tx.amount}",
                    fontWeight = FontWeight.Bold,
                    color =
                        if (tx.type == "CREDIT")
                            Color(0xFF2E7D32)
                        else
                            colors.onSurface
                )

                Text(
                    text = tx.category.uppercase(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                if (tx.note.isNotBlank()) {
                    Text(
                        text = toCamelCase(tx.note),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    TextButton(
                        onClick = onEdit,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text("Edit")
                    }

                    TextButton(onClick = onAdd,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Green
                        )) {
                        Text("Add")
                    }


                    TextButton(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
    if (showDeleteDialog) {

        AlertDialog(

            onDismissRequest = { showDeleteDialog = false },

            title = { Text("Delete Notification") },

            text = {
                Text("Are you sure you want to delete this notification?")
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },

            dismissButton = {

                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun responseToTransaction(r: Response): Transaction {

    return Transaction(
        id = UUID.randomUUID().toString(),
        name = r.name,
        amount = r.amount,
        category = r.category,
        type = if (r.type == "DEBIT") "EXPENSE" else "INCOME",
        accountLabel = "UPI",
        note = r.note,
        createdAt = r.createdAt
    )
}