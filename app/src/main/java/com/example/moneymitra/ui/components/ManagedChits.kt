package com.example.moneymitra.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moneymitra.viewmodel.ChitViewModel

@Composable
fun ManagedChits(
    navController: NavController,
    viewModel: ChitViewModel = viewModel()
) {

    val chits by viewModel.chits.collectAsState()
    val managedChits = chits.filter { it.manager }

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(managedChits) { chit ->

                ChitManagerCard(
                    chit = chit,
                    onClick = {
                        navController.navigate("chitDetail/${chit.id}") // 🔥 NAVIGATE
                    }
                )
            }
        }


    }
}