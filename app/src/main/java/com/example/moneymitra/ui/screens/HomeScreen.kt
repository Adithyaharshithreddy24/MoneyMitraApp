package com.example.moneymitra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onSendMail: () -> Unit,
    onLogout: () -> Unit,
    onEditProfile:()-> Unit

) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("You are logged in ✅", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onSendMail) {
            Text("Send Mail")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onLogout) {
            Text("Logout")
        }
        OutlinedButton(onClick = onEditProfile) {
            Text("Edit Profile")
        }
    }
}

