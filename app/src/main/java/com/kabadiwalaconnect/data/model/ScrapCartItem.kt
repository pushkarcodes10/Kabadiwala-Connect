package com.kabadiwalaconnect.data.model

/**
 * Represents a single item in the scrap selling cart.
 * Users can add multiple ScrapCartItems before confirming a sale.
 */
data class ScrapCartItem(
    val material: Material,
    val weightKg: Double
) {
    val estimatedAmount: Double get() = weightKg * material.basePricePerKg
    val formattedWeight: String get() = if (weightKg % 1.0 == 0.0) "%.0f kg".format(weightKg) else "%.1f kg".format(weightKg)
    val formattedAmount: String get() = "₹%.2f".format(estimatedAmount)
}
