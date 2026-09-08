package com.kabadiwalaconnect.ml

import com.google.mlkit.vision.label.ImageLabel
import com.kabadiwalaconnect.data.model.MaterialCategory

class MaterialCategoryMapper {

    fun mapLabelsToCategory(labels: List<ImageLabel>): MaterialCategory? {
        val topLabel = labels.maxByOrNull { it.confidence } ?: return null
        if (topLabel.confidence < 0.6f) {
            return null
        }
        for (label in labels.sortedByDescending { it.confidence }) {
            if (label.confidence < 0.6f) break
            val mapped = mapLabelText(label.text)
            if (mapped != null) {
                return mapped
            }
        }
        return null
    }

    private fun mapLabelText(text: String): MaterialCategory? {
        val lower = text.trim().lowercase()
        return when {
            lower.contains("circuit") || lower.contains("motherboard") || lower.contains("pcb") ||
                    lower.contains("battery") || lower.contains("accumulator") || lower.contains("electronic") ||
                    lower.contains("laptop") || lower.contains("phone") || lower.contains("microcontroller") ->
                MaterialCategory.ELECTRONICS

            lower.contains("wire") || lower.contains("cable") || lower.contains("copper") ||
                    lower.contains("brass") || lower.contains("iron") || lower.contains("steel") ||
                    lower.contains("aluminum") || lower.contains("aluminium") || lower.contains("metal") ||
                    lower.contains("tin") ->
                MaterialCategory.METAL

            lower.contains("plastic") || lower.contains("polyethylene") || lower.contains("pet bottle") ->
                MaterialCategory.PLASTIC

            lower.contains("paper") || lower.contains("cardboard") || lower.contains("newspaper") ||
                    lower.contains("carton") || lower.contains("book") ->
                MaterialCategory.PAPER

            lower.contains("glass") || lower.contains("cullet") ->
                MaterialCategory.GLASS

            lower.contains("tire") || lower.contains("tyre") || lower.contains("rubber") ->
                MaterialCategory.RUBBER

            lower.contains("textile") || lower.contains("fabric") || lower.contains("cloth") ||
                    lower.contains("garment") || lower.contains("clothing") ->
                MaterialCategory.TEXTILE

            lower.contains("packaging") || lower.contains("waste") ->
                MaterialCategory.OTHER

            else -> null
        }
    }
}
