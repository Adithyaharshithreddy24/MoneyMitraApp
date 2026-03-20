package com.example.moneymitra.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.auth.Transaction
import com.example.moneymitra.ui.viewmodel.TransactionsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onBack: () -> Unit,
    onEdit: (tx: Transaction) -> Unit
) {
    val vm: TransactionsViewModel = viewModel()
    val list by vm.transactions.collectAsState()

    var expandedId by remember { mutableStateOf<String?>(null) }

    var showFilters by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    // Active filters
    var selectedAccount by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedDateFilter by remember { mutableStateOf("All") }

    // Temp filters (used before pressing SET)
    var tempAccount by remember { mutableStateOf("All") }
    var tempCategory by remember { mutableStateOf("All") }
    var tempDate by remember { mutableStateOf("All") }

    val accounts = list.map { it.accountLabel }.distinct()
    val categories = list.map { it.category }.distinct()

    LaunchedEffect(Unit) {
        vm.loadTransactions()
    }

    val filteredList = list.filter { tx ->
        val matchesSearch =
            tx.category.contains(searchQuery, true) ||
                    tx.note.contains(searchQuery, true)

        val matchesAccount =
            selectedAccount == "All" ||
                    tx.accountLabel == selectedAccount

        val matchesCategory =
            selectedCategory == "All" ||
                    tx.category == selectedCategory

        val matchesDate =
            when (selectedDateFilter) {
                "Today" -> {
                    val today = Calendar.getInstance()
                    val txCal = Calendar.getInstance().apply {
                        timeInMillis = tx.createdAt
                    }
                    today.get(Calendar.YEAR) == txCal.get(Calendar.YEAR) &&
                            today.get(Calendar.DAY_OF_YEAR) == txCal.get(Calendar.DAY_OF_YEAR)
                }
                "This Month" -> {
                    val today = Calendar.getInstance()
                    val txCal = Calendar.getInstance().apply {
                        timeInMillis = tx.createdAt
                    }
                    today.get(Calendar.YEAR) == txCal.get(Calendar.YEAR) &&
                            today.get(Calendar.MONTH) == txCal.get(Calendar.MONTH)
                }
                else -> true
            }

        matchesSearch && matchesAccount && matchesCategory && matchesDate
    }
    val colors = MaterialTheme.colorScheme
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        containerColor = colors.background
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .background(colors.background)
        ) {

            /* 🔍 SEARCH + FILTER BUTTON ROW */
            SearchWithFilterBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilters = !showFilters }
            )

            Spacer(Modifier.height(16.dp))

            /* 🔥 FILTER PANEL */
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(5),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        FilterDropdown(
                            label = "Account",
                            options = listOf("All") + accounts,
                            selected = tempAccount,
                            onSelected = { tempAccount = it }
                        )

                        FilterDropdown(
                            label = "Category",
                            options = listOf("All") + categories,
                            selected = tempCategory,
                            onSelected = { tempCategory = it }
                        )

                        FilterDropdown(
                            label = "Date",
                            options = listOf("All", "Today", "This Month"),
                            selected = tempDate,
                            onSelected = { tempDate = it }
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Button(
                                onClick = {
                                    tempAccount = "All"
                                    tempCategory = "All"
                                    tempDate = "All"

                                    selectedAccount = "All"
                                    selectedCategory = "All"
                                    selectedDateFilter = "All"

                                    showFilters = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reset")
                            }

                            Button(
                                onClick = {
                                    selectedAccount = tempAccount
                                    selectedCategory = tempCategory
                                    selectedDateFilter = tempDate
                                    showFilters = false
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Set")
                            }
                        }
                    }
                }

            }
            /* 📋 LIST */
            LazyColumn {
                items(filteredList, key = { it.id }) { tx ->
                    TransactionExpandableCard(
                        tx = tx,
                        expanded = expandedId == tx.id,
                        onClick = {
                            expandedId =
                                if (expandedId == tx.id) null else tx.id
                        },
                        onDelete = { vm.delete(tx) },
                        onEdit = { onEdit(tx) }
                    )
                }
            }
        }
    }
}
@Composable
fun SearchWithFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        shape = RoundedCornerShape(50),
        color = colors.surfaceVariant.copy(alpha = 0.6f),
        tonalElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            // 🔍 Search Icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = colors.onSurfaceVariant
            )

            Spacer(Modifier.width(12.dp))

            // 🔎 Search Text Field
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = colors.onSurface
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search transactions",
                            color = colors.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(Modifier.width(12.dp))

            // Divider
            Divider(
                modifier = Modifier
                    .height(24.dp)
                    .width(1.dp),
                color = colors.outlineVariant
            )

            Spacer(Modifier.width(12.dp))

            // 🎛 Filter Icon
            IconButton(
                onClick = onFilterClick
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filters",
                    tint = colors.onSurface
                )
            }
        }
    }
}
@Composable
fun TransactionExpandableCard(
    tx: Transaction,
    expanded: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
/* ------------------ BOTTOM DIVIDER ------------------ */

        Divider(
            modifier = Modifier.padding(top = 12.dp),
            color = colors.outlineVariant
        )
        Spacer(Modifier.height(10.dp))
        /* ------------------ MAIN ROW ------------------ */

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Icon box (left side)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (tx.type == "EXPENSE")
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
                    text = if (tx.type=="EXPENSE") "Paid to" else "Recived From",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )

                Text(
                    text = toCamelCase(tx.name.ifBlank { tx.type }),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface
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

            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "₹${tx.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (tx.type == "Credit")
                        Color(0xFF2E7D32) // green
                    else
                        colors.onSurface
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    text = tx.accountLabel.toUpperCase()
                )
            }
        }

        /* ------------------ EXPANDED SECTION ------------------ */

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
                        style = MaterialTheme.typography.titleSmall,
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
                            contentColor = Color.Green
                        )
                    ) {
                        Text("Edit")
                    }

                    TextButton(
                        onClick = onDelete,
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
    fun toCamelCase(text: String): String {
        return text
            .lowercase()
            .split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { char ->
                    char.uppercase()
                }
            }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                )
            }
        }
    }
}


