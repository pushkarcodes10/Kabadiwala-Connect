package com.kabadiwalaconnect.ml

import com.google.mlkit.vision.label.ImageLabel
import com.kabadiwalaconnect.data.model.MaterialCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MaterialCategoryMapperTest {

    private lateinit var mapper: MaterialCategoryMapper

    @Before
    fun setup() {
        mapper = MaterialCategoryMapper()
    }

    @Test
    fun testLowConfidenceReturnsNull() {
        val labels = listOf(
            ImageLabel("Electronics", 0.59f, 0),
            ImageLabel("Circuit board", 0.55f, 1)
        )
        assertNull(mapper.mapLabelsToCategory(labels))
    }

    @Test
    fun testEmptyLabelsReturnsNull() {
        assertNull(mapper.mapLabelsToCategory(emptyList()))
    }

    @Test
    fun testElectronicsMapping() {
        val labels = listOf(
            ImageLabel("Circuit board", 0.88f, 0),
            ImageLabel("Electronics", 0.85f, 1)
        )
        assertEquals(MaterialCategory.ELECTRONICS, mapper.mapLabelsToCategory(labels))
    }

    @Test
    fun testBatteryMapping() {
        val labels = listOf(
            ImageLabel("Battery", 0.92f, 0)
        )
        assertEquals(MaterialCategory.ELECTRONICS, mapper.mapLabelsToCategory(labels))
    }

    @Test
    fun testWireAndMetalMapping() {
        val labels = listOf(
            ImageLabel("Wire", 0.82f, 0),
            ImageLabel("Cable", 0.79f, 1)
        )
        assertEquals(MaterialCategory.METAL, mapper.mapLabelsToCategory(labels))
    }

    @Test
    fun testPaperAndCardboardMapping() {
        val labels = listOf(
            ImageLabel("Cardboard", 0.91f, 0)
        )
        assertEquals(MaterialCategory.PAPER, mapper.mapLabelsToCategory(labels))
    }

    @Test
    fun testPlasticMapping() {
        val labels = listOf(
            ImageLabel("Plastic bottle", 0.89f, 0)
        )
        assertEquals(MaterialCategory.PLASTIC, mapper.mapLabelsToCategory(labels))
    }
}
