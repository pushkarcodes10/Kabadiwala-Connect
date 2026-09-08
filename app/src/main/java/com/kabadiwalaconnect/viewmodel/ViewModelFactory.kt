package com.kabadiwalaconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import com.kabadiwalaconnect.KabadiwalaApplication
import com.kabadiwalaconnect.language.LanguageViewModel

class ViewModelFactory(private val repository: KabadiwalaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(repository) as T
            MaterialEntryViewModel::class.java -> MaterialEntryViewModel(repository) as T
            MarketPricesViewModel::class.java -> MarketPricesViewModel(repository) as T
            RecyclerDiscoveryViewModel::class.java -> RecyclerDiscoveryViewModel(repository) as T
            TransactionViewModel::class.java -> TransactionViewModel(repository) as T
            EarningsViewModel::class.java -> EarningsViewModel(repository) as T
            SafetyViewModel::class.java -> SafetyViewModel(repository) as T
            LanguageViewModel::class.java -> LanguageViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}

fun getViewModelFactory(): ViewModelFactory {
    val app = KabadiwalaApplication.getInstance()
    return ViewModelFactory(app.repository)
}

fun viewModelFactory(): ViewModelProvider.Factory = getViewModelFactory()