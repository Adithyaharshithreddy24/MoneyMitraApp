package com.example.moneymitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moneymitra.repository.AccountRepository
import com.example.moneymitra.data.model.Account

class AddAccountViewModel : ViewModel() {

    private val repository = AccountRepository()

    fun saveAccount(
        account: Account,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        repository.addAccount(
            account = account,
            onSuccess = onSuccess,
            onError = onError
        )
    }
}
