package com.kabadiwalaconnect.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable object Splash : Screen("splash")
    @Serializable object Intro : Screen("intro")
    @Serializable object Logo : Screen("logo")
    @Serializable object Home : Screen("home")
    @Serializable object MaterialEntry : Screen("material_entry")
    @Serializable object MarketPrices : Screen("market_prices")
    @Serializable object RecyclerDiscovery : Screen("recycler_discovery")
    @Serializable object Transaction : Screen("transaction")
    @Serializable object Earnings : Screen("earnings")
    @Serializable object Safety : Screen("safety")
    @Serializable object MaterialScanner : Screen("material_scanner")

    @Serializable
    data class MaterialEntryWithId(val materialId: String) : Screen("material_entry?materialId=$materialId") {
        companion object {
            const val route = "material_entry?materialId={materialId}"
        }
    }

    @Serializable
    data class TransactionDetail(val transactionId: String) : Screen("transaction/$transactionId") {
        companion object {
            const val route = "transaction/{transactionId}"
        }
    }

    @Serializable
    data class RecyclerDetail(val recyclerId: String) : Screen("recycler/$recyclerId") {
        companion object {
            const val route = "recycler/{recyclerId}"
        }
    }
}