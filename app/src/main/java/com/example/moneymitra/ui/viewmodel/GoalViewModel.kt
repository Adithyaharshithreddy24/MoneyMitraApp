package com.example.moneymitra.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymitra.data.model.Goal
import com.example.moneymitra.repository.GoalRepository
import kotlinx.coroutines.launch

class GoalViewModel : ViewModel() {

    private val repository = GoalRepository()

    var goals = mutableStateListOf<Goal>()
        private set

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // ✅ LOAD GOALS
    fun loadGoals() {
        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            val result = repository.getGoals()

            result.onSuccess { list ->
                goals.clear()
                goals.addAll(list)
            }

            result.onFailure { e ->
                errorMessage = e.message
            }

            isLoading = false
        }
    }

    // ✅ ADD GOAL
    fun addGoal(title: String, target: Double, priority: Int) {
        viewModelScope.launch {

            val goal = Goal(
                title = title,
                targetAmount = target,
                priority = priority
            )

            val result = repository.addGoal(goal)

            result.onSuccess { newGoal ->
                goals.add(newGoal)
            }

            result.onFailure {
                errorMessage = it.message
            }
        }
    }

    // ✅ ADD SAVINGS
    fun addSavings(goal: Goal, amount: Double) {
        viewModelScope.launch {

            val updatedAmount = goal.savedAmount + amount

            val updatedGoal = goal.copy(
                savedAmount = updatedAmount,
                isCompleted = updatedAmount >= goal.targetAmount
            )

            val result = repository.updateGoal(updatedGoal)

            result.onSuccess {
                val index = goals.indexOfFirst { it.id == goal.id }
                if (index != -1) goals[index] = updatedGoal
            }

            result.onFailure {
                errorMessage = it.message
            }
        }
    }

    // ✅ UPDATE GOAL (EDIT)
    fun updateGoal(goal: Goal) {
        viewModelScope.launch {

            val result = repository.updateGoal(goal)

            result.onSuccess {
                val index = goals.indexOfFirst { it.id == goal.id }
                if (index != -1) goals[index] = goal
            }

            result.onFailure {
                errorMessage = it.message
            }
        }
    }

    // ✅ DELETE GOAL
    fun deleteGoal(id: String) {
        viewModelScope.launch {

            val result = repository.deleteGoal(id)

            result.onSuccess {
                goals.removeAll { it.id == id }
            }

            result.onFailure {
                errorMessage = it.message
            }
        }
    }
}