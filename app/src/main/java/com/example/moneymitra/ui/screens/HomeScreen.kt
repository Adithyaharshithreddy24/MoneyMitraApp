package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.moneymitra.ui.viewmodel.TransactionsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class FabMenu { NONE, ADD, ASSISTANT }

// Theme Colors
private val BackgroundColor = Color(0xFFF6F7F9)
private val PrimaryDark = Color(0xFF1A237E)

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

    val currentMonth = remember { SimpleDateFormat("MMMM", Locale.getDefault()).format(Date()) }
    var activeFab by remember { mutableStateOf(FabMenu.NONE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ---------- HEADER ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp) // Slightly taller to allow for the overlap
                    .background(
                        brush = Brush.linearGradient(listOf(Color.Black, PrimaryDark)),
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                    )
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp) // Handle status bar padding here
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .offset(y=(-15).dp),
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Welcome back,", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = toCamelCase(name), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ---------- OVERLAPPING CONTENT GROUP ----------
            // We group all the cards in one column and offset the whole group up
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp) // Single clean offset
                    .padding(bottom = 100.dp), // Padding for bottom nav/FABs
                verticalArrangement = Arrangement.spacedBy(20.dp) // Consistent gap between cards
            ) {
                BalanceCard(balance = balance, categories = categories, totalExpense = totalExpense)

                BudgetCard(
                    month = currentMonth,
                    budget = currentBudget,
                    expense = totalExpense,
                    progress = budgetProgress,
                    onSetBudget = { viewModel.setBudget(it) },
                    onEditBudget = { viewModel.updateBudget(it) }
                )

                RecentTransactionsDashboard(
                    transactions = recentTx,
                    onViewAll = onTransactionClick
                )

                QrSection(upiId = upiId)
            }
        }

        // ---------- FABS & BOTTOM NAV (Unchanged logic) ----------
        if (activeFab != FabMenu.ASSISTANT) {
            AssistantFab(
                onClick = { activeFab = if (activeFab == FabMenu.ASSISTANT) FabMenu.NONE else FabMenu.ASSISTANT },
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 96.dp).zIndex(10f)
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

        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
            onHomeClick = onHomeClick,
            onGridClick = onGridClick,
            onNotificationClick = onNotificationClick,
            onProfileClick = onProfileClick
        )

        CenterAddFab(
            onClick = { activeFab = if (activeFab == FabMenu.ADD) FabMenu.NONE else FabMenu.ADD },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp).size(70.dp).zIndex(10f)
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