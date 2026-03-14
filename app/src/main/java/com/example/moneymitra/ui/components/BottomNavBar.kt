package com.example.moneymitra.ui.components

import android.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavBar(
    selectedIndex: Int=0,
    onHomeClick: () -> Unit,
    onGridClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {

        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 8.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0B1A3A),
                selectedTextColor = Color(0xFF0B1A3A),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = onGridClick,
            icon = {
                Icon(Icons.Default.GridView, contentDescription = "Categories")
            },
            label = { Text("Categories", fontSize = 8.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0B1A3A),
                selectedTextColor = Color(0xFF0B1A3A),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = onNotificationClick,
            icon = {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            },
            label = { Text("Notifications", fontSize = 8.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0B1A3A),
                selectedTextColor = Color(0xFF0B1A3A),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = onProfileClick,
            icon = {
                Icon(Icons.Default.Person, contentDescription = "Profile")
            },
            label = { Text("Profile", fontSize = 8.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0B1A3A),
                selectedTextColor = Color(0xFF0B1A3A),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color.Transparent
            )
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
        containerColor =Color(0xFF0B1A3A) ,
        shape = RoundedCornerShape(50.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White
        )
    }
}