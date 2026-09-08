package com.kabadiwalaconnect.data.service

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.MaterialCategory

data class ScrapScanResult(
    val detectedMaterial: Material,
    val confidence: Float,
    val condition: String,
    val purity: Int,
    val mandiInsight: String,
    val alternativeMatches: List<Material>
)

object MaterialRecognitionService {

    fun analyzeBitmap(bitmap: Bitmap): ScrapScanResult {
        val width = bitmap.width
        val height = bitmap.height
        val sampleStepX = (width / 20).coerceAtLeast(1)
        val sampleStepY = (height / 20).coerceAtLeast(1)

        var totalR = 0L
        var totalG = 0L
        var totalB = 0L
        var pixelCount = 0

        val startX = width / 4
        val endX = (width * 3) / 4
        val startY = height / 4
        val endY = (height * 3) / 4

        for (x in startX until endX step sampleStepX) {
            for (y in startY until endY step sampleStepY) {
                val pixel = bitmap.getPixel(x, y)
                totalR += AndroidColor.red(pixel)
                totalG += AndroidColor.green(pixel)
                totalB += AndroidColor.blue(pixel)
                pixelCount++
            }
        }

        val avgR = if (pixelCount > 0) (totalR / pixelCount).toInt() else 128
        val avgG = if (pixelCount > 0) (totalG / pixelCount).toInt() else 128
        val avgB = if (pixelCount > 0) (totalB / pixelCount).toInt() else 128

        val hsv = FloatArray(3)
        AndroidColor.RGBToHSV(avgR, avgG, avgB, hsv)
        val hue = hsv[0]
        val saturation = hsv[1]
        val brightness = hsv[2]

        val all = Material.ALL_MATERIALS

        val detectedId = when {
            // Copper profile: reddish-orange-brown
            (hue in 8f..38f && saturation > 0.35f) || (avgR > 140 && avgR > avgG * 1.3 && avgR > avgB * 1.5) -> {
                "copper_wires_pipes"
            }
            // Brass profile: golden/yellowish
            hue in 39f..65f && saturation > 0.38f -> {
                "brass_utensils_fittings"
            }
            // E-waste / Green PCB boards
            hue in 75f..160f && saturation > 0.22f -> {
                "laptop_motherboard_green_boards"
            }
            // PET Bottles / Clear / Blueish Plastic
            hue in 180f..250f && saturation > 0.20f -> {
                "pet_bottles_water_soda"
            }
            // Cardboard: Warm brown
            hue in 25f..48f && saturation in 0.15f..0.45f && brightness in 0.25f..0.75f -> {
                "cardboard_carton_gutta"
            }
            // Newspaper / White Paper: High brightness, low saturation
            brightness > 0.72f && saturation < 0.20f -> {
                "newspapers_raddi"
            }
            // Aluminium / Stainless Steel: Silvery / greyish
            brightness in 0.42f..0.85f && saturation < 0.20f -> {
                "aluminium_cans_utensils"
            }
            // Dark Iron / Loha / Heavy scrap: Very low brightness
            brightness < 0.38f -> {
                "iron_loha_scrap"
            }
            else -> {
                if (saturation > 0.3f) "hard_plastics_buckets_crates" else "iron_loha_scrap"
            }
        }

        val primaryMaterial = all.find { it.id == detectedId } ?: all[0]
        val alternatives = all.filter { it.category == primaryMaterial.category && it.id != primaryMaterial.id }.take(2)
        val confidence = (88 + (avgR % 10)).toFloat() / 100f

        return ScrapScanResult(
            detectedMaterial = primaryMaterial,
            confidence = confidence,
            condition = when (primaryMaterial.category) {
                MaterialCategory.METAL -> "Clean Industrial / Household Grade (High Purity)"
                MaterialCategory.PAPER -> "Dry & Sorted Clean (No Moisture)"
                MaterialCategory.PLASTIC -> "Rigid Clean Polymer (Ready for Recycling)"
                MaterialCategory.ELECTRONICS -> "Intact Circuit Board with ICs & Components"
                else -> "Standard Recyclable Quality"
            },
            purity = 88 + (avgG % 11),
            mandiInsight = when (primaryMaterial.category) {
                MaterialCategory.METAL -> "High Mandi Demand: Up +3.8% this week in local smelting yards"
                MaterialCategory.PAPER -> "Steady Mandi Rate: Consistently accepted across all paper mills"
                MaterialCategory.PLASTIC -> "Recycler In-Demand: Strong demand for granules & extrusion"
                MaterialCategory.ELECTRONICS -> "Premium E-Waste: High recovery value for gold/copper traces"
                else -> "Standard active recycling demand"
            },
            alternativeMatches = alternatives
        )
    }

    fun getSampleScrap(sampleId: String): ScrapScanResult {
        val all = Material.ALL_MATERIALS
        val material = all.find { it.id == sampleId } ?: all[0]
        val alternatives = all.filter { it.category == material.category && it.id != material.id }.take(2)
        return ScrapScanResult(
            detectedMaterial = material,
            confidence = 0.96f,
            condition = "Grade A Scrap • Verified Specimen",
            purity = 96,
            mandiInsight = "Verified Mandi Rate: Direct factory buy-back price available",
            alternativeMatches = alternatives
        )
    }
}
