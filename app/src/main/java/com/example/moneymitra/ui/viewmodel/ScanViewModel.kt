package com.example.moneymitra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymitra.data.model.Response
import com.example.moneymitra.auth.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class ScanViewModel : ViewModel() {

    private val repo = ScanRepository()

    private val _receipt = MutableStateFlow<Response?>(null)
    val receipt: StateFlow<Response?> = _receipt

    fun scan(file: File) {

        viewModelScope.launch {

            val result = repo.scanReceipt(file)

            _receipt.value = result
        }
    }
}