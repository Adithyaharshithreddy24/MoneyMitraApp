package com.example.moneymitra.repository

import com.example.moneymitra.data.api.RetrofitClient
import com.example.moneymitra.data.model.Loan
import com.example.moneymitra.data.model.LoanRequest
import com.example.moneymitra.data.model.LoanResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LoanRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userId(): String {
        return auth.currentUser?.uid ?: throw Exception("User not logged in")
    }

    suspend fun addLoan(loan: Loan) {
        val doc = db.collection("users")
            .document(userId())
            .collection("loans")
            .document()

        val loanWithId = loan.copy(id = doc.id)

        doc.set(loanWithId).await()
    }
    fun getLoans(onResult: (List<Loan>) -> Unit) {

        val uid = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(uid)
            .collection("loans")
            .addSnapshotListener { snapshot, _ ->

                val list = snapshot?.documents?.mapNotNull {
                    it.toObject(Loan::class.java)
                } ?: emptyList()

                onResult(list)
            }
    }
    suspend fun updateLoan(loan: Loan) {

        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")

        db.collection("users")
            .document(uid)
            .collection("loans")
            .document(loan.id)
            .set(loan)
            .await()
    }
    suspend fun deleteLoan(loanId: String) {

        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")

        db.collection("users")
            .document(uid)
            .collection("loans")
            .document(loanId)
            .delete()
            .await()
    }
    suspend fun personalloanprediction(request: LoanRequest): Result<LoanResponse> {
        return try {
            val response = RetrofitClient.apiService.predictPersonalLoan(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}