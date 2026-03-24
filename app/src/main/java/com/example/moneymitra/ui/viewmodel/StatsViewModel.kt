package com.example.moneymitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymitra.data.model.Account
import com.example.moneymitra.repository.ChitRepository
import com.example.moneymitra.repository.StatsRepository
import com.example.moneymitra.data.model.StatsData
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

// Replaced ALL with CUSTOM
enum class TimeFilter { WEEK, MONTH, YEAR, CUSTOM }

class StatsViewModel : ViewModel() {

    private val repo = StatsRepository(ChitRepository())

    private val _stats = mutableStateOf<StatsData?>(null)
    val stats: State<StatsData?> = _stats

    private val _selectedFilter = mutableStateOf(TimeFilter.MONTH)
    val selectedFilter: State<TimeFilter> = _selectedFilter

    // --- NEW: Account States ---
    private val _accounts = mutableStateOf<List<Account>>(emptyList())
    val accounts: State<List<Account>> = _accounts

    private val _selectedAccount = mutableStateOf<Account?>(null) // null means "All Accounts"
    val selectedAccount: State<Account?> = _selectedAccount

    // --- NEW: Custom Date States ---
    private val _customDateRange = mutableStateOf<Pair<Long, Long>?>(null)
    val customDateRange: State<Pair<Long, Long>?> = _customDateRange

    init {
        fetchAccounts()
        loadStats()
    }

    private fun fetchAccounts() {
        viewModelScope.launch {
            try {
                _accounts.value = repo.getAccounts()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun setFilter(filter: TimeFilter) {
        _selectedFilter.value = filter
        if (filter != TimeFilter.CUSTOM) {
            loadStats()
        }
    }

    fun setCustomDateRange(startDate: Long, endDate: Long) {
        _customDateRange.value = Pair(startDate, endDate)
        _selectedFilter.value = TimeFilter.CUSTOM
        loadStats()
    }

    fun setSelectedAccount(account: Account?) {
        _selectedAccount.value = account
        loadStats()
    }

    fun refreshStats() {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _stats.value = repo.getStats(
                filter = _selectedFilter.value,
                customStart = _customDateRange.value?.first,
                customEnd = _customDateRange.value?.second,
                accountId = _selectedAccount.value?.id
            )
        }
    }
}