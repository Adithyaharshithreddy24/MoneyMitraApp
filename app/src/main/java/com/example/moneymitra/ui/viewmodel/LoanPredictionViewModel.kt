package com.example.moneymitra.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymitra.data.model.LoanRequest
import com.example.moneymitra.data.model.LoanResponse
import com.example.moneymitra.repository.LoanRepository
import kotlinx.coroutines.launch

class LoanPredictionViewModel : ViewModel() {

    private val repository = LoanRepository()

    var result by mutableStateOf<LoanResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    fun personalloanprediction(request: LoanRequest) {
        viewModelScope.launch {

            isLoading = true
            error = null

            val response = repository.personalloanprediction(request) // ✅ FIX

            response.onSuccess {
                result = it
            }

            response.onFailure {
                error = it.message
            }

            isLoading = false
        }
    }
}