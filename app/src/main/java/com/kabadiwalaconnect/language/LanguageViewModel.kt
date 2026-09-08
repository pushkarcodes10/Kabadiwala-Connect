package com.kabadiwalaconnect.language

import androidx.lifecycle.ViewModel
import com.kabadiwalaconnect.KabadiwalaApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.staticCompositionLocalOf

val LocalLanguageViewModel = staticCompositionLocalOf<LanguageViewModel?> { null }
val LocalCurrentLanguage = staticCompositionLocalOf<Language> {
    SUPPORTED_LANGUAGES.first()
}

class LanguageViewModel : ViewModel() {
    private val languageManager = LanguageManager.getInstance(KabadiwalaApplication.getInstance())
    
    private val _currentLanguage = MutableStateFlow<Language>(languageManager.getCurrentLanguage())
    val currentLanguage: StateFlow<Language> = _currentLanguage
    
    private val _supportedLanguages = MutableStateFlow(SUPPORTED_LANGUAGES)
    val supportedLanguages: StateFlow<List<Language>> = _supportedLanguages
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    val filteredLanguages: StateFlow<List<Language>> = combine(_supportedLanguages, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { lang ->
                lang.name.contains(query, ignoreCase = true) ||
                lang.nativeName.contains(query, ignoreCase = true) ||
                lang.region.contains(query, ignoreCase = true) ||
                lang.code.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SUPPORTED_LANGUAGES)
    
    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun changeLanguage(language: Language) {
        languageManager.setLanguage(language)
        _currentLanguage.value = language
        _showLanguageDialog.value = false
    }
    
    fun toggleLanguageDialog() {
        _showLanguageDialog.value = !_showLanguageDialog.value
        if (_showLanguageDialog.value) {
            _searchQuery.value = ""
        }
    }
    
    fun openLanguageDialog() {
        _searchQuery.value = ""
        _showLanguageDialog.value = true
    }
    
    fun dismissLanguageDialog() {
        _showLanguageDialog.value = false
    }
}