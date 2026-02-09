package com.example.moneymitra.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onGridClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTransactionClick: () -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color(0xFF11123C)
    ) {

        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    null,
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 8.sp,
                    color = Color.White
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onGridClick,
            icon = {
                Icon(
                    Icons.Default.GridView,
                    null,
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Cetogeries",
                    fontSize = 8.sp,
                    color = Color.White
                )
            }
        )

        Spacer(modifier = Modifier.weight(1f)) // 🔥 space for + FAB

        NavigationBarItem(
            selected = false,
            onClick = onNotificationClick,
            icon = {
                Icon(
                    Icons.Default.Notifications,
                    null,
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Notifications",
                    fontSize = 8.sp,
                    color = Color.White
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onTransactionClick,
            icon = {
                Icon(
                    Icons.Default.SyncAlt,
                    null,
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = "Transactions",
                    fontSize = 8.sp,
                    color = Color.White
                )
            }
        )
    }
}

@Composable
fun CenterAddFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier ,
        containerColor = Color.White,
        shape = RoundedCornerShape(50.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}