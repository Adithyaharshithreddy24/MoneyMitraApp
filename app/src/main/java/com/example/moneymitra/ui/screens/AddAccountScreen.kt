package com.example.moneymitra.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
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
    var accName by remember { mutableStateOf("") }
    var accType by remember { mutableStateOf("") }
    var accNo by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(20.dp,0.dp,20.dp,0.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add Account",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }


        StyledTextField(accName, "Account Name") { accName = it }
        StyledTextField(bankName, "Bank Name") { bankName = it }
        StyledTextField(accType, "Account Type") { accType = it }
        StyledTextField(accNo, "Account Number") { accNo = it }
        StyledTextField(balance, "Balance") { balance = it }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(0.dp))

        var loading by remember { mutableStateOf(false) }

        Button(
            onClick = {
                if (loading) return@Button

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
                .height(52.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF000000),
                            Color(0xFF282B8C)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                ),
            shape = RoundedCornerShape(14.dp),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
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
        Spacer(modifier = Modifier.height(10.dp))

    }
}
@Composable
fun StyledTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )
}
