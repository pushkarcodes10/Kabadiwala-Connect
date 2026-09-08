package com.kabadiwalaconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val name: String,
    val phone: String,
    val email: String? = null,
    val address: String? = null,
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    @Serializable(with = InstantSerializer::class)
    val createdAt: java.time.Instant = java.time.Instant.now(),
    val preferences: UserPreferences = UserPreferences()
)

@Serializable
data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val priceAlertsEnabled: Boolean = true,
    val language: String = "en",
    val themeMode: Int = 0, // 0=system, 1=light, 2=dark
    val autoBackup: Boolean = true
)