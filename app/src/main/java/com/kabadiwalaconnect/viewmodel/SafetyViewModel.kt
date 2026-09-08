package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabadiwalaconnect.data.model.EmergencyContact
import com.kabadiwalaconnect.data.model.SafetyTip
import com.kabadiwalaconnect.data.model.SafetyCategory
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SafetyViewModel(private val repository: KabadiwalaRepository) : ViewModel() {

    private val _safetyTips = MutableStateFlow<List<SafetyTip>>(emptyList())
    val safetyTips: StateFlow<List<SafetyTip>> = _safetyTips

    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts

    private val _selectedCategory = MutableStateFlow<SafetyCategory?>(null)
    val selectedCategory: StateFlow<SafetyCategory?> = _selectedCategory

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getSafetyTips().onSuccess { tips ->
                _safetyTips.value = tips
            }.onError { message ->
                _error.value = message
            }
            repository.getEmergencyContacts().onSuccess { contacts ->
                _emergencyContacts.value = contacts
                _isLoading.value = false
            }.onError { message ->
                _error.value = message
                _isLoading.value = false
            }
        }
    }

    fun filterByCategory(category: SafetyCategory?) {
        _selectedCategory.value = category
    }

    val filteredTips: StateFlow<List<SafetyTip>> = combine(_safetyTips, _selectedCategory) { tips, category ->
        if (category == null) tips else tips.filter { it.category == category }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}