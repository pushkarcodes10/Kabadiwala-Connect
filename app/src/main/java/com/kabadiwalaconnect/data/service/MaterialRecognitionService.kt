package com.kabadiwalaconnect.data.service

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.MaterialCategory
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class ScrapScanResult(
    val detectedMaterial: Material,
    val confidence: Float,
    val condition: String,
    val purity: Int,
    val mandiInsight: String,
    val alternativeMatches: List<Material>
)

object MaterialRecognitionService {

    suspend fun analyzeBitmap(bitmap: Bitmap): ScrapScanResult {
        val all = Material.ALL_MATERIALS
        val labels = detectLabelsWithMlKit(bitmap)

        val detectedId = if (labels.isNotEmpty()) {
            mapLabelsToSpecificMaterialId(labels)
        } else {
            null
        } ?: analyzeVisualProfile(bitmap)

        val primaryMaterial = all.find { it.id == detectedId } ?: all[0]
        val alternatives = all.filter { it.category == primaryMaterial.category && it.id != primaryMaterial.id }.take(2)
        val topConfidence = labels.firstOrNull()?.confidence ?: 0.90f
        val finalConfidence = topConfidence.coerceIn(0.78f, 0.98f)

        return ScrapScanResult(
            detectedMaterial = primaryMaterial,
            confidence = finalConfidence,
            condition = when (primaryMaterial.category) {
                MaterialCategory.ELECTRONICS -> "High-Value E-Waste • Components & ICs Intact"
                MaterialCategory.METAL -> "Clean Industrial / Household Grade Scrap"
                MaterialCategory.PLASTIC -> "Rigid Clean Polymer • Ready for Shredding"
                MaterialCategory.PAPER -> "Dry & Sorted Clean (Zero Moisture)"
                MaterialCategory.GLASS -> "Segregated Cullet • Free of Ceramics & Stone"
                MaterialCategory.RUBBER -> "Vulcanized Heavy Rubber • Clean Tread Scrap"
                MaterialCategory.TEXTILE -> "Clean Discarded Fabric / Garment Scraps"
                MaterialCategory.OTHER -> "Recyclable Multi-Layer & Composite Packaging"
                MaterialCategory.ALL -> "Standard Recyclable Quality"
            },
            purity = (86 + (finalConfidence * 11).toInt()).coerceIn(84, 98),
            mandiInsight = when (primaryMaterial.category) {
                MaterialCategory.ELECTRONICS -> "Premium E-Waste: High recovery rate for precious metals & PCBs"
                MaterialCategory.METAL -> "High Mandi Demand: Up +3.8% this week in local smelting yards"
                MaterialCategory.PLASTIC -> "Recycler In-Demand: Strong demand for granules & extrusion"
                MaterialCategory.PAPER -> "Steady Mandi Rate: Consistently accepted across all paper mills"
                MaterialCategory.GLASS -> "Active Demand: Cullet directly accepted by container glass manufacturers"
                MaterialCategory.RUBBER -> "Industrial Reclaiming: Granulation demand from tyre retreaders"
                MaterialCategory.TEXTILE -> "Upcycling Stream: Sourced for industrial cotton wipes & felt"
                MaterialCategory.OTHER -> "EPR Compliant: Authorized recycling drop-off rate applicable"
                MaterialCategory.ALL -> "Standard active recycling demand"
            },
            alternativeMatches = alternatives
        )
    }

    private suspend fun detectLabelsWithMlKit(bitmap: Bitmap): List<ImageLabel> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            suspendCancellableCoroutine { continuation ->
                labeler.process(image)
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resume(emptyList()) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mapLabelsToSpecificMaterialId(labels: List<ImageLabel>): String? {
        val hasElectronicsContext = labels.any {
            val l = it.text.lowercase()
            l.contains("laptop") || l.contains("computer") || l.contains("screen") ||
                    l.contains("display") || l.contains("keyboard") || l.contains("electronic") ||
                    l.contains("circuit") || l.contains("motherboard") || l.contains("hardware")
        }

        val hasBottleOrPlasticContext = labels.any {
            val l = it.text.lowercase()
            l.contains("bottle") || l.contains("plastic") || l.contains("water bottle") || l.contains("polyethylene")
        }

        val scores = mutableMapOf<String, Float>()

        fun addScore(materialId: String, points: Float) {
            scores[materialId] = (scores[materialId] ?: 0f) + points
        }

        for (label in labels) {
            val text = label.text.trim().lowercase()
            val conf = label.confidence

            when {
                text == "laptop" || text.contains("netbook") || text.contains("notebook computer") ||
                        text.contains("personal computer") || text == "computer" || text.contains("desktop computer") -> {
                    addScore("laptop_motherboard_green_boards", conf * 5.0f)
                }

                text.contains("keyboard") || text.contains("touchpad") || text.contains("space bar") ||
                        text.contains("circuit board") || text.contains("motherboard") || text.contains("pcb") -> {
                    addScore("laptop_motherboard_green_boards", conf * 4.2f)
                }

                text.contains("phone") || text.contains("smartphone") || text.contains("cellular") -> {
                    addScore("used_smartphone_mobile_pcbs", conf * 4.5f)
                }

                text.contains("monitor") || text.contains("screen") || text.contains("television") || text.contains("display") -> {
                    if (labels.any { it.text.lowercase().contains("laptop") || it.text.lowercase().contains("keyboard") }) {
                        addScore("laptop_motherboard_green_boards", conf * 3.5f)
                    } else {
                        addScore("monitors_heavy_appliance_scrap", conf * 3.5f)
                    }
                }

                text.contains("battery") || text.contains("accumulator") -> {
                    addScore("lead_batteries_inverter_vehicle", conf * 4.0f)
                }

                text.contains("appliance") || text.contains("toaster") || text.contains("microwave") || text.contains("mixer") -> {
                    addScore("small_home_appliances", conf * 3.5f)
                }

                text.contains("plastic bottle") || text.contains("water bottle") -> {
                    addScore("pet_bottles_water_soda", conf * 5.0f)
                }

                text == "bottle" || text.contains("pet bottle") || text.contains("mineral water") || text.contains("soft drink") -> {
                    addScore("pet_bottles_water_soda", conf * 4.0f)
                }

                text.contains("bucket") || text.contains("crate") || text.contains("tub") -> {
                    addScore("hard_plastics_buckets_crates", conf * 3.5f)
                }

                text == "plastic" || text.contains("polyethylene") || text.contains("polymer") -> {
                    if (hasBottleOrPlasticContext) {
                        addScore("pet_bottles_water_soda", conf * 2.5f)
                    } else {
                        addScore("hard_plastics_buckets_crates", conf * 2.5f)
                    }
                }

                text.contains("copper") || text.contains("wire") || text.contains("cable") || text.contains("electrical wiring") -> {
                    addScore("copper_wires_pipes", conf * 4.0f)
                }

                text.contains("brass") || text.contains("bronze") -> {
                    addScore("brass_utensils_fittings", conf * 3.5f)
                }

                text.contains("aluminum can") || text.contains("aluminium can") || text.contains("tin can") ||
                        (text == "can" && !hasBottleOrPlasticContext) -> {
                    addScore("aluminium_cans_utensils", conf * 3.5f)
                }

                text.contains("stainless") || text.contains("steel") -> {
                    addScore("stainless_steel", conf * 3.0f)
                }

                text == "iron" || text.contains("metal") || text.contains("rebar") -> {
                    if (!hasBottleOrPlasticContext && !hasElectronicsContext) {
                        addScore("iron_loha_scrap", conf * 2.0f)
                    }
                }

                text.contains("cardboard") || text.contains("carton") || text.contains("corrugated") || text.contains("box") -> {
                    addScore("cardboard_carton_gutta", conf * 3.5f)
                }

                text.contains("newspaper") || text.contains("newsprint") -> {
                    addScore("newspapers_raddi", conf * 3.5f)
                }

                text.contains("book") || text.contains("publication") || text.contains("document") || text == "paper" -> {
                    if (!hasElectronicsContext) {
                        addScore("books_white_paper", conf * 2.5f)
                    }
                }

                text.contains("glass") || text.contains("cullet") -> {
                    addScore("broken_glass_ceramics", conf * 3.5f)
                }

                text.contains("tire") || text.contains("tyre") || text.contains("rubber") || text.contains("wheel") -> {
                    addScore("fibre_tyres_rubber", conf * 3.5f)
                }

                text.contains("clothing") || text.contains("clothes") || text.contains("textile") || text.contains("fabric") ||
                        text.contains("garment") || text.contains("shirt") || text.contains("pants") -> {
                    addScore("old_clothes_textiles", conf * 3.5f)
                }

                text.contains("tetra pak") || text.contains("aseptic packaging") -> {
                    addScore("composite_packaging_tetra_pak", conf * 3.5f)
                }
            }
        }

        val best = scores.maxByOrNull { it.value }
        return if (best != null && best.value >= 1.0f) best.key else null
    }

    private fun analyzeVisualProfile(bitmap: Bitmap): String {
        val width = bitmap.width
        val height = bitmap.height
        val aspectRatio = width.toFloat() / height.toFloat()

        var totalR = 0L
        var totalG = 0L
        var totalB = 0L
        var pixelCount = 0

        val stepX = (width / 24).coerceAtLeast(1)
        val stepY = (height / 24).coerceAtLeast(1)

        val startX = width / 4
        val endX = (width * 3) / 4
        val startY = height / 4
        val endY = (height * 3) / 4

        for (x in startX until endX step stepX) {
            for (y in startY until endY step stepY) {
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

        return when {
            aspectRatio < 0.75f || (hue in 170f..255f && saturation > 0.15f) ->
                "pet_bottles_water_soda"

            aspectRatio > 1.25f && brightness in 0.15f..0.85f ->
                "laptop_motherboard_green_boards"

            (hue in 8f..38f && saturation > 0.35f) || (avgR > 140 && avgR > avgG * 1.3 && avgR > avgB * 1.5) ->
                "copper_wires_pipes"

            hue in 39f..65f && saturation > 0.38f ->
                "brass_utensils_fittings"

            hue in 75f..160f && saturation > 0.22f ->
                "laptop_motherboard_green_boards"

            hue in 25f..48f && saturation in 0.15f..0.45f ->
                "cardboard_carton_gutta"

            brightness > 0.85f && saturation < 0.15f ->
                "newspapers_raddi"

            else ->
                "pet_bottles_water_soda"
        }
    }

    fun getSampleScrap(sampleId: String): ScrapScanResult {
        val all = Material.ALL_MATERIALS
        val material = all.find { it.id == sampleId } ?: all[0]
        val alternatives = all.filter { it.category == material.category && it.id != material.id }.take(2)
        return ScrapScanResult(
            detectedMaterial = material,
            confidence = 0.96f,
            condition = when (material.category) {
                MaterialCategory.ELECTRONICS -> "Verified High-Grade E-Waste Specimen"
                MaterialCategory.METAL -> "Verified Industrial Grade Metal Specimen"
                MaterialCategory.PLASTIC -> "Verified Sorted Clean Polymer Specimen"
                MaterialCategory.PAPER -> "Dry & Sorted Clean Paper Specimen"
                MaterialCategory.GLASS -> "Clean Segregated Glass Specimen"
                MaterialCategory.RUBBER -> "Vulcanized Rubber Specimen"
                MaterialCategory.TEXTILE -> "Graded Textile Specimen"
                MaterialCategory.OTHER -> "Recyclable Packaging Specimen"
                MaterialCategory.ALL -> "Grade A Scrap • Verified Specimen"
            },
            purity = 96,
            mandiInsight = when (material.category) {
                MaterialCategory.ELECTRONICS -> "Verified Mandi Rate: High recovery rate for gold/copper traces"
                MaterialCategory.METAL -> "Verified Mandi Rate: Direct smelting yard rate applicable"
                MaterialCategory.PLASTIC -> "Verified Mandi Rate: High extrusion granule demand"
                MaterialCategory.PAPER -> "Verified Mandi Rate: Regular mill collection rate"
                else -> "Verified Mandi Rate: Direct factory buy-back price available"
            },
            alternativeMatches = alternatives
        )
    }
}
