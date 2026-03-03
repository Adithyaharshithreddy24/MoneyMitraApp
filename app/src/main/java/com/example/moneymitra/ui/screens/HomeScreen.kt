package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.R
import com.example.moneymitra.ui.components.*
import com.example.moneymitra.ui.theme.Lovelo
import com.example.moneymitra.ui.viewmodel.HomeViewModel

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
    onScan: () -> Unit,
    onUpload: () -> Unit,
    onNotificationClick: () -> Unit,
    onTransactionClick: () -> Unit,
    onChitFunds: () -> Unit,
    onGoals: () -> Unit,
    onLoans: () -> Unit
) {

    val isDark = isSystemInDarkTheme()
    val colors = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val viewModel: HomeViewModel = viewModel()

    val balance by viewModel.balance.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    var activeFab by remember { mutableStateOf(FabMenu.NONE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)   // 🔥 theme adaptive
    ) {

        /* ---------- TOP BAR ---------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(
                    if (isDark) R.drawable.logo_white
                    else R.drawable.logo_color
                ),
                contentDescription = null,
                modifier = Modifier.size(48.dp) // smaller & clean
            )

            Text(
                text = "ONEY MITRA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Lovelo,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.offset(y = (12).dp) // 🔥 Pull text closer
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            BalanceCard(
                balance = balance,
                categories = categories,
                totalExpense = totalExpense
            )

            LendingBorrowingSection()

            QrSection(
                upiId = "7815805576@axl" // later replace with user.uipiId
            )

            Spacer(Modifier.height(100.dp))
        }
        /* ---------- PROFILE ICON ---------- */

        ProfileIcon(
            onClick = onProfileClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(16.dp)
                .size(42.dp)
        )

        /* ---------- ASSISTANT FAB ---------- */

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
                .padding(
                    end = 16.dp,
                    bottom = 96.dp
                )
                .zIndex(10f)
        )

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
                .navigationBarsPadding(),   // 🔥 safe area
            onHomeClick = onHomeClick,
            onGridClick = onGridClick,
            onNotificationClick = onNotificationClick,
            onTransactionClick = onTransactionClick
        )

        /* ---------- CENTER ADD FAB ---------- */

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
                .padding(bottom = 28.dp)
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
