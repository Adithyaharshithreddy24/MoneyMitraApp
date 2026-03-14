package com.example.moneymitra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymitra.data.model.Account
import com.example.moneymitra.ui.viewmodel.AddAccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountBottomSheet(
    onDismiss: () -> Unit,
    onAccountSaved: () -> Unit,
    viewModel: AddAccountViewModel = viewModel()
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val colors = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = colors.surface,   // 🔥 Theme adaptive
        tonalElevation = 6.dp
    ) {
        AddAccountSheetContent(
            onAccountSaved = onAccountSaved
        )
    }
}

@Composable
fun AddAccountSheetContent(
    onAccountSaved: () -> Unit,
    viewModel: AddAccountViewModel = viewModel()
) {

    val colors = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    var accName by remember { mutableStateOf("") }
    var accType by remember { mutableStateOf("") }
    var accNo by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val buttonGradient = if (isDark) {
        Brush.horizontalGradient(
            listOf(Color(0xFF283593), Color(0xFF5C6BC0))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color.Black, Color(0xFF282B8C))
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /* Title */
        Text(
            text = "Add Account",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        StyledTextField(accName, "Account Name") { accName = it }
        StyledTextField(bankName, "Bank Name") { bankName = it }
        StyledTextField(accType, "Account Type") { accType = it }
        StyledTextField(accNo, "Account Number") { accNo = it }
        StyledTextField(balance, "Balance") { balance = it }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = colors.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        /* Save Button */

        Button(
            onClick = {

                if (loading) return@Button

                if (accName.isBlank() ||accType.isBlank() || accNo.isBlank() || bankName.isBlank() || balance.isBlank()) {
                    error = "Please fill required details"
                    return@Button
                }

                loading = true

                viewModel.saveAccount(
                    account = Account(
                        accName = accName,
                        accType = accType,
                        accNo = accNo,
                        bankName = bankName,
                        balance = balance.toDoubleOrNull() ?: 0.0
                    ),
                    onSuccess = {
                        loading = false
                        onAccountSaved()
                    },
                    onError = {
                        loading = false
                        error = it
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            enabled = !loading
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = buttonGradient,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                if (loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Save Account",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
@Composable
fun StyledTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {

    val colors = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                buildAnnotatedString {
                    append(label)
                    append(" ")
                    withStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.error)
                    ) {
                        append("*")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.outline
        ),
    )
}
