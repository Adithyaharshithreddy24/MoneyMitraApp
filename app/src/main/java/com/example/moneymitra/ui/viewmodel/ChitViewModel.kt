package com.example.moneymitra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymitra.auth.ChitRepository
import com.example.moneymitra.data.model.Chit
import com.example.moneymitra.data.model.Member
import com.example.moneymitra.ui.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChitViewModel : ViewModel() {

    private val repo = ChitRepository()

    private val _chits = MutableStateFlow<List<Chit>>(emptyList())
    val chits: StateFlow<List<Chit>> = _chits

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadChits() {
        viewModelScope.launch {
            _loading.value = true
            repo.getChits().onSuccess {
                _chits.value = it
            }
            _loading.value = false
        }
    }

    fun getMembers(
        chitId: String,
        onResult: (List<Member>) -> Unit
    ) {
        viewModelScope.launch {
            val result = repo.getMembers(chitId)
            onResult(result)
        }
    }

    fun addManagerChit(
        chit: Chit,
        members: List<Member>,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.addManagerChit(chit, members).onSuccess {
                loadChits()
                onDone()
            }
        }
    }

    fun addMemberChit(
        chit: Chit,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.addMemberChit(chit).onSuccess {
                loadChits()
                onDone()
            }
        }
    }

    fun updateMember(
        chitId: String,
        member: Member,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.updateMember(chitId, member.id, member)
            onDone()
        }
    }

    // 🔥 EXISTING (UNCHANGED)
    fun settleAmount(
        chitId: String,
        member: Member,
        amount: Double,
        transactionVM: AddTransactionViewModel,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {

            val newDue = (member.due - amount).coerceAtLeast(0.0)

            repo.updateDue(chitId, member.id, newDue)

            transactionVM.addTransactionSuspend(
                name = member.name,
                amount = amount,
                type = "INCOME",
                category = "Chits",
                note = "Chit payment from ${member.name}"
            )

            onDone()
        }
    }

    fun markPayout(
        chitId: String,
        memberId: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.markPayout(chitId, memberId)
            onDone()
        }
    }

    fun updateChit(
        chit: Chit,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.updateChit(chit)
            loadChits()
            onDone()
        }
    }

    // 🔥 NEW: DELETE CHIT
    fun deleteChit(
        chitId: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.deleteChit(chitId)
            loadChits()
            onDone()
        }
    }

    // 🔥 NEW: PAY FOR MEMBER CHIT (IMPORTANT)
    fun payChit(
        chit: Chit,
        amount: Double,
        transactionVM: AddTransactionViewModel,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {

            val newDue = (chit.due - amount)

            val updated = chit.copy(due = newDue.toInt())

            // 1️⃣ Update chit due
            repo.updateChit(updated)

            // 2️⃣ Add EXPENSE transaction (auto first account)
            transactionVM.addTransactionSuspend(
                name = chit.name,
                amount = amount,
                type = "EXPENSE",
                category = "Chits",
                note = "Chit payment"
            )

            loadChits()
            onDone()
        }
    }
}