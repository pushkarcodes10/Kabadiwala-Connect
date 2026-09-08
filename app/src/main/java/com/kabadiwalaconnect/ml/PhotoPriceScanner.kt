package com.kabadiwalaconnect.ml

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.kabadiwalaconnect.KabadiwalaApplication
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.MaterialCategory
import com.kabadiwalaconnect.data.repository.KabadiwalaRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class ScanResult(
    val category: MaterialCategory?,
    val confidence: Float,
    val price: Double?
)

class PhotoPriceScanner(
    private val repository: KabadiwalaRepository = KabadiwalaApplication.getInstance().repository,
    private val mapper: MaterialCategoryMapper = MaterialCategoryMapper()
) {

    suspend fun scanPhoto(bitmap: Bitmap): ScanResult {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val labels = suspendCancellableCoroutine<List<ImageLabel>> { continuation ->
                labeler.process(image)
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resumeWithException(it) }
            }
            val topConfidence = labels.maxByOrNull { it.confidence }?.confidence ?: 0f
            val category = mapper.mapLabelsToCategory(labels)
            if (category == null) {
                ScanResult(null, topConfidence, null)
            } else {
                val price = lookupCategoryPrice(category)
                ScanResult(category, topConfidence, price)
            }
        } catch (e: Exception) {
            ScanResult(null, 0f, null)
        }
    }

    private fun lookupCategoryPrice(category: MaterialCategory): Double? {
        val list = repository.materials.value.ifEmpty { Material.ALL_MATERIALS }
        val matching = list.filter { it.category == category }
        return matching.firstOrNull()?.basePricePerKg
    }
}
