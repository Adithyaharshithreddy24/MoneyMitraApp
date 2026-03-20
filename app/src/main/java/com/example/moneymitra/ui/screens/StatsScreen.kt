package com.example.moneymitra.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// THEME COLORS
private val BackgroundColor = Color(0xFFF6F7F9)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryOrange = Color(0xFFE26A2C)
private val TextDark = Color(0xFF111827)
private val TextGray = Color(0xFF6B7280)

// CHART & STAT COLORS
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

@Composable
fun StatsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TopHeader()

        Spacer(Modifier.height(24.dp))

        TitleSection()

        Spacer(Modifier.height(24.dp))

        StatCardsGrid()

        Spacer(Modifier.height(24.dp))

        AccountDropdown()

        Spacer(Modifier.height(16.dp))

        PeriodTabs()

        Spacer(Modifier.height(24.dp))

        FinancialTrendsCard()

        Spacer(Modifier.height(24.dp))

        WeeklyPerformanceCard()

        Spacer(Modifier.height(24.dp))

        SpendingDistributionCard()

        Spacer(Modifier.height(24.dp))

        LendingBorrowingCard()

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryOrange, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Text("Money Mitra", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextDark)

        Spacer(Modifier.weight(1f))

        // Notification Bell
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, CircleShape)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun TitleSection() {
    Column {
        Text("Statistics", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
        Spacer(Modifier.height(4.dp))
        Text("Financial Insights", fontSize = 14.sp, color = TextGray)
    }
}

@Composable
fun AccountDropdown() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("All Accounts", fontWeight = FontWeight.Medium, color = TextDark)
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand", tint = TextGray)
    }
}

@Composable
fun PeriodTabs() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TabButton(text = "Week", isSelected = true)
        TabButton(text = "Month", isSelected = false)
        TabButton(text = "Year", isSelected = false)
        TabButton(text = "Custom", isSelected = false, icon = Icons.Outlined.DateRange)
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, icon: ImageVector? = null) {
    val bgColor = if (isSelected) PrimaryOrange else CardWhite
    val contentColor = if (isSelected) Color.White else TextDark
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFE5E7EB)

    Row(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(24.dp))
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
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
fun StatCardsGrid() {
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                title = "TOTAL SAVINGS",
                value = "₹1,27,930",
                subText = "+12% from last month",
                icon = Icons.Default.Savings,
                bgColor = LightBlueBg,
                iconColor = BlueIcon,
                textColor = DarkBlueText,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            StatCard(
                title = "NET OUTSTANDING",
                value = "₹32,400",
                subText = "3 active payments",
                icon = Icons.Default.AccountBalanceWallet,
                bgColor = LightOrangeBg,
                iconColor = OrangeIcon,
                textColor = DarkOrangeText,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                title = "AVG. MONTHLY INCOME",
                value = "₹82,500",
                subText = null,
                icon = Icons.Default.ArrowDownward,
                bgColor = LightGreenBg,
                iconColor = GreenIcon,
                textColor = DarkGreenText,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(16.dp))
            StatCard(
                title = "AVG. MONTHLY EXPENSE",
                value = "₹54,200",
                subText = null,
                icon = Icons.Default.ArrowUpward,
                bgColor = LightRedBg,
                iconColor = RedIcon,
                textColor = DarkRedText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subText: String?,
    icon: ImageVector,
    bgColor: Color,
    iconColor: Color,
    textColor: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(12.dp))
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = iconColor, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = textColor)

            if (subText != null) {
                Spacer(Modifier.height(6.dp))
                Text(subText, fontSize = 10.sp, color = iconColor)
            }
        }
    }
}

@Composable
fun FinancialTrendsCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            // Chart area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw chart border top line
                    drawLine(
                        color = Color(0xFFF3F4F6),
                        start = Offset(0f, 0f),
                        end = Offset(w, 0f),
                        strokeWidth = 4f
                    )

                    // Normalized points (y inverted so 0 is at bottom, 1 is top)
                    val incomePoints = listOf(0.1f, 0.2f, 0.45f, 0.1f, 0.8f, 0.3f)
                    val expensePoints = listOf(0.1f, 0.15f, 0.3f, 0.05f, 0.4f, 0.2f)

                    val stepX = w / (incomePoints.size - 1)

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

                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(width = 10f, cap = StrokeCap.Round)
                        )
                    }

                    drawCurve(incomePoints, DarkGreenLine)
                    drawCurve(expensePoints, RedIcon)
                }
            }

            Spacer(Modifier.height(16.dp))

            // X-axis labels
            val months = listOf("DEC", "JAN", "FEB", "MAR", "APR", "MAY")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                months.forEach { month ->
                    Text(month, fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WeeklyPerformanceCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text("Weekly\nPerformance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)

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

            val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            val incomeHeights = listOf(0.7f, 0.4f, 0.9f, 0.45f, 0.9f, 0.25f, 0.4f)
            val expenseHeights = listOf(0.4f, 0.3f, 0.5f, 0.6f, 0.3f, 0.6f, 0.3f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val barWidth = 7.dp.toPx()
                val gap = 2.dp.toPx()
                val groupWidth = canvasWidth / 7f

                for (i in 0..6) {
                    val centerX = (i * groupWidth) + (groupWidth / 2f)
                    val incomeX = centerX - barWidth - gap
                    val expenseX = centerX + gap

                    val incomeH = incomeHeights[i] * canvasHeight
                    val expenseH = expenseHeights[i] * canvasHeight

                    val alpha = if (i == 0) 0.3f else 1f

                    drawRoundRect(
                        color = DarkGreenLine,
                        topLeft = Offset(incomeX, canvasHeight - incomeH),
                        size = Size(barWidth, incomeH),
                        cornerRadius = CornerRadius(barWidth / 2, barWidth / 2),
                        alpha = alpha
                    )

                    drawRoundRect(
                        color = RedIcon,
                        topLeft = Offset(expenseX, canvasHeight - expenseH),
                        size = Size(barWidth, expenseH),
                        cornerRadius = CornerRadius(barWidth / 2, barWidth / 2),
                        alpha = alpha
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                days.forEach { day ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(day, fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SpendingDistributionCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "Spending Distribution",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    val strokeWidth = 36f

                    // Draw the background gray ring (rarely visible in this design)
                    drawCircle(
                        color = Color(0xFFF3F4F6),
                        style = Stroke(width = strokeWidth)
                    )

                    // Draw the primary green arc (Takes up majority)
                    drawArc(
                        color = DarkGreenLine,
                        startAngle = -200f,
                        sweepAngle = 310f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )

                    // Draw the smaller purple bottom arc
                    drawArc(
                        color = Color(0xFFA855F7), // Purple
                        startAngle = 110f,
                        sweepAngle = 50f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("₹45.5k", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = TextDark)
                    Text("TOTAL SPENT", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            Spacer(Modifier.height(32.dp))

            CategoryListBar("Food & Dining", "₹10,250", PrimaryOrange, 0.6f)
            CategoryListBar("Shopping", "₹8,200", BlueIcon, 0.45f)
            CategoryListBar("Transport", "₹6,400", DarkGreenLine, 0.35f)
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color(0xFFF3F4F6), RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(6.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
fun LendingBorrowingCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                "Lending & Borrowing",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(24.dp))

            Row {
                // MONEY LENT
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "MONEY LENT",
                        fontSize = 11.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "₹18,000",
                        color = DarkGreenLine,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(DarkGreenLine, shape = CircleShape)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            "₹12,000 Pending",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }

                // MONEY BORROWED
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "MONEY BORROWED",
                        fontSize = 11.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "₹14,400",
                        color = RedIcon,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(RedIcon, shape = CircleShape)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            "₹14,400 Pending",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}