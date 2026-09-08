package com.kabadiwalaconnect.ml

import com.google.mlkit.vision.label.ImageLabel
import com.kabadiwalaconnect.data.model.MaterialCategory

class MaterialCategoryMapper {

    fun mapLabelsToCategory(labels: List<ImageLabel>): MaterialCategory? {
        val topLabel = labels.maxByOrNull { it.confidence } ?: return null
        if (topLabel.confidence < 0.6f) {
            return null
        }

        val hasElectronicsContext = labels.any {
            val l = it.text.lowercase()
            l.contains("laptop") || l.contains("computer") || l.contains("screen") ||
                    l.contains("display") || l.contains("keyboard") || l.contains("electronic") ||
                    l.contains("circuit") || l.contains("motherboard") || l.contains("hardware")
        }

        val hasBottleOrPlasticContext = labels.any {
            val l = it.text.lowercase()
            l.contains("bottle") || l.contains("plastic") || l.contains("polyethylene")
        }

        val scores = mutableMapOf<MaterialCategory, Float>()

        for (label in labels) {
            val text = label.text.trim().lowercase()
            val conf = label.confidence

            when {
                text == "laptop" || text.contains("netbook") || text.contains("notebook computer") ||
                        text.contains("personal computer") || text == "computer" || text.contains("desktop computer") -> {
                    scores[MaterialCategory.ELECTRONICS] = (scores[MaterialCategory.ELECTRONICS] ?: 0f) + conf * 4.5f
                }
                text.contains("keyboard") || text.contains("touchpad") || text.contains("circuit board") ||
                        text.contains("motherboard") || text.contains("pcb") || text.contains("microcontroller") -> {
                    scores[MaterialCategory.ELECTRONICS] = (scores[MaterialCategory.ELECTRONICS] ?: 0f) + conf * 3.8f
                }
                text.contains("monitor") || text.contains("screen") || text.contains("display") ||
                        text.contains("electronic") || text.contains("gadget") || text.contains("hardware") -> {
                    scores[MaterialCategory.ELECTRONICS] = (scores[MaterialCategory.ELECTRONICS] ?: 0f) + conf * 2.8f
                }
                text.contains("phone") || text.contains("smartphone") || text.contains("cellular") ||
                        text.contains("battery") || text.contains("accumulator") -> {
                    scores[MaterialCategory.ELECTRONICS] = (scores[MaterialCategory.ELECTRONICS] ?: 0f) + conf * 3.5f
                }

                text.contains("plastic bottle") || text.contains("water bottle") -> {
                    scores[MaterialCategory.PLASTIC] = (scores[MaterialCategory.PLASTIC] ?: 0f) + conf * 4.5f
                }
                text == "bottle" || text.contains("pet bottle") || text.contains("polyethylene") -> {
                    scores[MaterialCategory.PLASTIC] = (scores[MaterialCategory.PLASTIC] ?: 0f) + conf * 3.5f
                }
                text.contains("plastic") || text.contains("container") || text.contains("crate") || text.contains("bucket") -> {
                    scores[MaterialCategory.PLASTIC] = (scores[MaterialCategory.PLASTIC] ?: 0f) + conf * 2.2f
                }

                text.contains("copper") || text.contains("wire") || text.contains("cable") || text.contains("electrical wiring") -> {
                    scores[MaterialCategory.METAL] = (scores[MaterialCategory.METAL] ?: 0f) + conf * 3.8f
                }
                text.contains("brass") || text.contains("bronze") || text.contains("stainless steel") -> {
                    scores[MaterialCategory.METAL] = (scores[MaterialCategory.METAL] ?: 0f) + conf * 3.2f
                }
                text.contains("tin can") || text.contains("aluminum can") || text.contains("aluminium can") -> {
                    scores[MaterialCategory.METAL] = (scores[MaterialCategory.METAL] ?: 0f) + conf * 3.2f
                }
                text == "can" || text.contains("beverage can") -> {
                    if (!hasBottleOrPlasticContext) {
                        scores[MaterialCategory.METAL] = (scores[MaterialCategory.METAL] ?: 0f) + conf * 2.0f
                    }
                }
                text == "metal" || text == "iron" || text.contains("steel") || text.contains("aluminum") || text.contains("aluminium") -> {
                    if (!hasBottleOrPlasticContext) {
                        scores[MaterialCategory.METAL] = (scores[MaterialCategory.METAL] ?: 0f) + conf * 1.5f
                    }
                }

                text.contains("cardboard") || text.contains("carton") || text.contains("corrugated") -> {
                    scores[MaterialCategory.PAPER] = (scores[MaterialCategory.PAPER] ?: 0f) + conf * 3.2f
                }
                text.contains("newspaper") || text.contains("newsprint") -> {
                    scores[MaterialCategory.PAPER] = (scores[MaterialCategory.PAPER] ?: 0f) + conf * 3.2f
                }
                text.contains("book") || text.contains("publication") || text.contains("document") || text == "paper" -> {
                    if (!hasElectronicsContext) {
                        scores[MaterialCategory.PAPER] = (scores[MaterialCategory.PAPER] ?: 0f) + conf * 2.0f
                    }
                }

                text.contains("glass") || text.contains("cullet") -> {
                    scores[MaterialCategory.GLASS] = (scores[MaterialCategory.GLASS] ?: 0f) + conf * 3.2f
                }

                text.contains("tire") || text.contains("tyre") || text.contains("rubber") || text.contains("wheel") -> {
                    scores[MaterialCategory.RUBBER] = (scores[MaterialCategory.RUBBER] ?: 0f) + conf * 3.2f
                }

                text.contains("textile") || text.contains("fabric") || text.contains("clothing") ||
                        text.contains("cloth") || text.contains("garment") || text.contains("shirt") -> {
                    scores[MaterialCategory.TEXTILE] = (scores[MaterialCategory.TEXTILE] ?: 0f) + conf * 3.2f
                }

                text.contains("packaging") || text.contains("tetra pak") -> {
                    scores[MaterialCategory.OTHER] = (scores[MaterialCategory.OTHER] ?: 0f) + conf * 2.2f
                }
            }
        }

        val best = scores.maxByOrNull { it.value }
        return if (best != null && best.value >= 1.0f) best.key else null
    }
}
