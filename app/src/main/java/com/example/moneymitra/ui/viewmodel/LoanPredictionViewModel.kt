package com.example.moneymitra.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymitra.data.api.RetrofitClient
import com.example.moneymitra.data.model.LoanRequest
import com.example.moneymitra.data.model.LoanResponse
import com.example.moneymitra.data.model.LoansResponse
import com.example.moneymitra.repository.LoanRepository
import com.example.moneymitra.data.model.GoldLoanResponse
import com.example.moneymitra.data.model.VehicleLoanResponse
import kotlinx.coroutines.launch

class LoanPredictionViewModel : ViewModel() {

    private val repository = LoanRepository()

    var result by mutableStateOf<LoanResponse?>(null)
        private set
    var results by mutableStateOf<LoansResponse?>(null)
    var goldResult by mutableStateOf<GoldLoanResponse?>(null)
    var vehicleResult by mutableStateOf<VehicleLoanResponse?>(null)
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

    fun calculatehomeLoan(property: Double, income: Double, cibil: Int) {
        viewModelScope.launch {
            try {
                isLoading = true
                results = RetrofitClient.apiService.calculateHomeLoan(property, income, cibil)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }


    fun calculateGoldLoan(weight: Double) {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null

                goldResult = RetrofitClient.apiService.calculateGoldLoan(weight)

            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun calculateVehicleLoan(price: Double, income: Double, cibil: Int, type: String) {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null

                vehicleResult = RetrofitClient.apiService
                    .calculateVehicleLoan(price, income, cibil, type)

            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}