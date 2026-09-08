package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaterialEntryViewModel(private val repository: KabadiwalaRepository) : ViewModel() {

    private val _materials = MutableStateFlow<List<Material>>(emptyList())
    val materials: StateFlow<List<Material>> = _materials

    private val _selectedMaterial = MutableStateFlow<Material?>(null)
    val selectedMaterial: StateFlow<Material?> = _selectedMaterial

    private val _weight = MutableStateFlow<String>("")
    val weight: StateFlow<String> = _weight

    private val _estimatedPrice = MutableStateFlow<Double>(0.0)
    val estimatedPrice: StateFlow<Double> = _estimatedPrice

    private val _minEstimatedPrice = MutableStateFlow<Double>(0.0)
    val minEstimatedPrice: StateFlow<Double> = _minEstimatedPrice

    private val _maxEstimatedPrice = MutableStateFlow<Double>(0.0)
    val maxEstimatedPrice: StateFlow<Double> = _maxEstimatedPrice

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadMaterials()
    }

    fun loadMaterials() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getMaterials().onSuccess { materials ->
                _materials.value = materials
                if (_selectedMaterial.value == null && materials.isNotEmpty()) {
                    _selectedMaterial.value = materials[0]
                    updateEstimatedPrice()
                }
                _isLoading.value = false
            }.onError { message ->
                _error.value = message
                _isLoading.value = false
            }
        }
    }

    fun selectMaterial(material: Material) {
        _selectedMaterial.value = material
        updateEstimatedPrice()
    }

    fun updateWeight(weightStr: String) {
        _weight.value = weightStr
        updateEstimatedPrice()
    }

    private fun updateEstimatedPrice() {
        val material = _selectedMaterial.value
        val weightStr = _weight.value
        if (material != null && weightStr.isNotBlank()) {
            val weightKg = weightStr.toDoubleOrNull() ?: 0.0
            _estimatedPrice.value = weightKg * material.basePricePerKg
            _minEstimatedPrice.value = weightKg * material.minPricePerKg
            _maxEstimatedPrice.value = weightKg * material.maxPricePerKg
        } else {
            _estimatedPrice.value = 0.0
            _minEstimatedPrice.value = 0.0
            _maxEstimatedPrice.value = 0.0
        }
    }

    fun submitEntry(onSuccess: (Double) -> Unit) {
        val material = _selectedMaterial.value
        val weightStr = _weight.value
        if (material == null || weightStr.isBlank()) {
            _error.value = "Please select material and enter weight"
            return
        }
        val weightKg = weightStr.toDoubleOrNull() ?: return
        if (weightKg <= 0) {
            _error.value = "Weight must be greater than 0"
            return
        }
        onSuccess(_estimatedPrice.value)
    }
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (String) -> Unit): Result<T> {
    if (this is Result.Error) action(message ?: exception.message ?: "Unknown error")
    return this
}