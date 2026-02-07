package com.example.moneymitra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.moneymitra.ui.components.*

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onGridClick: () -> Unit,
    onAddClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTransactionClick: () -> Unit,
    onChitFunds: () -> Unit,
    onGoals: () -> Unit,
    onLoans: () -> Unit
) {
    var assistantOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        /* ---------- LOGOUT (TOP LEFT) ---------- */
        TextButton(
            onClick = onLogout,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .zIndex(3f)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = Color.Red
            )
            Spacer(Modifier.width(6.dp))
            Text("Logout", color = Color.Red)
        }

        /* ---------- PROFILE (TOP RIGHT) ---------- */
        ProfileIcon(
            onClick = onProfileClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(3f)
        )

        /* ---------- ASSISTANT FAB (BOTTOM RIGHT) ---------- */
        AssistantFab(
            onClick = { assistantOpen = !assistantOpen },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp)
                .zIndex(5f)

        )

        /* ---------- ASSISTANT RADIAL MENU ---------- */
        if (assistantOpen) {
            AssistantRadialMenu(
                expanded = true,
                onDismiss = { assistantOpen = false },
                onChitFunds = onChitFunds,
                onGoals = onGoals,
                onLoans = onLoans
            )
        }

        /* ---------- BOTTOM NAV ---------- */
        BottomNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f),
            onHomeClick = onHomeClick,
            onGridClick = onGridClick,
            onNotificationClick = onNotificationClick,
            onTransactionClick = onTransactionClick
        )

        /* ---------- + ADD FAB (CENTER) ---------- */
        CenterAddFab(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 25.dp)
                .size(70.dp)
                .zIndex(5f)
        )
    }
}
