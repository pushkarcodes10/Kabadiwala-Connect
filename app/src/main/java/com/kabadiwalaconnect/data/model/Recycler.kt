package com.kabadiwalaconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Recycler(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val reviewCount: Int,
    val acceptedMaterials: List<String>,
    val workingHours: String,
    val imageUrl: String? = null,
    val isVerified: Boolean = false,
    val distanceKm: Double = 0.0,
    val contactPerson: String = "",
    val whatsappNumber: String = "",
    val paymentModes: List<String> = listOf("Cash on Spot", "Instant UPI"),
    val minPickupKg: Double = 10.0,
    val facilityType: String = "Authorized Mandi Yard",
    val isOpenNow: Boolean = true,
    val primaryCategory: MaterialCategory? = null
) {
    val formattedDistance: String
        get() = if (distanceKm < 1) {
            "${(distanceKm * 1000).toInt()} m"
        } else {
            "%.1f km".format(distanceKm)
        }

    val formattedRating: String
        get() = "%.1f".format(rating)

    val dedicatedCategory: MaterialCategory?
        get() = primaryCategory ?: acceptedMaterials.firstNotNullOfOrNull { Material.getById(it)?.category }

    fun acceptsCategory(category: MaterialCategory): Boolean {
        if (category == MaterialCategory.ALL) return true
        if (primaryCategory != null) {
            return primaryCategory == category
        }
        return acceptedMaterials.any { matId ->
            Material.getById(matId)?.category == category
        }
    }
}

@Serializable
data class RecyclerSearchResult(
    val recyclers: List<Recycler>,
    val totalCount: Int,
    val searchRadiusKm: Double
)