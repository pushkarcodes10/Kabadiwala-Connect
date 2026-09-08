package com.kabadiwalaconnect.data.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

@Serializable
data class EarningsSummary(
    val totalEarnings: Double,
    val thisMonthEarnings: Double,
    val lastMonthEarnings: Double,
    val totalTransactions: Int,
    val thisMonthTransactions: Int,
    val averagePerTransaction: Double,
    val topMaterial: String,
    val totalWeightKg: Double
) {
    val formattedTotalEarnings: String
        get() = "₹%.2f".format(totalEarnings)

    val formattedThisMonthEarnings: String
        get() = "₹%.2f".format(thisMonthEarnings)

    val formattedAverage: String
        get() = "₹%.2f".format(averagePerTransaction)

    val formattedTotalWeight: String
        get() = "%.2f kg".format(totalWeightKg)
}

@Serializable
data class KhataEntry(
    val id: String,
    @Serializable(with = InstantSerializer::class)
    val date: Instant,
    val recyclerName: String,
    val materialName: String,
    val weightKg: Double,
    val ratePerKg: Double,
    val amount: Double,
    val paymentStatus: PaymentStatus,
    @Serializable(with = InstantSerializer::class)
    val paymentDate: Instant? = null,
    val notes: String = ""
) {
    val formattedDate: String
        get() = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(date.toEpochMilli()))

    val formattedAmount: String
        get() = "₹%.2f".format(amount)

    val formattedWeight: String
        get() = "%.2f kg".format(weightKg)
}

enum class PaymentStatus {
    PENDING, PARTIAL, PAID, OVERDUE
}

@Serializable
data class MonthlyEarnings(
    @Serializable(with = YearMonthSerializer::class)
    val month: YearMonth,
    val totalEarnings: Double,
    val totalWeight: Double,
    val transactionCount: Int,
    val materialBreakdown: Map<String, MaterialEarnings>
) {
    val formattedMonth: String
        get() = month.toString() // yyyy-MM

    val formattedEarnings: String
        get() = "₹%.2f".format(totalEarnings)
}

@Serializable
data class MaterialEarnings(
    val weightKg: Double,
    val earnings: Double,
    val transactionCount: Int
)