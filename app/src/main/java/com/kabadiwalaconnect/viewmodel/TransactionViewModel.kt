package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.model.TransactionStatus
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: KabadiwalaRepository) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _pendingTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val pendingTransactions: StateFlow<List<Transaction>> = _pendingTransactions

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _verifyingOtp = MutableStateFlow<String?>(null)
    val verifyingOtp: StateFlow<String?> = _verifyingOtp

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getTransactions().onSuccess { transactions ->
                _transactions.value = transactions
                _pendingTransactions.value = transactions.filter {
                    it.status == TransactionStatus.PENDING || it.status == TransactionStatus.CONFIRMED
                }
                _isLoading.value = false
            }.onError { message ->
                _error.value = message
                _isLoading.value = false
            }
        }
    }

    fun confirmHandover(transaction: Transaction, otp: String) {
        if (otp.isBlank()) {
            _error.value = "Please enter OTP"
            return
        }
        viewModelScope.launch {
            _verifyingOtp.value = transaction.id
            _error.value = null
            repository.confirmHandover(transaction.id, otp).onSuccess { updated ->
                _transactions.value = _transactions.value.map { if (it.id == transaction.id) updated else it }
                _pendingTransactions.value = _pendingTransactions.value.filter { it.id != transaction.id }
                _verifyingOtp.value = null
            }.onError { message ->
                _error.value = message
                _verifyingOtp.value = null
            }
        }
    }

    fun getTransaction(id: String): Transaction? = _transactions.value.find { it.id == id }
}