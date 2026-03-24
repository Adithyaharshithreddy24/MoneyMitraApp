package com.example.moneymitra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymitra.repository.LoanRepository
import com.example.moneymitra.data.model.Loan
import com.example.moneymitra.utils.calculateEMI
import com.example.moneymitra.utils.calculateNextDueDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanViewModel : ViewModel() {

    private val repo = LoanRepository()
    private val _loans = MutableStateFlow<List<Loan>>(emptyList())
    val loans: StateFlow<List<Loan>> = _loans
    fun addLoan(
        name: String,
        principal: String,
        interest: String,
        tenure: String,
        loanType: String,
        frequency: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val p = principal.toDouble()
                val r = interest.toDouble()
                val t = tenure.toInt()

                val emi = calculateEMI(p, r, t)

                val loan = Loan(
                    name = name,
                    principal = p,
                    interestRate = r,
                    tenureYears = t,
                    emi = emi,
                    loanType = loanType,
                    repaymentFrequency = frequency,
                    nextDueDate = calculateNextDueDate()
                )

                repo.addLoan(loan)
                onSuccess()

            } catch (e: Exception) {
                onError(e.message ?: "Error")
            }
        }
    }

    fun loadLoans() {
        repo.getLoans {
            _loans.value = it
        }
    }
    fun updateLoan(
        loan: Loan,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.updateLoan(loan)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error")
            }
        }
    }
    fun deleteLoan(
        loanId: String,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repo.deleteLoan(loanId)
            } catch (e: Exception) {
                onError(e.message ?: "Error")
            }
        }
    }
}