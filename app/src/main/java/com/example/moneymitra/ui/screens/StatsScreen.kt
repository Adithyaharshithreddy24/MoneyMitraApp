package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.data.model.Account
import com.example.moneymitra.data.model.StatsData
import com.example.moneymitra.ui.viewmodel.StatsViewModel
import com.example.moneymitra.ui.viewmodel.TimeFilter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// THEME COLORS
private val BackgroundColor = Color(0xFFF6F7F9)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryOrange = Color(0xFF1A237E)
private val TextDark = Color(0xFF111827)
private val TextGray = Color(0xFF6B7280)

private val LightGreenBg = Color(0xFFF0FDF4)
private val DarkGreenText = Color(0xFF14532D)
private val GreenIcon = Color(0xFF4ADE80)
private val DarkGreenLine = Color(0xFF34D399)

private val LightRedBg = Color(0xFFFFF1F2)
private val DarkRedText = Color(0xFF7F1D1D)
private val RedIcon = Color(0xFFF87171)

private val LightBlueBg = Color(0xFFEFF6FF)
private val DarkBlueText = Color(0xFF1E3A8A)
private val BlueIcon = Color(0xFF60A5FA)

private val LightOrangeBg = Color(0xFFFFFBEB)
private val DarkOrangeText = Color(0xFF78350F)
private val OrangeIcon = Color(0xFFD97706)

private fun formatRupee(amount: Number): String {
    val format = NumberFormat.getNumberInstance(Locale("en", "IN"))
    return "₹${format.format(amount)}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = viewModel()
) {
    val stats = viewModel.stats.value
    val selectedFilter = viewModel.selectedFilter.value
    val accounts = viewModel.accounts.value
    val selectedAccount = viewModel.selectedAccount.value

    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshStats()
    }

    if (stats == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryOrange)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TitleSection(onBack = onBack)
        Spacer(Modifier.height(24.dp))


        AccountDropdown(
            accounts = accounts,
            selectedAccount = selectedAccount,
            onAccountSelected = { viewModel.setSelectedAccount(it) }
        )
        Spacer(Modifier.height(16.dp))

        PeriodTabs(
            selectedFilter = selectedFilter,
            onFilterSelected = { filter ->
                if (filter == TimeFilter.CUSTOM) {
                    showDatePicker = true
                } else {
                    viewModel.setFilter(filter)
                }
            }
        )
        Spacer(Modifier.height(24.dp))

        StatCardsGrid(stats)
        Spacer(Modifier.height(24.dp))

        FinancialTrendsCard(stats)
        Spacer(Modifier.height(24.dp))
        WeeklyPerformanceCard(stats, selectedFilter)
        Spacer(Modifier.height(24.dp))
        SpendingDistributionCard(stats)
        Spacer(Modifier.height(24.dp))
        LendingBorrowingCard(stats)
        Spacer(Modifier.height(50.dp))
    }

    // Modal Date Range Picker with Arrows and Year Selection
    if (showDatePicker) {
        val existingStart = viewModel.customDateRange.value?.first
        val existingEnd = viewModel.customDateRange.value?.second

        var step by remember { mutableIntStateOf(1) }
        var tempStartDate by remember { mutableStateOf<Long?>(existingStart) }
        var tempEndDate by remember { mutableStateOf<Long?>(existingEnd) }

        val datePickerState = key(step) {
            rememberDatePickerState(
                initialSelectedDateMillis = if (step == 1) tempStartDate else tempEndDate
            )
        }

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                if (step == 1) {
                    TextButton(
                        onClick = {
                            if (datePickerState.selectedDateMillis != null) {
                                tempStartDate = datePickerState.selectedDateMillis
                                step = 2
                            }
                        },
                        enabled = datePickerState.selectedDateMillis != null
                    ) { Text("Next", color = PrimaryOrange) }
                } else {
                    TextButton(
                        onClick = {
                            val end = datePickerState.selectedDateMillis
                            if (tempStartDate != null && end != null) {
                                // Ensure start date is mathematically before end date
                                val finalStart = minOf(tempStartDate!!, end)
                                val finalEnd = maxOf(tempStartDate!!, end)
                                viewModel.setCustomDateRange(finalStart, finalEnd)
                                showDatePicker = false
                            }
                        },
                        enabled = datePickerState.selectedDateMillis != null
                    ) { Text("Confirm", color = PrimaryOrange) }
                }
            },
            dismissButton = {
                if (step == 2) {
                    TextButton(onClick = { step = 1 }) { Text("Back") }
                } else {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Select Custom Range",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp),
                        fontSize = 14.sp,
                        color = TextGray
                    )
                },
                headline = {
                    val currentSelected = datePickerState.selectedDateMillis
                    val displayStart = if (step == 1) currentSelected else tempStartDate
                    val displayEnd = if (step == 2) currentSelected else tempEndDate

                    val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val startStr = displayStart?.let { format.format(it) } ?: "Start Date"
                    val endStr = displayEnd?.let { format.format(it) } ?: "End Date"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start // Aligns text cleanly to the left
                    ) {
                        Text(
                            text = startStr,
                            fontSize = 18.sp,
                            fontWeight = if (step == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (step == 1) PrimaryOrange else TextGray
                        )
                        Text(
                            text = "  ➔  ",
                            fontSize = 18.sp,
                            color = TextGray
                        )
                        Text(
                            text = endStr,
                            fontSize = 18.sp,
                            fontWeight = if (step == 2) FontWeight.Bold else FontWeight.Medium,
                            color = if (step == 2) PrimaryOrange else TextGray
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun TitleSection(
    onBack: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack, Modifier.offset(x=(-10).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }
        Column ( Modifier.offset(x=(-10).dp)){
            Text("Statistics", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Text("Financial Insights", fontSize = 14.sp, color = TextGray)
        }
    }
}

@Composable
fun AccountDropdown(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelected: (Account?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val accountDisplayText = selectedAccount?.let { "${it.accName} | ${it.accType}" } ?: "All Accounts"

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardWhite, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = accountDisplayText, fontWeight = FontWeight.Medium, color = TextDark)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand", tint = TextGray)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f).background(CardWhite)
        ) {
            DropdownMenuItem(
                text = { Text("All Accounts") },
                onClick = {
                    onAccountSelected(null)
                    expanded = false
                }
            )
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text("${account.accName} | ${account.accType}") },
                    onClick = {
                        onAccountSelected(account)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun PeriodTabs(selectedFilter: TimeFilter, onFilterSelected: (TimeFilter) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        TabButton("Week", selectedFilter == TimeFilter.WEEK) { onFilterSelected(TimeFilter.WEEK) }
        TabButton("Month", selectedFilter == TimeFilter.MONTH) { onFilterSelected(TimeFilter.MONTH) }
        TabButton("Year", selectedFilter == TimeFilter.YEAR) { onFilterSelected(TimeFilter.YEAR) }
        TabButton("Custom", selectedFilter == TimeFilter.CUSTOM, Icons.Outlined.DateRange) { onFilterSelected(TimeFilter.CUSTOM) }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, icon: ImageVector? = null, onClick: () -> Unit) {
    val bgColor = if (isSelected) PrimaryOrange else CardWhite
    val contentColor = if (isSelected) Color.White else TextDark
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFE5E7EB)

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(24.dp))
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, color = contentColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun StatCardsGrid(stats: StatsData) {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard("TOTAL SAVINGS", formatRupee(stats.totalSavings), Icons.Default.Savings, LightBlueBg, BlueIcon, DarkBlueText, Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            StatCard("NET OUTSTANDING", formatRupee(stats.netOutstanding), Icons.Default.AccountBalanceWallet, LightOrangeBg, OrangeIcon, DarkOrangeText, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard("TOTAL INCOME", formatRupee(stats.monthlyIncome),  Icons.Default.ArrowDownward, LightGreenBg, GreenIcon, DarkGreenText, Modifier.weight(1f))
            Spacer(Modifier.width(16.dp))
            StatCard("TOTAL EXPENSE", formatRupee(stats.monthlyExpense),  Icons.Default.ArrowUpward, LightRedBg, RedIcon, DarkRedText, Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, bgColor: Color, iconColor: Color, textColor: Color, modifier: Modifier) {
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = bgColor), elevation = CardDefaults.cardElevation(0.dp), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = iconColor, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = textColor)
        }
    }
}

@Composable
fun FinancialTrendsCard(stats: StatsData) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Financial Trends", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(Modifier.size(6.dp)) { drawCircle(GreenIcon) }
                    Spacer(Modifier.width(6.dp))
                    Text("INCOME", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.width(12.dp))
                    Canvas(Modifier.size(6.dp)) { drawCircle(RedIcon) }
                    Spacer(Modifier.width(6.dp))
                    Text("EXPENSE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }

            Spacer(Modifier.height(24.dp))
            val maxVal = (stats.incomeTrend + stats.expenseTrend).maxOrNull()?.takeIf { it > 0 } ?: 1f

            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    drawLine(color = Color(0xFFF3F4F6), start = Offset(0f, 0f), end = Offset(w, 0f), strokeWidth = 4f)

                    val incomePoints = stats.incomeTrend.map { it / maxVal }
                    val expensePoints = stats.expenseTrend.map { it / maxVal }

                    if (incomePoints.size > 1 && expensePoints.size > 1) {
                        val stepX = w / (incomePoints.size - 1).toFloat()
                        val drawCurve = { points: List<Float>, color: Color ->
                            val path = Path()
                            path.moveTo(0f, h - (points[0] * h))
                            for (i in 0 until points.size - 1) {
                                val x1 = i * stepX
                                val y1 = h - (points[i] * h)
                                val x2 = (i + 1) * stepX
                                val y2 = h - (points[i + 1] * h)
                                val cp1x = x1 + (x2 - x1) / 2
                                val cp2x = x2 - (x2 - x1) / 2
                                path.cubicTo(cp1x, y1, cp2x, y2, x2, y2)
                            }
                            drawPath(path = path, color = color, style = Stroke(width = 10f, cap = StrokeCap.Round))
                        }
                        drawCurve(incomePoints, DarkGreenLine)
                        drawCurve(expensePoints, RedIcon)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                val labels = stats.trendLabels.ifEmpty { listOf("N/A") }
                labels.forEach { label ->
                    Text(text = label, fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun WeeklyPerformanceCard(stats: StatsData, selectedFilter: TimeFilter) {
    val titleStr =  "Performance\nDetails"

    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(titleStr, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(Modifier.size(6.dp)) { drawCircle(GreenIcon) }
                    Spacer(Modifier.width(6.dp))
                    Text("INCOME", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(Modifier.width(12.dp))
                    Canvas(Modifier.size(6.dp)) { drawCircle(RedIcon) }
                    Spacer(Modifier.width(6.dp))
                    Text("EXPENSE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }

            Spacer(Modifier.height(32.dp))
            val maxVal = (stats.weeklyIncome + stats.weeklyExpense).maxOrNull()?.takeIf { it > 0 } ?: 1f
            val incomeHeights = stats.weeklyIncome.map { it / maxVal }
            val expenseHeights = stats.weeklyExpense.map { it / maxVal }
            val labels = stats.performanceLabels.ifEmpty { listOf("N/A") }

            Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val maxItems = maxOf(1, labels.size)
                val groupWidth = canvasWidth / maxItems.toFloat()

                val barWidth = (groupWidth * 0.35f).coerceAtMost(14.dp.toPx())
                val gap = groupWidth * 0.05f

                for (i in 0 until maxItems) {
                    val centerX = (i * groupWidth) + (groupWidth / 2f)
                    val incomeX = centerX - barWidth - gap
                    val expenseX = centerX + gap
                    val incomeH = incomeHeights.getOrNull(i)?.times(canvasHeight) ?: 0f
                    val expenseH = expenseHeights.getOrNull(i)?.times(canvasHeight) ?: 0f
                    val alpha = if (i == maxItems - 1) 1f else 0.4f

                    drawRoundRect(color = DarkGreenLine, topLeft = Offset(incomeX, canvasHeight - incomeH), size = Size(barWidth, incomeH), cornerRadius = CornerRadius(barWidth / 2, barWidth / 2), alpha = alpha)
                    drawRoundRect(color = RedIcon, topLeft = Offset(expenseX, canvasHeight - expenseH), size = Size(barWidth, expenseH), cornerRadius = CornerRadius(barWidth / 2, barWidth / 2), alpha = alpha)
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                labels.forEach { label ->
                    Text(text = label, fontSize = 8.sp, color = TextGray, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SpendingDistributionCard(stats: StatsData) {
    val totalSpent = stats.categorySpending.sumOf { it.amount }

    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = CardWhite)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text("Spending Distribution", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val strokeWidth = 36f
                    drawCircle(color = Color(0xFFF3F4F6), style = Stroke(width = strokeWidth))

                    var currentStartAngle = -200f
                    if (totalSpent > 0) {
                        stats.categorySpending.forEach { category ->
                            val sweepAngle = (category.amount.toFloat() / totalSpent.toFloat()) * 360f
                            drawArc(color = Color(category.colorHex).copy(alpha = 0.9f), startAngle = currentStartAngle, sweepAngle = sweepAngle, useCenter = false, style = Stroke(width = strokeWidth, cap = StrokeCap.Butt))
                            currentStartAngle += sweepAngle
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(formatRupee(totalSpent), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = TextDark)
                    Text("TOTAL SPENT", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
            Spacer(Modifier.height(32.dp))
            if (stats.categorySpending.isEmpty()) {
                Text("No spending data available.", fontSize = 12.sp, color = TextGray)
            } else {
                stats.categorySpending.forEach { category ->
                    CategoryListBar(category.name, formatRupee(category.amount), Color(category.colorHex), category.percentage)
                }
            }
        }
    }
}

@Composable
fun CategoryListBar(name: String, value: String, color: Color, progress: Float) {
    Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Canvas(Modifier.size(8.dp)) { drawCircle(color) }
            Spacer(Modifier.width(8.dp))
            Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
            Spacer(Modifier.weight(1f))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        }
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFFF3F4F6), RoundedCornerShape(3.dp))) {
            Box(modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(6.dp).background(color, RoundedCornerShape(3.dp)))
        }
    }
}

@Composable
fun LendingBorrowingCard(stats: StatsData) {
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(0.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Lending & Borrowing", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Spacer(Modifier.height(24.dp))
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text("MONEY LENT", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(formatRupee(stats.moneyLent), color = DarkGreenLine, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(DarkGreenLine, shape = CircleShape))
                        Spacer(Modifier.width(6.dp))
                        Text("${formatRupee(stats.moneyLentPending)} Pending", fontSize = 11.sp, color = TextGray)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("MONEY BORROWED", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(formatRupee(stats.moneyBorrowed), color = RedIcon, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).background(RedIcon, shape = CircleShape))
                        Spacer(Modifier.width(6.dp))
                        Text("${formatRupee(stats.moneyBorrowedPending)} Pending", fontSize = 11.sp, color = TextGray)
                    }
                }
            }
        }
    }
}