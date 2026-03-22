package com.example.moneymitra.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.viewmodel.ChitViewModel
import com.example.moneymitra.ui.components.*

@Composable
fun ChitFundScreen(
    navController: NavController,
    viewModel: ChitViewModel = viewModel()
) {

    var tab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadChits()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (tab == 0) navController.navigate("add_member_chit")
                else navController.navigate("add_chit")
            }) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

        Column(Modifier.padding(padding).padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton({ navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
                Text("Chits", style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                    .padding(4.dp)
            ) {

                TabItem("My Chits", tab == 0, { tab = 0 }, Modifier.weight(1f))
                TabItem("Managed", tab == 1, { tab = 1 }, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            if (tab == 0) {
                MemberChits(navController, viewModel)
            } else {
                ManagedChits(navController, viewModel)
            }
        }
    }
}

@Composable
fun TabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
    }
}