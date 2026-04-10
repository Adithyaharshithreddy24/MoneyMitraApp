package com.example.moneymitra.repository

import com.example.moneymitra.data.model.Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GoalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userGoals(uid: String) =
        db.collection("users")
            .document(uid)
            .collection("goals")

    // ✅ ADD GOAL
    suspend fun addGoal(goal: Goal): Result<Goal> {
        return try {

            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            val doc = userGoals(uid).document()

            val newGoal = goal.copy(id = doc.id)

            doc.set(newGoal).await()

            Result.success(newGoal)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ GET GOALS
    suspend fun getGoals(): Result<List<Goal>> {
        return try {

            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            val snapshot = userGoals(uid).get().await()

            Result.success(snapshot.toObjects(Goal::class.java))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ UPDATE GOAL
    suspend fun updateGoal(goal: Goal): Result<Unit> {
        return try {

            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            userGoals(uid)
                .document(goal.id)
                .set(goal)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ DELETE GOAL
    suspend fun deleteGoal(id: String): Result<Unit> {
        return try {

            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            userGoals(uid)
                .document(id)
                .delete()
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}