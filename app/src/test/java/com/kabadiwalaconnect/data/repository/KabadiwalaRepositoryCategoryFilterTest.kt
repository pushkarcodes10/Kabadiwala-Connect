package com.kabadiwalaconnect.data.repository

import com.kabadiwalaconnect.data.model.MaterialCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class KabadiwalaRepositoryCategoryFilterTest {

    private lateinit var repository: KabadiwalaRepositoryImpl

    @Before
    fun setup() {
        repository = KabadiwalaRepositoryImpl()
    }

    @Test
    fun testEveryWasteCategoryHasDedicatedRecycler() = runBlocking {
        val categoriesToTest = listOf(
            MaterialCategory.ELECTRONICS,
            MaterialCategory.METAL,
            MaterialCategory.PAPER,
            MaterialCategory.PLASTIC,
            MaterialCategory.GLASS,
            MaterialCategory.TEXTILE,
            MaterialCategory.RUBBER,
            MaterialCategory.OTHER
        )

        val allRecyclers = repository.nearbyRecyclers.value
        assertTrue("Recycler list should not be empty", allRecyclers.isNotEmpty())

        for (category in categoriesToTest) {
            val result = repository.searchRecyclers(
                query = "",
                lat = 28.6139,
                lng = 77.2090,
                radiusKm = 100.0,
                category = category
            )
            assertTrue("Result must be Success", result is Result.Success)
            val matching = (result as Result.Success).data.recyclers
            assertTrue(
                "Category ${category.name} must have at least one dedicated/accepting buyer",
                matching.isNotEmpty()
            )
            // Verify every returned recycler accepts this category
            for (recycler in matching) {
                assertTrue(
                    "Recycler ${recycler.name} must accept ${category.name}",
                    recycler.acceptsCategory(category)
                )
            }
        }
    }

    @Test
    fun testEWasteCategoryShowsOnlyEWasteBuyers() = runBlocking {
        val result = repository.searchRecyclers(
            query = "",
            lat = 28.6139,
            lng = 77.2090,
            radiusKm = 100.0,
            category = MaterialCategory.ELECTRONICS
        )
        assertTrue("Result must be Success", result is Result.Success)
        val eWasteBuyers = (result as Result.Success).data.recyclers
        assertFalse("E-waste buyers should not be empty", eWasteBuyers.isEmpty())

        for (buyer in eWasteBuyers) {
            assertTrue(
                "Buyer '${buyer.name}' shown for E-Waste must accept ELECTRONICS",
                buyer.acceptsCategory(MaterialCategory.ELECTRONICS)
            )
            // Ensure pure non-electronics facilities are NOT included
            assertFalse(
                "Pure paper mill should not be in e-waste list",
                buyer.id == "recycler_bengaluru_eco"
            )
            assertFalse(
                "Pure plastic recycler should not be in e-waste list",
                buyer.id == "recycler_balaji_plastics"
            )
            assertFalse(
                "Pure tyre recycler should not be in e-waste list",
                buyer.id == "recycler_royal_tyres"
            )
        }

        // Verify the dedicated e-waste facilities are returned
        val ids = eWasteBuyers.map { it.id }
        assertTrue("Should contain Metro E-Waste", ids.contains("recycler_metro_ewaste"))
        assertTrue("Should contain EcoTron Battery Hub", ids.contains("recycler_delhi_ncr_battery"))
    }

    @Test
    fun testMetalCategoryShowsOnlyMetalBuyers() = runBlocking {
        val result = repository.searchRecyclers(
            query = "",
            lat = 28.6139,
            lng = 77.2090,
            radiusKm = 100.0,
            category = MaterialCategory.METAL
        )
        assertTrue("Result must be Success", result is Result.Success)
        val metalBuyers = (result as Result.Success).data.recyclers
        assertFalse("Metal buyers should not be empty", metalBuyers.isEmpty())

        for (buyer in metalBuyers) {
            assertTrue(
                "Buyer '${buyer.name}' shown for Metal must accept METAL",
                buyer.acceptsCategory(MaterialCategory.METAL)
            )
        }

        val ids = metalBuyers.map { it.id }
        assertTrue("Should contain Sharma Metal", ids.contains("recycler_sharma"))
        assertTrue("Should contain Gupta Brothers Copper", ids.contains("recycler_gupta_copper"))
    }

    @Test
    fun testAllOrNullReturnsAllRecyclers() = runBlocking {
        val allRecyclers = repository.nearbyRecyclers.value
        val fromNull = (repository.searchRecyclers(query = "", lat = 28.6139, lng = 77.2090, radiusKm = 100.0, category = null) as Result.Success).data.recyclers
        val fromAll = (repository.searchRecyclers(query = "", lat = 28.6139, lng = 77.2090, radiusKm = 100.0, category = MaterialCategory.ALL) as Result.Success).data.recyclers

        assertEquals("Null category filter should return all recyclers", allRecyclers.size, fromNull.size)
        assertEquals("ALL category filter should return all recyclers", allRecyclers.size, fromAll.size)
    }
}
