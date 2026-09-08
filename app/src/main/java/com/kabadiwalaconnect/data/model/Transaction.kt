package com.kabadiwalaconnect.data.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Transaction(
    val id: String,
    @Serializable(with = InstantSerializer::class)
    val transactionDate: Instant,
    val recyclerId: String,
    val recyclerName: String,
    val items: List<TransactionItem>,
    val totalWeight: Double,
    val totalAmount: Double,
    val status: TransactionStatus,
    val otp: String? = null,
    val handoverConfirmed: Boolean = false,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val notes: String = "",
    val receiptNumber: String = id.uppercase(),
    val paymentReference: String = "",
    val grossWeight: Double = totalWeight,
    val tareWeight: Double = 0.0,
    val weighbridgeSlipNo: String = "",
    val scheduledTestingTime: String = ""
) {
    val formattedDate: String
        get() = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(transactionDate.toEpochMilli()))

    val formattedAmount: String
        get() = "₹%.2f".format(totalAmount)

    val displayTestingTime: String
        get() = if (scheduledTestingTime.isNotBlank()) {
            scheduledTestingTime
        } else {
            val timeStr = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(transactionDate.toEpochMilli()))
            when (status) {
                TransactionStatus.PENDING -> "Testing Slot: Today, $timeStr (Pending)"
                TransactionStatus.CONFIRMED -> "Testing Slot: In progress ($timeStr)"
                TransactionStatus.COMPLETED -> "Testing Done: Verified at $timeStr"
                TransactionStatus.CANCELLED -> "Testing Slot Cancelled"
            }
        }
}

@Serializable
data class TransactionItem(
    val materialId: String,
    val materialName: String,
    val weightKg: Double,
    val pricePerKg: Double,
    val amount: Double
) {
    val formattedWeight: String
        get() = "%.2f kg".format(weightKg)

    val formattedAmount: String
        get() = "₹%.2f".format(amount)
}

enum class TransactionStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}

enum class PaymentMethod {
    CASH, UPI, BANK_TRANSFER, CHEQUE
}