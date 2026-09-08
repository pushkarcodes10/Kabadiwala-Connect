package com.kabadiwalaconnect.data.repository

import com.kabadiwalaconnect.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface KabadiwalaRepository {
    // Materials
    val materials: StateFlow<List<Material>>
    suspend fun getMaterials(): Result<List<Material>>
    suspend fun getMaterial(id: String): Result<Material?>

    // Market Prices
    val marketPrices: StateFlow<List<MarketPrice>>
    suspend fun refreshMarketPrices(): Result<List<MarketPrice>>
    suspend fun getMarketPrice(materialId: String): Result<MarketPrice?>

    // Recyclers
    val nearbyRecyclers: StateFlow<List<Recycler>>
    suspend fun searchRecyclers(query: String, lat: Double, lng: Double, radiusKm: Double): Result<RecyclerSearchResult>
    suspend fun getRecycler(id: String): Result<Recycler?>

    // Transactions
    val transactions: StateFlow<List<Transaction>>
    suspend fun getTransactions(): Result<List<Transaction>>
    suspend fun createTransaction(transaction: Transaction): Result<Transaction>
    suspend fun updateTransaction(transaction: Transaction): Result<Transaction>
    suspend fun confirmHandover(transactionId: String, otp: String): Result<Transaction>

    // Earnings
    val earningsSummary: StateFlow<EarningsSummary?>
    suspend fun getEarningsSummary(): Result<EarningsSummary>
    val khataEntries: StateFlow<List<KhataEntry>>
    suspend fun getKhataEntries(): Result<List<KhataEntry>>
    suspend fun getMonthlyEarnings(): Result<List<MonthlyEarnings>>

    // Safety
    val safetyTips: StateFlow<List<SafetyTip>>
    suspend fun getSafetyTips(): Result<List<SafetyTip>>
    val emergencyContacts: StateFlow<List<EmergencyContact>>
    suspend fun getEmergencyContacts(): Result<List<EmergencyContact>>

    // User
    val currentUser: StateFlow<User?>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun updateUser(user: User): Result<User>
}

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>
    object Loading : Result<Nothing>
}