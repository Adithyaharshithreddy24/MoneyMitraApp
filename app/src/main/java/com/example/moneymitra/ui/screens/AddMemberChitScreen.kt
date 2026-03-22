package com.example.moneymitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Chit
import com.example.moneymitra.viewmodel.ChitViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberChitScreen(
    navController: NavController,
    viewModel: ChitViewModel = viewModel()
) {

    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    var name by remember { mutableStateOf("") }
    var manager by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Chit") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(innerPadding) // ✅ FIX
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.height(8.dp))

            // 🔹 INPUT FIELDS
            CustomTextField(name, { name = it }, "Chit Name")
            Spacer(Modifier.height(10.dp))

            CustomTextField(manager, { manager = it }, "Manager Name")
            Spacer(Modifier.height(10.dp))

            CustomTextField(amount, { amount = it }, "Total Amount")
            Spacer(Modifier.height(10.dp))

            CustomTextField(months, { months = it }, "Months")
            Spacer(Modifier.height(10.dp))

            // 🔹 DATE FIELD
            val formattedDate = startDate?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
            } ?: ""

            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Start Date") },
                placeholder = { Text("Select date") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔥 SAVE BUTTON
            Button(
                onClick = {

                    val total = amount.toDoubleOrNull()
                    val mths = months.toIntOrNull()

                    if (name.isBlank() || manager.isBlank() || total == null || mths == null || startDate == null) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (mths <= 0) {
                        Toast.makeText(context, "Months must be > 0", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val chit = Chit(
                        name = name.trim(),
                        managerName = manager.trim(),
                        totalAmount = total,
                        months = mths,
                        monthlyAmount = total / (mths-1),
                        startDate = startDate!!
                    )

                    viewModel.addMemberChit(chit) {
                        Toast.makeText(context, "Chit Saved", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Save")
            }
        }

        // 🔥 DATE PICKER
        if (showDatePicker) {
            val state = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        state.selectedDateMillis?.let { startDate = it }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = state)
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onChange: (String) -> Unit,
    label: String
) {
    val colors = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colors.outline,
            unfocusedBorderColor = colors.outline,
            focusedLabelColor = colors.outline,
            cursorColor = colors.outline
        )
    )
}