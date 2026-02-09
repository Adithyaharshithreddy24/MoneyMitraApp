package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.moneymitra.R
import com.example.moneymitra.ui.components.*
import com.example.moneymitra.ui.theme.Lovelo

enum class FabMenu {
    NONE,
    ADD,
    ASSISTANT
}

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit,
    onGridClick: () -> Unit,
    onManual: () -> Unit,
    onScan:()->Unit,
    onUpload:()->Unit,
    onNotificationClick: () -> Unit,
    onTransactionClick: () -> Unit,
    onChitFunds: () -> Unit,
    onGoals: () -> Unit,
    onLoans: () -> Unit
) {

    var activeFab by remember { mutableStateOf(FabMenu.NONE) }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(0.dp,12.dp,8.dp,8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_color),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
                    .padding(13.dp,0.dp,0.dp,17.dp)
            )
            Text(
                text = "oney Mitra",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Lovelo
            )
        }
        /* ---------- PROFILE (TOP RIGHT) ---------- */
        ProfileIcon(
            onClick = onProfileClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .zIndex(3f)
                .size(40.dp)
        )

        /* ---------- ASSISTANT FAB (BOTTOM RIGHT) ---------- */
        AssistantFab(
            onClick = {
                activeFab =
                    if (activeFab == FabMenu.ASSISTANT)
                        FabMenu.NONE
                    else
                        FabMenu.ASSISTANT
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp)
                .zIndex(10f)
        )


        /* ---------- ASSISTANT RADIAL MENU ---------- */
        if (activeFab == FabMenu.ASSISTANT) {
            AssistantRadialMenu(
                expanded = true,
                onDismiss = { activeFab = FabMenu.NONE },
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
            onClick = {
                activeFab =
                    if (activeFab == FabMenu.ADD)
                        FabMenu.NONE
                    else
                        FabMenu.ADD
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 25.dp)
                .size(70.dp)
                .zIndex(10f)
        )

        if (activeFab == FabMenu.ADD) {
            AddRadialMenu(
                expanded = true,
                onDismiss = { activeFab = FabMenu.NONE },
                onManual = onManual,
                onScan = onScan,
                onUpload = onUpload
            )
        }

    }
}
