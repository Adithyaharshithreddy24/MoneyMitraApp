package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.ui.unit.offset
import com.example.moneymitra.R
import com.example.moneymitra.ui.components.*
import com.example.moneymitra.ui.theme.Lovelo
import com.example.moneymitra.ui.viewmodel.HomeViewModel
import com.example.moneymitra.ui.viewmodel.TransactionsViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    val viewModel: HomeViewModel = viewModel()

    val name by viewModel.name.collectAsState()
    val upiId by viewModel.upiId.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    val currentBudget by viewModel.budget.collectAsState()
    val budgetProgress by viewModel.budgetProgress.collectAsState()


    val txVm: TransactionsViewModel = viewModel()
    val recentTx by txVm.recentTransactions.collectAsState()

    LaunchedEffect(Unit) {
        txVm.loadTransactions()
    }

    // 🔹 Dynamic Month
    val currentMonth = remember {
        SimpleDateFormat("MMMM", Locale.getDefault())
            .format(Date())
    }

    var activeFab by remember { mutableStateOf(FabMenu.NONE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFAFAFAFA))
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
            ) {

                // ---------- HEADER ----------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF000000),
                                    Color(0xFF1A237E)
                                )
                            ),
                            shape = RoundedCornerShape(
                                bottomStart = 30.dp,
                                bottomEnd = 30.dp
                            )
                        )
                ) {

                    Column(
                        Modifier.padding(16.dp, 8.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(R.drawable.logo_white),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )

                                Text(
                                    text = "ONEY MITRA",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Lovelo,
                                    color = Color.White,
                                    modifier = Modifier.offset(y = 12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Welcome.!",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = toCamelCase(name),
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // ---------- BALANCE ----------
                BalanceCard(
                    balance = balance,
                    categories = categories,
                    totalExpense = totalExpense
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ---------- BUDGET ----------
                BudgetCard(
                    month = currentMonth,
                    budget = currentBudget,
                    expense = totalExpense,
                    progress = budgetProgress,
                    onSetBudget = { newBudget ->
                        viewModel.setBudget(newBudget)
                    },
                    onEditBudget = { newBudget ->
                        viewModel.updateBudget(newBudget)
                    }
                )

                // ---------- LENDING / BORROWING ----------
                LendingBorrowingSection()

                RecentTransactionsDashboard(
                    transactions = recentTx,
                    onViewAll = {
                        onTransactionClick()   // navigates to TransactionsScreen
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                // ---------- QR ----------
                QrSection(
                    upiId = upiId
                )

                Spacer(Modifier.height(100.dp))
            }
        }

        // ---------- ASSISTANT FAB ----------
        if (activeFab != FabMenu.ASSISTANT )
        {
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
        }

        if (activeFab == FabMenu.ASSISTANT) {
            AssistantRadialMenu(
                expanded = true,
                onDismiss = { activeFab = FabMenu.NONE },
                onChitFunds = onChitFunds,
                onGoals = onGoals,
                onLoans = onLoans
            )
        }

        // ---------- BOTTOM NAV ----------
        BottomNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            onHomeClick = onHomeClick,
            onGridClick = onGridClick,
            onNotificationClick = onNotificationClick,
            onProfileClick = onProfileClick
        )

        // ---------- CENTER FAB ----------
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