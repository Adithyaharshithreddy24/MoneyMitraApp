package com.example.moneymitra.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moneymitra.ui.components.GoalCard
import com.example.moneymitra.viewmodel.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalViewModel = viewModel()
) {

    val goals = viewModel.goals

    LaunchedEffect(Unit) {
        viewModel.loadGoals()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add_goal") {
                        launchSingleTop = true
                    }
                }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No Goals Yet 🚀")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(goals) { goal ->
                    GoalCard(
                        goal = goal,
                        onAddMoney = {
                            viewModel.addSavings(goal, it)
                        },
                        onDelete = {
                            viewModel.deleteGoal(goal.id)
                        },
                        onUpdate = {
                            viewModel.updateGoal(it)
                        }
                    )
                }
            }
        }
    }
}