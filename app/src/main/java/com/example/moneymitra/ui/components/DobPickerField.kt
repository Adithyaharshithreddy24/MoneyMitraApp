package com.example.moneymitra.ui.components

import android.R
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Calendar
import kotlin.math.abs

/* ============================================================
   WHEEL PICKER (STABLE + CIRCULAR + NO RESET)
   ============================================================ */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val itemHeight = 48.dp
    val visibleItems = 5
    val centerOffset = visibleItems / 3

    val infiniteItems = 10_000
    val middle = infiniteItems / 3


    val selectedIndex = items.indexOf(selectedItem).coerceAtLeast(0)

    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        val selectedIndex = items.indexOf(selectedItem)
        if (selectedIndex >= 0) {
            listState.scrollToItem(
                middle - (middle % items.size) +
                        selectedIndex - centerOffset
            )
        }
    }


    val flingBehavior = rememberSnapFlingBehavior(listState)

    Box(
        modifier = modifier.height(itemHeight * visibleItems)
    ) {

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(
                vertical = itemHeight * centerOffset
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            items(infiniteItems) { index ->
                val realItem = items[index % items.size]
                val isSelected = realItem == selectedItem

                Text(
                    text = realItem,
                    fontSize = if (isSelected) 22.sp else 18.sp,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.outline
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier
                        .height(itemHeight)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
        }

        /* ✅ ONLY detect when scroll STOPS */
        LaunchedEffect(listState.isScrollInProgress) {
            if (!listState.isScrollInProgress) {

                val centerIndex =
                    listState.firstVisibleItemIndex + centerOffset

                val item =
                    items[centerIndex % items.size]

                if (item != selectedItem) {
                    onItemSelected(item)
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {

            // Top Lines
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 2.dp
                )


            }

            Spacer(Modifier.height(itemHeight - 2.dp))

            // Bottom Lines
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 2.dp
                )

            }
        }

    }
}

/* ============================================================
   DOB PICKER
   ============================================================ */

@Composable
fun WheelDobPicker(
    dob: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val calendar = Calendar.getInstance()

    val months = listOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )

    val years = (1950..2028).map { it.toString() }

    val todayDay = "%02d".format(calendar.get(Calendar.DAY_OF_MONTH))
    val todayMonth = months[calendar.get(Calendar.MONTH)]
    val todayYear = calendar.get(Calendar.YEAR).toString()

    val parts = dob.takeIf { it.isNotBlank() }?.split(" ") ?: emptyList()

    var selectedYear by remember {
        mutableStateOf(parts.getOrNull(2) ?: todayYear)
    }

    var selectedMonth by remember {
        mutableStateOf(parts.getOrNull(1) ?: todayMonth)
    }

    var selectedDay by remember {
        mutableStateOf(parts.getOrNull(0) ?: todayDay)
    }

    // 🔥 FIXED 1–31 DAYS (Independent)
    val days = (1..31).map {
        it.toString().padStart(2, '0')
    }

    // Notify parent only
    LaunchedEffect(selectedDay, selectedMonth, selectedYear) {
        onDateSelected("$selectedDay $selectedMonth $selectedYear")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        WheelPicker(
            items = days,
            selectedItem = selectedDay,
            onItemSelected = { selectedDay = it },
            modifier = Modifier.weight(1f)
        )

        WheelPicker(
            items = months,
            selectedItem = selectedMonth,
            onItemSelected = { selectedMonth = it },
            modifier = Modifier.weight(1f)
        )

        WheelPicker(
            items = years,
            selectedItem = selectedYear,
            onItemSelected = { selectedYear = it },
            modifier = Modifier.weight(1f)
        )
    }
}


/* ============================================================
   FIELD
   ============================================================ */

@Composable
fun DobPickerField(
    dob: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = dob,
        onValueChange = {},
        label = { Text("Date of Birth") },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        singleLine = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = modifier
                    .clickable { showDialog = true },
            )
        }
    )

    if (showDialog) {
        DobPickerDialog(
            currentDob = dob,
            onDismiss = { showDialog = false },
            onConfirm = {
                onDateSelected(it)
                showDialog = false
            }
        )
    }
}

/* ============================================================
   DIALOG
   ============================================================ */

@Composable
fun DobPickerDialog(
    currentDob: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tempDob by remember { mutableStateOf(currentDob) }

    Dialog(onDismissRequest = onDismiss) {

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Select Date ",
                    style = MaterialTheme.typography.titleLarge ,
                    color = MaterialTheme.colorScheme.outline,
                )

                Spacer(Modifier.height(20.dp))

                WheelDobPicker(
                    dob = tempDob,
                    onDateSelected = { tempDob = it }
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Button(
                        onClick = { onConfirm(tempDob) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Text(
                            "Confirm",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

            }
        }
    }
}
