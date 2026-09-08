package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabadiwalaconnect.data.model.MarketPrice
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketPricesViewModel(private val repository: KabadiwalaRepository) : ViewModel() {

    private val _prices = MutableStateFlow<List<MarketPrice>>(emptyList())
    val prices: StateFlow<List<MarketPrice>> = _prices

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadPrices()
    }

    fun loadPrices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getMaterials().onSuccess { materials ->
                // Get prices for all materials
                repository.marketPrices.value.also { prices ->
                    if (prices.isEmpty()) {
                        refreshPrices()
                    } else {
                        _prices.value = prices
                        _isLoading.value = false
                    }
                }
            }.onError { message ->
                _error.value = message
                _isLoading.value = false
            }
        }
    }

    fun refreshPrices() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            repository.refreshMarketPrices().onSuccess { prices ->
                _prices.value = prices
                _isRefreshing.value = false
            }.onError { message ->
                _error.value = message
                _isRefreshing.value = false
            }
        }
    }
}