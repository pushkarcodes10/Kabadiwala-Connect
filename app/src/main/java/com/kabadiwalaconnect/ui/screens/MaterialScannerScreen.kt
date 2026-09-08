package com.kabadiwalaconnect.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.kabadiwalaconnect.language.localizedUi
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.kabadiwalaconnect.data.service.MaterialRecognitionService
import com.kabadiwalaconnect.data.service.ScrapScanResult
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.MaterialImage
import com.kabadiwalaconnect.ui.components.getMaterialIconRes
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.runtime.LaunchedEffect
import com.kabadiwalaconnect.data.model.PendingPickupStore
import com.kabadiwalaconnect.data.model.ScrapCartItem
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.LocalLanguageViewModel
import com.kabadiwalaconnect.language.localizedName
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton

@Composable
fun MaterialScannerScreen(
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit = { onNavigate(Screen.Home) },
    languageViewModel: LanguageViewModel? = LocalLanguageViewModel.current
) {
    val strings = currentStrings()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScrapScanResult?>(null) }
    var selectedWeightKg by remember { mutableDoubleStateOf(10.0) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionAlert by remember { mutableStateOf(false) }

    fun processBitmap(bitmap: Bitmap) {
        capturedBitmap = bitmap
        isAnalyzing = true
        scope.launch {
            delay(600) // Brief animation simulation for AI scan
            scanResult = MaterialRecognitionService.analyzeBitmap(bitmap)
            isAnalyzing = false
        }
    }

    // Fallback preview thumbnail contract (for devices/emulators with non-standard camera apps)
    val takePicturePreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            processBitmap(bitmap)
        } else if (capturedBitmap == null && scanResult == null) {
            Toast.makeText(context, "No photo captured. You can also pick from Gallery or test with sample scrap.", Toast.LENGTH_SHORT).show()
        }
    }

    // High resolution capture via FileProvider
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempPhotoUri != null) {
            val bitmap = decodeAndCorrectExif(context, tempPhotoUri!!)
            if (bitmap != null) {
                processBitmap(bitmap)
            } else {
                try {
                    takePicturePreviewLauncher.launch(null)
                } catch (e: Exception) {
                    Toast.makeText(context, "Could not load photo. Please retry or choose from Gallery.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // User cancelled camera or image capture failed
            Toast.makeText(context, "Camera cancelled. You can retry, select from Gallery, or test with sample scrap.", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCameraIntent() {
        showPermissionAlert = false
        try {
            val storageDir = File(context.externalCacheDir ?: context.cacheDir, "camera_photos").apply { mkdirs() }
            val photoFile = File.createTempFile("scrap_${System.currentTimeMillis()}_", ".jpg", storageDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            tempPhotoUri = uri
            takePictureLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                takePicturePreviewLauncher.launch(null)
            } catch (e2: Exception) {
                e2.printStackTrace()
                Toast.makeText(
                    context,
                    "Camera error: ${e2.localizedMessage ?: "No camera found"}. Choose from Gallery or use sample items.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showPermissionAlert = true
        }
        // Still attempt launch, as system camera intent does not require app-level CAMERA permission
        launchCameraIntent()
    }

    fun checkAndLaunchCamera() {
        val permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            launchCameraIntent()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Auto-launch camera when entering scanner if nothing is scanned yet
    LaunchedEffect(Unit) {
        if (capturedBitmap == null && scanResult == null) {
            checkAndLaunchCamera()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = decodeAndCorrectExif(context, uri)
            if (bitmap != null) {
                processBitmap(bitmap)
            } else {
                Toast.makeText(context, "Could not load selected photo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KabadiwalaColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = KabadiwalaColors.Primary
                    )
                }
                Column {
                    Text(
                        text = strings.aiScannerTitle,
                        style = KabadiwalaTypography.TitleLarge,
                        fontWeight = FontWeight.Bold,
                        color = KabadiwalaColors.OnBackground
                    )
                    Text(
                        text = strings.aiScannerSubtitle,
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant
                    )
                }
            }
            LanguageTopBarButton()
        }

        // Permission Alert Banner (if previously denied)
        if (showPermissionAlert) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.ErrorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Permission Alert",
                        tint = KabadiwalaColors.Error
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Camera Permission Required",
                            style = KabadiwalaTypography.LabelLarge,
                            fontWeight = FontWeight.Bold,
                            color = KabadiwalaColors.Error
                        )
                        Text(
                            text = "Tap here to allow camera access, or choose from Gallery / Samples below.",
                            style = KabadiwalaTypography.BodySmall,
                            color = KabadiwalaColors.Error
                        )
                    }
                }
            }
        }

        // Camera / Viewfinder Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(KabadiwalaShapes.Large)
                .clickable { checkAndLaunchCamera() },
            colors = CardDefaults.cardColors(
                containerColor = KabadiwalaColors.Surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (capturedBitmap != null) {
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = "Captured scrap",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isAnalyzing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.55f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
                                Text(
                                    text = "Analyzing material & matching mandi rates...",
                                    color = Color.White,
                                    style = KabadiwalaTypography.BodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else if (scanResult != null) {
                    // Specimen / Sample Loaded Preview
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(KabadiwalaColors.PrimaryContainer.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MaterialImage(
                                material = scanResult!!.detectedMaterial,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Text(
                                text = scanResult!!.detectedMaterial.localizedName,
                                style = KabadiwalaTypography.TitleMedium,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.OnBackground
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = KabadiwalaColors.PrimaryContainer
                            ) {
                                Text(
                                    text = localizedUi("specimen_loaded", scanResult!!.detectedMaterial.rateRange),
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = KabadiwalaColors.Primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Empty Viewfinder Placeholder
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                            .border(
                                width = 2.dp,
                                color = KabadiwalaColors.Primary.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(KabadiwalaColors.Primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Camera",
                                tint = KabadiwalaColors.Primary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = localizedUi("tap_to_open_camera"),
                            style = KabadiwalaTypography.TitleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = localizedUi("scanner_materials_subtitle"),
                            style = KabadiwalaTypography.BodySmall,
                            color = KabadiwalaColors.OnSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Camera Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { checkAndLaunchCamera() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KabadiwalaColors.Primary,
                    contentColor = KabadiwalaColors.OnPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (capturedBitmap != null || scanResult != null) localizedUi("retake_photo") else localizedUi("take_photo"),
                    style = KabadiwalaTypography.LabelLarge
                )
            }

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoLibrary,
                    contentDescription = null,
                    tint = KabadiwalaColors.Primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = localizedUi("from_gallery"),
                    style = KabadiwalaTypography.LabelLarge,
                    color = KabadiwalaColors.Primary
                )
            }
        }

        // Quick Specimen Samples (Ideal for instant testing and emulator use)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = localizedUi("test_with_samples"),
                style = KabadiwalaTypography.LabelMedium,
                color = KabadiwalaColors.OnSurfaceVariant
            )
            val testSamples = listOf(
                "laptop_motherboard_green_boards" to "💻 Laptop / PC",
                "used_smartphone_mobile_pcbs" to "📱 Smartphone",
                "lead_batteries_inverter_vehicle" to "🔋 Inverter Battery",
                "copper_wires_pipes" to "🔌 Copper Wires",
                "iron_loha_scrap" to "🔩 Iron / Loha",
                "brass_utensils_fittings" to "🟡 Brass Utensils",
                "aluminium_cans_utensils" to "🥫 Aluminium Cans",
                "newspapers_raddi" to "📰 Raddi / Newspaper",
                "cardboard_carton_gutta" to "📦 Cardboard Carton",
                "pet_bottles_water_soda" to "🧴 PET Bottles",
                "hard_plastics_buckets_crates" to "🪣 Hard Plastic",
                "broken_glass_ceramics" to "🍾 Broken Glass",
                "fibre_tyres_rubber" to "🛞 Tyres / Rubber",
                "old_clothes_textiles" to "👕 Old Clothes",
                "composite_packaging_tetra_pak" to "🧃 Tetra Pak"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                testSamples.forEach { (sampleId, label) ->
                    FilterChip(
                        selected = scanResult?.detectedMaterial?.id == sampleId,
                        onClick = {
                            capturedBitmap = null
                            isAnalyzing = true
                            scope.launch {
                                delay(350)
                                scanResult = MaterialRecognitionService.getSampleScrap(sampleId)
                                isAnalyzing = false
                            }
                        },
                        label = { Text(label, style = KabadiwalaTypography.LabelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = KabadiwalaColors.PrimaryContainer,
                            selectedLabelColor = KabadiwalaColors.Primary
                        )
                    )
                }
            }
        }

        // Recognition & Fair Price Result Card
        AnimatedVisibility(
            visible = scanResult != null && !isAnalyzing,
            enter = fadeIn() + slideInVertically()
        ) {
            scanResult?.let { result ->
                val material = result.detectedMaterial
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = KabadiwalaShapes.Large
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Detection Status Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = KabadiwalaColors.Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = localizedUi("scrap_identified"),
                                    style = KabadiwalaTypography.LabelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.Primary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(KabadiwalaColors.PrimaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${(result.confidence * 100).toInt()}% Match",
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.Primary
                                )
                            }
                        }

                        // Material Title and Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            MaterialImage(
                                material = material,
                                modifier = Modifier.size(56.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = material.localizedName,
                                    style = KabadiwalaTypography.TitleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = result.condition,
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                            }
                        }

                        // Fair Mandi Rate Hero Box
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = localizedUi("current_fair_market_rate"),
                                        style = KabadiwalaTypography.LabelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = KabadiwalaColors.Primary
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            tint = KabadiwalaColors.Primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = localizedUi("live_mandi_rate"),
                                            style = KabadiwalaTypography.LabelSmall,
                                            color = KabadiwalaColors.Primary
                                        )
                                    }
                                }
                                Text(
                                    text = material.rateRange,
                                    style = KabadiwalaTypography.HeadlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = KabadiwalaColors.Primary
                                )
                                Text(
                                    text = localizedUi("fair_benchmark", "%.0f".format(material.basePricePerKg), result.purity),
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.Primary.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // Mandi Market Insight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = KabadiwalaColors.Info,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = result.mandiInsight,
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }

                        // Quick Quantity Valuation Estimator
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = localizedUi("quick_payout_estimator"),
                                style = KabadiwalaTypography.LabelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(5.0, 10.0, 25.0, 50.0).forEach { weightVal ->
                                    val isSelected = selectedWeightKg == weightVal
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) KabadiwalaColors.Primary else KabadiwalaColors.SurfaceVariant.copy(alpha = 0.5f))
                                            .clickable { selectedWeightKg = weightVal }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${weightVal.toInt()} kg",
                                            style = KabadiwalaTypography.LabelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else KabadiwalaColors.OnSurface
                                        )
                                    }
                                }
                            }
                            val minVal = selectedWeightKg * material.minPricePerKg
                            val maxVal = selectedWeightKg * material.maxPricePerKg
                            val avgVal = selectedWeightKg * material.basePricePerKg
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(KabadiwalaColors.SurfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = localizedUi("estimated_payout_kg", selectedWeightKg.toInt()),
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                                Text(
                                    text = if (material.minPricePerKg != material.maxPricePerKg)
                                        "₹${"%.0f".format(minVal)} - ₹${"%.0f".format(maxVal)} (Avg: ₹${"%.0f".format(avgVal)})"
                                    else
                                        "₹${"%.0f".format(avgVal)}",
                                    style = KabadiwalaTypography.TitleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.Primary
                                )
                            }
                        }

                        // Alternative Matches
                        if (result.alternativeMatches.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = localizedUi("or_is_it_one_of_these"),
                                    style = KabadiwalaTypography.LabelSmall,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    result.alternativeMatches.forEach { alt ->
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(1.dp, KabadiwalaColors.OutlineVariant, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    scanResult = MaterialRecognitionService.getSampleScrap(alt.id)
                                                }
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            MaterialImage(
                                                material = alt,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = alt.localizedName,
                                                    style = KabadiwalaTypography.LabelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = alt.rateRange,
                                                    style = KabadiwalaTypography.BodySmall,
                                                    color = KabadiwalaColors.Primary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Call to Actions
                        Button(
                            onClick = {
                                PendingPickupStore.setPending(
                                    materialId = material.id,
                                    materialName = material.name,
                                    weightKg = selectedWeightKg,
                                    estimatedPrice = selectedWeightKg * material.basePricePerKg,
                                    ratePerKg = material.basePricePerKg
                                )
                                onNavigate(Screen.MaterialEntryWithId(material.id))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KabadiwalaColors.Primary,
                                contentColor = KabadiwalaColors.OnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = localizedUi("sell_material_cta", material.localizedName, selectedWeightKg.toInt(), "%.0f".format(material.basePricePerKg)),
                                style = KabadiwalaTypography.LabelLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                PendingPickupStore.setPending(
                                    materialId = material.id,
                                    materialName = material.name,
                                    weightKg = selectedWeightKg,
                                    estimatedPrice = selectedWeightKg * material.basePricePerKg,
                                    ratePerKg = material.basePricePerKg
                                )
                                onNavigate(Screen.RecyclerDiscovery)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = KabadiwalaColors.Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = localizedUi("find_buyers_near_me", selectedWeightKg.toInt()),
                                style = KabadiwalaTypography.LabelLarge,
                                color = KabadiwalaColors.Primary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Safely decodes a bitmap from Uri with automatic downsampling to avoid OOM,
 * and corrects orientation based on EXIF metadata.
 */
private fun decodeAndCorrectExif(context: Context, uri: Uri): Bitmap? {
    return try {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, boundsOptions)
        }

        var sampleSize = 1
        val maxTargetDim = 1024
        while (boundsOptions.outWidth / sampleSize > maxTargetDim || boundsOptions.outHeight / sampleSize > maxTargetDim) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val rawBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        } ?: return null

        var rotationDegrees = 0
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                rotationDegrees = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            }
        } catch (ignored: Exception) {}

        if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
        } else {
            rawBitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
