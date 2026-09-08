package com.kabadiwalaconnect.data.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MarketPrice(
    val materialId: String,
    val materialName: String,
    val pricePerKg: Double,
    val trend: PriceTrend,
    val changePercent: Double,
    @Serializable(with = InstantSerializer::class)
    val lastUpdated: Instant,
    val source: String = "Local Market"
) {
    val formattedPrice: String
        get() = "₹%.2f/kg".format(pricePerKg)

    val formattedChange: String
        get() = when (trend) {
            PriceTrend.UP -> "+%.1f%%".format(changePercent)
            PriceTrend.DOWN -> "%.1f%%".format(changePercent)
            PriceTrend.STABLE -> "0.0%%"
        }

    val trendColor: Int
        get() = when (trend) {
            PriceTrend.UP -> 0xFF2E7D32.toInt()
            PriceTrend.DOWN -> 0xFFC62828.toInt()
            PriceTrend.STABLE -> 0xFF757575.toInt()
        }
}

enum class PriceTrend {
    UP, DOWN, STABLE
}