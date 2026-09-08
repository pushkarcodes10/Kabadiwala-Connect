package com.kabadiwalaconnect.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class PendingPickup(
    val materialId: String,
    val materialName: String,
    val weightKg: Double,
    val estimatedPrice: Double,
    val ratePerKg: Double
)

object PendingPickupStore {
    var pendingPickup by mutableStateOf<PendingPickup?>(null)
        private set

    var pendingCartItems by mutableStateOf<List<ScrapCartItem>>(emptyList())
        private set

    fun setPendingItems(items: List<ScrapCartItem>) {
        pendingCartItems = items
        if (items.isNotEmpty()) {
            val first = items.first()
            val totalWeight = items.sumOf { it.weightKg }
            val totalAmount = items.sumOf { it.estimatedAmount }
            val summaryName = if (items.size == 1) {
                first.material.name
            } else {
                "${first.material.name} + ${items.size - 1} more"
            }
            pendingPickup = PendingPickup(
                materialId = first.material.id,
                materialName = summaryName,
                weightKg = totalWeight,
                estimatedPrice = totalAmount,
                ratePerKg = first.material.basePricePerKg
            )
        } else {
            pendingPickup = null
        }
    }

    fun setPending(
        materialId: String,
        materialName: String,
        weightKg: Double,
        estimatedPrice: Double,
        ratePerKg: Double
    ) {
        pendingPickup = PendingPickup(
            materialId = materialId,
            materialName = materialName,
            weightKg = weightKg,
            estimatedPrice = estimatedPrice,
            ratePerKg = ratePerKg
        )
        val mat = Material.getById(materialId)
        if (mat != null) {
            pendingCartItems = listOf(ScrapCartItem(material = mat, weightKg = weightKg))
        }
    }

    fun clear() {
        pendingPickup = null
        pendingCartItems = emptyList()
    }

    fun hasPending(): Boolean = pendingPickup != null || pendingCartItems.isNotEmpty()
}
