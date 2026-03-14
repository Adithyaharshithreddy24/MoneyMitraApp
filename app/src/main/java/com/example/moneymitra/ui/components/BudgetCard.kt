package com.example.moneymitra.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCard(
    month: String,
    budget: Double?,
    expense: Double,
    progress: Float,
    onSetBudget: (Double) -> Unit,
    onEditBudget: (Double) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    var input by remember(budget) {
        mutableStateOf(budget?.toInt()?.toString() ?: "")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-60).dp)
            .padding(horizontal = 20.dp)
            .clickable { expanded = !expanded }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(25.dp)
            )
            .animateContentSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White
                )
                .padding(20.dp)
        ) {

            Column {

                /* -------------------------
                   BUDGET EXISTS
                -------------------------- */

                if (budget != null) {

                    val remaining = budget - expense
                    val exceeded = expense - budget

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Budget for $month",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "₹${budget.toInt()}",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(
                                    brush = if (expense > budget)
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFF5252), Color(0xFFFF8A80))
                                        )
                                    else
                                        Brush.horizontalGradient(
                                            listOf(Color.Black, Color(0xFF1A237E))
                                        ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    when {

                        expense < budget -> {

                            Text(
                                text = "Remaining: ₹${remaining.toInt()}",
                                color = Color.Black.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                        }

                        expense == budget -> {

                            Text(
                                text = "Budget fully used",
                                color = Color.Red,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        expense > budget -> {

                            Text(
                                text = "⚠ Budget exceeded by ₹${exceeded.toInt()}",
                                color = Color(0xFFFF5252),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (expanded) {

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            label = { Text("Edit Budget") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            TextButton(
                                onClick = { expanded = false }
                            ) {
                                Text("Cancel", color = Color.White)
                            }

                            Button(
                                onClick = {

                                    val value =
                                        input.toDoubleOrNull()

                                    if (value != null && value > 0) {

                                        onEditBudget(value)
                                        expanded = false
                                    }
                                }
                            ) {
                                Text("Update")
                            }
                        }
                    }
                }

                /* -------------------------
                   BUDGET NOT SET
                -------------------------- */

                else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Set Budget for $month",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (expanded) {

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            label = { Text("Enter Budget") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            TextButton(
                                onClick = { expanded = false }
                            ) {
                                Text("Cancel", color = colors.primary )
                            }

                            Button(
                                onClick = {

                                    val value =
                                        input.toDoubleOrNull()

                                    if (value != null && value > 0) {

                                        onSetBudget(value)
                                        expanded = false
                                    }
                                }
                            ) {
                                Text("Set")
                            }
                        }
                    }
                }
            }
        }
    }
}