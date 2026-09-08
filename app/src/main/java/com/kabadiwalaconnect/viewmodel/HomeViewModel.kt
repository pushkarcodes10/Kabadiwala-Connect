package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabadiwalaconnect.data.model.EarningsSummary
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.MarketPrice
import com.kabadiwalaconnect.data.model.Recycler
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: KabadiwalaRepository) : ViewModel() {

    private val _earningsSummary = MutableStateFlow<EarningsSummary?>(null)
    val earningsSummary: StateFlow<EarningsSummary?> = _earningsSummary

    private val _recentTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val recentTransactions: StateFlow<List<Transaction>> = _recentTransactions

    private val _topMaterials = MutableStateFlow<List<Material>>(emptyList())
    val topMaterials: StateFlow<List<Material>> = _topMaterials

    private val _marketPrices = MutableStateFlow<List<MarketPrice>>(emptyList())
    val marketPrices: StateFlow<List<MarketPrice>> = _marketPrices

    private val _nearbyRecyclers = MutableStateFlow<List<Recycler>>(emptyList())
    val nearbyRecyclers: StateFlow<List<Recycler>> = _nearbyRecyclers

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getEarningsSummary().onSuccess { summary ->
                _earningsSummary.value = summary
            }.onError { message ->
                _error.value = message
            }

            repository.getTransactions().onSuccess { transactions ->
                _recentTransactions.value = transactions.take(5)
            }.onError { message ->
                _error.value = message
            }

            repository.getMaterials().onSuccess { materials ->
                _topMaterials.value = materials.take(4)
            }.onError { message ->
                _error.value = message
            }

            // Get first 4 market prices
            _marketPrices.value = repository.marketPrices.value.take(4)

            // Get first 3 nearby recyclers
            _nearbyRecyclers.value = repository.nearbyRecyclers.value.take(3)

            _isLoading.value = false
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshMarketPrices()
            loadDashboardData()
        }
    }
}