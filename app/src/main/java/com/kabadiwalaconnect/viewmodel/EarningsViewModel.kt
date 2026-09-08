package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabadiwalaconnect.data.model.EarningsSummary
import com.kabadiwalaconnect.data.model.KhataEntry
import com.kabadiwalaconnect.data.model.MonthlyEarnings
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EarningsViewModel(private val repository: KabadiwalaRepository) : ViewModel() {

    private val _earningsSummary = MutableStateFlow<EarningsSummary?>(null)
    val earningsSummary: StateFlow<EarningsSummary?> = _earningsSummary

    private val _khataEntries = MutableStateFlow<List<KhataEntry>>(emptyList())
    val khataEntries: StateFlow<List<KhataEntry>> = _khataEntries

    private val _monthlyEarnings = MutableStateFlow<List<MonthlyEarnings>>(emptyList())
    val monthlyEarnings: StateFlow<List<MonthlyEarnings>> = _monthlyEarnings

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedTab = MutableStateFlow<EarningsTab>(EarningsTab.SUMMARY)
    val selectedTab: StateFlow<EarningsTab> = _selectedTab

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getEarningsSummary().onSuccess { summary ->
                _earningsSummary.value = summary
            }.onError { message ->
                _error.value = message
            }
            repository.getKhataEntries().onSuccess { entries ->
                _khataEntries.value = entries
            }.onError { message ->
                _error.value = message
            }
            repository.getMonthlyEarnings().onSuccess { monthly ->
                _monthlyEarnings.value = monthly
                _isLoading.value = false
            }.onError { message ->
                _error.value = message
                _isLoading.value = false
            }
        }
    }

    fun setSelectedTab(tab: EarningsTab) {
        _selectedTab.value = tab
    }

    fun refresh() {
        loadData()
    }
}

enum class EarningsTab {
    SUMMARY, KHATA, MONTHLY
}