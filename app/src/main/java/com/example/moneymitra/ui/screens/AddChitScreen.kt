package com.example.moneymitra.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Chit
import com.example.moneymitra.data.model.Member
import com.example.moneymitra.viewmodel.ChitViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChitScreen(
    navController: NavController
) {

    val viewModel: ChitViewModel = viewModel()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // 🔹 Focus
    val nameFocus = remember { FocusRequester() }
    val amountFocus = remember { FocusRequester() }
    val monthsFocus = remember { FocusRequester() }

    // 🔹 Inputs
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }

    val members = remember { mutableStateListOf<Member>() }

    var showSheet by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }

    var memberName by remember { mutableStateOf("") }
    var memberEmail by remember { mutableStateOf("") }
    var memberPhone by remember { mutableStateOf("") }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.outline,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.outline
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Chit") },
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
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // 🔥 NAME
            OutlinedTextField(
                value = toCamelCase(name),
                onValueChange = { name = it },
                label = { Text("Chit Name") },
                singleLine = true,
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocus),
                keyboardActions = KeyboardActions(
                    onNext = { amountFocus.requestFocus() }
                )
            )

            // 🔥 AMOUNT
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Total Amount") },
                singleLine = true,
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(amountFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { monthsFocus.requestFocus() }
                )
            )

            // 🔥 MONTHS
            OutlinedTextField(
                value = months,
                onValueChange = { months = it },
                label = { Text("Months") },
                singleLine = true,
                colors = textFieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(monthsFocus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            // 🔥 DATE
            val formattedDate = startDate?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
            } ?: ""

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = formattedDate,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select date") },
                    label = { Text("Start Date") },
                    colors = textFieldColors,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔥 MEMBERS
            members.forEachIndexed { index, member ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedIndex = index }
                        .padding(vertical = 8.dp)
                ) {
                    Text("${index + 1}. ${toCamelCase(member.name)}")

                    DropdownMenu(
                        expanded = selectedIndex == index,
                        onDismissRequest = { selectedIndex = null }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                editingIndex = index
                                memberName = member.name
                                memberEmail = member.email
                                memberPhone = member.phone
                                showSheet = true
                                selectedIndex = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                members.removeAt(index)
                                selectedIndex = null
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (members.size >= (months.toIntOrNull() ?: 0)) {
                        Toast.makeText(context, "Max members reached", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    editingIndex = null // 🔥 RESET MODE
                    memberName = ""
                    memberEmail = ""
                    memberPhone = ""
                    showSheet = true
                },
                modifier = Modifier.fillMaxWidth()

            ) {
                Text("Add Member")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔥 CREATE CHIT
            Button(
                onClick = {

                    val total = amount.toDoubleOrNull()
                    val mths = months.toIntOrNull()

                    if (name.isBlank() || total == null || mths == null || startDate == null) {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (members.size != mths) {
                        Toast.makeText(context, "Members must match months", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                    val chit = Chit(
                        name = name,
                        totalAmount = total,
                        months = mths,
                        monthlyAmount = total / (mths-1),
                        managerId = uid,
                        managerName = "You",
                        startDate = startDate!!
                    )

                    viewModel.addManagerChit(chit, members) {
                        Toast.makeText(context, "Chit Created", Toast.LENGTH_SHORT).show()

                        // 🔥 GO BACK AFTER SAVE
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Chit")
            }
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

    // 🔥 MEMBER SHEET
    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {

            val memberNameFocus = remember { FocusRequester() }
            val memberEmailFocus = remember { FocusRequester() }
            val memberPhoneFocus = remember { FocusRequester() }
            LaunchedEffect(showSheet) {
                if (showSheet) memberNameFocus.requestFocus()
            }
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .imePadding()
            ) {


                OutlinedTextField(
                    value = toCamelCase(memberName),
                    onValueChange = { memberName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { memberEmailFocus.requestFocus() }
                    ),
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(memberNameFocus)
                )

                OutlinedTextField(
                    value = memberEmail,
                    onValueChange = { memberEmail = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { memberPhoneFocus.requestFocus() }
                    ),
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(memberEmailFocus)
                )

                OutlinedTextField(
                    value = memberPhone,
                    onValueChange = { memberPhone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = textFieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(memberPhoneFocus)
                )

                Button(
                    onClick = {
                        val m = Member("",memberName, memberEmail, memberPhone,0,"",false,0)
                        if (editingIndex == null) members.add(m)
                        else members[editingIndex!!] = m
                        showSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Member")
                }
            }
        }
    }
}