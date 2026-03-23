package com.example.moneymitra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneymitra.data.model.Chit
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import com.example.moneymitra.ui.screens.toCamelCase


@Composable
fun ChitManagerCard(
    chit: Chit,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    // 1. Format the startDate (Long) to match the image style "22 Mar 2026"
    val formattedDate = remember(chit.startDate) {
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        formatter.format(Date(chit.startDate))
    }

    // 2. Calculate completed months for the progress text
    val completedMonths = remember(chit.startDate, chit.months) {
        val currentTime = System.currentTimeMillis()
        val elapsedMillis = currentTime - chit.startDate
        val monthMillis = 30L * 24L * 60L * 60L * 1000L // Approx 30 days in ms

        if (elapsedMillis > 0) {
            (elapsedMillis / monthMillis).toInt().coerceIn(0, chit.months)
        } else {
            0
        }
    }

    // 3. Calculate dynamic tenure progress (from 0.0f to 1.0f)
    val progress = remember(completedMonths, chit.months) {
        if (chit.months > 0) {
            (completedMonths.toFloat() / chit.months.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(colors.surface)
    ) {
        // 🔹 HEADER (Transaction Style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box (Left side rounded square)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEDE6F3), RoundedCornerShape(14.dp)), // Light Blue background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = Color(0xFF311B92), // Darker Blue
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Middle Column: Role (small), Name (Bold), Date (small)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Role: Manager",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = chit.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Started: $formattedDate",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Right Column: Total Amount (Bold)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${chit.totalAmount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "TOTAL POOL",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // 🔹 PROGRESS SECTION (Always visible, no expansion needed)
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tenure Progress",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant
                )
                Text(
                    text = "$completedMonths / ${chit.months} Months",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                strokeCap = StrokeCap.Round,
                color = Color(0xFF311B92), // Matches the blue icon color
                trackColor = Color(0xFFEDE6F3),
            )
        }

        Spacer(Modifier.height(8.dp))

        // Faint divider at the bottom of the list item
        HorizontalDivider(
            color = Color(0xFFF3F4F6),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun ChitMemberCard(
    chit: Chit,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onShowDetails: () -> Unit,
    onEdit: () -> Unit,
    onPay: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    // 1. Format the startDate (Long) to match the image style "22 Mar 2026"
    val formattedDate = remember(chit.startDate) {
        val formatter = SimpleDateFormat("dd MMM ", Locale.getDefault())
        formatter.format(Date(chit.startDate))
    }

    // 2. Calculate completed months for the progress text
    val completedMonths = remember(chit.startDate, chit.months) {
        val currentTime = System.currentTimeMillis()
        val elapsedMillis = currentTime - chit.startDate
        val monthMillis = 30L * 24L * 60L * 60L * 1000L // Approx 30 days in ms

        if (elapsedMillis > 0) {
            (elapsedMillis / monthMillis).toInt().coerceIn(0, chit.months)
        } else {
            0
        }
    }

    // 3. Calculate dynamic tenure progress (from 0.0f to 1.0f)
    val progress = remember(completedMonths, chit.months) {
        if (chit.months > 0) {
            (completedMonths.toFloat() / chit.months.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Call the parent's onClick instead of changing local state
            .background(colors.surface)
    ) {
        // 🔹 ALWAYS VISIBLE HEADER (Transaction Style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box (Left side rounded square)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEDE6F3), RoundedCornerShape(14.dp)), // Light purple-ish background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = Color(0xFF311B92),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Middle Column: Manager (small), Name (Bold), Date (small)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toCamelCase(chit.name),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )
                Text(
                    text = "Manager: ${toCamelCase(chit.managerName)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Chit Amount : ₹${chit.totalAmount}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
            }

            // Right Column: Due Amount (Bold) & Total Amount (small uppercase)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${chit.due}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Due: $formattedDate",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // 🔹 EXPANDABLE CONTENT (Buttons & Progress)
        AnimatedVisibility(
            visible = isExpanded, // Uses the state passed from the parent
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

                // Progress Bar section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tenure Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                    Text(
                        text = "$completedMonths / ${chit.months} Months",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    strokeCap = StrokeCap.Round,
                    color = colors.primary,
                    trackColor = colors.primaryContainer,
                )

                Spacer(Modifier.height(24.dp))

                // Action Buttons (Row 1) - Secondary Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onShowDetails,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Details")
                    }
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Edit")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Action Buttons (Row 2) - Primary Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Delete Button
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.error
                        ),
                        border = BorderStroke(1.dp, colors.error)
                    ) {
                        Text("Delete")
                    }

                    // Prominent Pay Button
                    Button(
                        onClick = onPay,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        Text("Pay Now", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(0.dp))
            }
        }

        // Faint divider at the bottom of the list item
        HorizontalDivider(
            color = colors.outlineVariant,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
    }
}