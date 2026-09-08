package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabadiwalaconnect.data.model.Recycler
import com.kabadiwalaconnect.data.model.RecyclerSearchResult
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecyclerDiscoveryViewModel(private val repository: KabadiwalaRepository) : ViewModel() {

    private val _recyclers = MutableStateFlow<List<Recycler>>(emptyList())
    val recyclers: StateFlow<List<Recycler>> = _recyclers

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isSearching = MutableStateFlow<Boolean>(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow<String?>(null)
    val selectedFilter: StateFlow<String?> = _selectedFilter

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Default location (Bangalore)
    private val defaultLat = 12.9716
    private val defaultLng = 77.5946
    private val defaultRadius = 10.0

    init {
        searchRecyclers()
    }

    fun searchRecyclers() {
        viewModelScope.launch {
            _isSearching.value = true
            _error.value = null
            repository.searchRecyclers(_searchQuery.value, defaultLat, defaultLng, defaultRadius)
                .onSuccess { result: RecyclerSearchResult ->
                    _recyclers.value = result.recyclers
                    _isSearching.value = false
                }.onError { message ->
                    _error.value = message
                    _isSearching.value = false
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchRecyclers()
    }

    fun filterByMaterial(materialId: String?) {
        _selectedFilter.value = materialId
        searchRecyclers()
    }

    fun refresh() {
        searchRecyclers()
    }
}