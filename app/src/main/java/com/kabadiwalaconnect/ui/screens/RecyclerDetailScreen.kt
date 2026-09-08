package com.kabadiwalaconnect.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kabadiwalaconnect.language.localizedUi
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.ScrapCartItem
import com.kabadiwalaconnect.data.model.Recycler
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.model.TransactionItem
import com.kabadiwalaconnect.data.model.TransactionStatus
import com.kabadiwalaconnect.data.model.PaymentMethod
import com.kabadiwalaconnect.data.model.PendingPickupStore
import com.kabadiwalaconnect.data.repository.Result
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.KabadiwalaApplication
import com.kabadiwalaconnect.ui.components.MaterialImage
import com.kabadiwalaconnect.ui.components.ScrapSaleSuccessDialog
import com.kabadiwalaconnect.ui.components.getMaterialIconRes
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.random.Random

import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.LocalLanguageViewModel
import com.kabadiwalaconnect.language.localizedName
import com.kabadiwalaconnect.language.localizedFacilityType
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton
import com.kabadiwalaconnect.language.EntityTranslations
import com.kabadiwalaconnect.language.LocalCurrentLanguage

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RecyclerDetailScreen(
    recyclerId: String,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit = { onNavigate(Screen.RecyclerDiscovery) },
    languageViewModel: LanguageViewModel? = LocalLanguageViewModel.current
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentLangCode = LocalCurrentLanguage.current.code
    var recycler by remember { mutableStateOf<Recycler?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var showQuickSellSheet by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var completedTransaction by remember { mutableStateOf<Transaction?>(null) }

    LaunchedEffect(recyclerId) {
        val repo = KabadiwalaApplication.getInstance().repository
        when (val res = repo.getRecycler(recyclerId)) {
            is Result.Success -> {
                recycler = res.data
                isLoading = false
            }
            is Result.Error -> {
                isLoading = false
            }
            is Result.Loading -> {
                isLoading = true
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = KabadiwalaColors.Primary)
        }
        return
    }

    val item = recycler
    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Recycler not found", style = KabadiwalaTypography.TitleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KabadiwalaColors.Background)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = KabadiwalaColors.Primary
                    )
                }
                Text(
                    text = localizedUi("recycler_profile"),
                    style = KabadiwalaTypography.TitleLarge,
                    fontWeight = FontWeight.Bold,
                    color = KabadiwalaColors.OnBackground
                )
            }
            LanguageTopBarButton()
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Large,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = item.localizedName,
                                    style = KabadiwalaTypography.HeadlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (item.isVerified) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = "Verified",
                                        tint = KabadiwalaColors.Success,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            if (item.contactPerson.isNotBlank()) {
                                Text(
                                    text = "👤 ${item.contactPerson}",
                                    style = KabadiwalaTypography.BodyMedium,
                                    color = KabadiwalaColors.Primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = item.localizedFacilityType,
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }

                        // Rating Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(KabadiwalaColors.SecondaryContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = KabadiwalaColors.Secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${item.formattedRating} (${item.reviewCount})",
                                    style = KabadiwalaTypography.LabelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.OnSecondaryContainer
                                )
                            }
                        }
                    }

                    // Open / Closed & Distance Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.isOpenNow) KabadiwalaColors.PrimaryContainer else KabadiwalaColors.SurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = if (item.isOpenNow) KabadiwalaColors.Primary else KabadiwalaColors.OnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (item.isOpenNow) "${localizedUi("open_now")} • ${item.workingHours}" else "${localizedUi("closed")} • ${item.workingHours}",
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.isOpenNow) KabadiwalaColors.Primary else KabadiwalaColors.OnSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "📍 ${item.formattedDistance} away",
                            style = KabadiwalaTypography.BodySmall,
                            fontWeight = FontWeight.Medium,
                            color = KabadiwalaColors.OnSurfaceVariant
                        )
                    }

                    // Contact Action Bar (Call, WhatsApp, Directions)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { callRecycler(context, item.phone) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KabadiwalaColors.Primary,
                                contentColor = KabadiwalaColors.OnPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localizedUi("call"), style = KabadiwalaTypography.LabelMedium)
                        }

                        Button(
                            onClick = {
                                val wp = if (item.whatsappNumber.isNotBlank()) item.whatsappNumber else item.phone
                                openWhatsApp(context, wp, item.name)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localizedUi("whatsapp"), style = KabadiwalaTypography.LabelMedium)
                        }

                        OutlinedButton(
                            onClick = { openDirections(context, item.latitude, item.longitude, item.name) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Navigation, contentDescription = "Map", tint = KabadiwalaColors.Primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localizedUi("route"), style = KabadiwalaTypography.LabelMedium, color = KabadiwalaColors.Primary)
                        }
                    }
                }
            }

            // Location & Address Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Medium,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = localizedUi("yard_mandi_location"),
                        style = KabadiwalaTypography.TitleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = KabadiwalaColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.address, style = KabadiwalaTypography.BodyMedium)
                            Text(
                                text = "Coordinates: ${"%.4f".format(item.latitude)}, ${"%.4f".format(item.longitude)}",
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Buying Rates & Accepted Materials Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Medium,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = localizedUi("materials_accepted_rates"),
                        style = KabadiwalaTypography.TitleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = localizedUi("rates_benchmarked_info"),
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant
                    )

                    item.acceptedMaterials.forEach { matId ->
                        val mat = Material.getById(matId)
                        if (mat != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, KabadiwalaColors.OutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    MaterialImage(
                                        material = mat,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Column {
                                        Text(text = mat.localizedName, style = KabadiwalaTypography.BodyMedium, fontWeight = FontWeight.SemiBold)
                                        Text(text = mat.category.localizedName, style = KabadiwalaTypography.LabelSmall, color = KabadiwalaColors.OnSurfaceVariant)
                                    }
                                }
                                Text(
                                    text = mat.rateRange,
                                    style = KabadiwalaTypography.TitleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.Primary
                                )
                            }
                        }
                    }
                }
            }

            // Facilities & Services
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Medium,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = localizedUi("yard_amenities"),
                        style = KabadiwalaTypography.TitleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    AmenityRow(icon = Icons.Filled.Scale, title = localizedUi("weighing_scale"), desc = "Digital weighbridge & precision tare scale certified")
                    AmenityRow(icon = Icons.Filled.Payments, title = localizedUi("payment_modes"), desc = item.paymentModes.joinToString(" • "))
                    AmenityRow(icon = Icons.Filled.CheckCircle, title = localizedUi("pickup_service"), desc = "Doorstep pickup available for lots over ${item.minPickupKg.toInt()} kg")
                }
            }
        }

        // Bottom Fixed CTA
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = KabadiwalaColors.Surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val pending = PendingPickupStore.pendingPickup
                val pendingCart = PendingPickupStore.pendingCartItems
                val btnText = if (pendingCart.size > 1 && pending != null) {
                    localizedUi("sell_items_to_recycler", pendingCart.size, pending.estimatedPrice)
                } else if (pending != null) {
                    localizedUi("sell_scrap_to_recycler", pending.estimatedPrice)
                } else {
                    localizedUi("sell_scrap_to_recycler_simple")
                }
                Button(
                    onClick = {
                        showQuickSellSheet = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = KabadiwalaColors.OnPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(btnText, style = KabadiwalaTypography.LabelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Quick Sell Modal Sheet directly on Recycler's Page — MULTI-ITEM CART
        if (showQuickSellSheet) {
            val acceptedList = remember(item.acceptedMaterials) {
                item.acceptedMaterials.mapNotNull { Material.getById(it) }.ifEmpty { Material.ALL_MATERIALS.take(5) }
            }
            val pending = PendingPickupStore.pendingPickup

            // Cart state
            val cartItems = remember {
                mutableStateListOf<ScrapCartItem>().apply {
                    addAll(PendingPickupStore.pendingCartItems)
                }
            }

            // Input state for adding new items
            var selectedMatId by remember { mutableStateOf(pending?.materialId ?: acceptedList.firstOrNull()?.id ?: "") }
            var weightInput by remember { mutableStateOf(pending?.weightKg?.let { if (it % 1.0 == 0.0) "%.0f".format(it) else "%.2f".format(it) } ?: "15") }
            val currentMat = acceptedList.find { it.id == selectedMatId } ?: acceptedList.firstOrNull() ?: Material.ALL_MATERIALS.first()
            val parsedWeight = weightInput.toDoubleOrNull() ?: 0.0
            val singleEstPayout = parsedWeight * currentMat.basePricePerKg

            // Cart totals
            val cartTotalWeight = cartItems.sumOf { it.weightKg }
            val cartTotalAmount = cartItems.sumOf { it.estimatedAmount }

            Dialog(
                onDismissRequest = { showQuickSellSheet = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.94f)
                        .clip(RoundedCornerShape(20.dp)),
                    color = KabadiwalaColors.Surface,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sell Scrap to ${item.localizedName.split(" ").firstOrNull() ?: "Dealer"}",
                                    style = KabadiwalaTypography.TitleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = localizedUi("add_multiple_items_sub"),
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                            }
                            IconButton(onClick = { showQuickSellSheet = false }) {
                                Icon(Icons.Filled.Close, contentDescription = "Close", tint = KabadiwalaColors.OnSurfaceVariant)
                            }
                        }

                        // ① ADD ITEM SECTION
                        Text(
                            text = "① ${localizedUi("select_good")}",
                            style = KabadiwalaTypography.LabelLarge,
                            fontWeight = FontWeight.Bold,
                            color = KabadiwalaColors.Primary
                        )

                        androidx.compose.foundation.layout.FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            acceptedList.forEach { mat ->
                                val isSelected = mat.id == selectedMatId
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedMatId = mat.id },
                                    leadingIcon = {
                                        MaterialImage(
                                            material = mat,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "${mat.localizedName} (${mat.rateRange})",
                                            style = KabadiwalaTypography.LabelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = KabadiwalaColors.PrimaryContainer,
                                        selectedLabelColor = KabadiwalaColors.Primary
                                    )
                                )
                            }
                        }

                        // Weight Input
                        Text(
                            text = "② ${localizedUi("enter_weight_kg")}",
                            style = KabadiwalaTypography.LabelLarge,
                            fontWeight = FontWeight.Bold,
                            color = KabadiwalaColors.Primary
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g. 15") },
                            trailingIcon = { Text("kg", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Quick weight chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5.0, 10.0, 25.0, 50.0).forEach { wt ->
                                OutlinedButton(
                                    onClick = { weightInput = "%.0f".format(wt) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                                ) {
                                    Text("%.0f kg".format(wt), style = KabadiwalaTypography.LabelSmall)
                                }
                            }
                        }

                        // Preview of item to add
                        if (parsedWeight > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currentMat.localizedName,
                                            style = KabadiwalaTypography.BodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "$parsedWeight kg × ₹${"%.0f".format(currentMat.basePricePerKg)}/kg",
                                            style = KabadiwalaTypography.BodySmall,
                                            color = KabadiwalaColors.OnSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "₹${"%.2f".format(singleEstPayout)}",
                                        style = KabadiwalaTypography.TitleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = KabadiwalaColors.Primary
                                    )
                                }
                            }
                        }

                        // ADD TO CART BUTTON
                        Button(
                            onClick = {
                                if (parsedWeight > 0) {
                                    cartItems.add(ScrapCartItem(material = currentMat, weightKg = parsedWeight))
                                    weightInput = "15"
                                    // Move selection to next uncarted material
                                    val cartedIds = cartItems.map { it.material.id }.toSet()
                                    val nextMat = acceptedList.firstOrNull { it.id !in cartedIds }
                                    if (nextMat != null) selectedMatId = nextMat.id
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = parsedWeight > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KabadiwalaColors.Secondary,
                                contentColor = KabadiwalaColors.OnSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(localizedUi("add_to_cart"), style = KabadiwalaTypography.LabelLarge, fontWeight = FontWeight.Bold)
                        }

                        // ② CART SUMMARY SECTION
                        if (cartItems.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = KabadiwalaColors.Primary, modifier = Modifier.size(20.dp))
                                Text(
                                    text = localizedUi("your_cart"),
                                    style = KabadiwalaTypography.TitleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(KabadiwalaColors.Primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${cartItems.size}",
                                        style = KabadiwalaTypography.LabelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Cart items list
                            cartItems.forEachIndexed { index, cartItem ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, KabadiwalaColors.OutlineVariant),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Item icon
                                        MaterialImage(
                                            material = cartItem.material,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )

                                        // Item details
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = cartItem.material.localizedName,
                                                style = KabadiwalaTypography.BodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${cartItem.formattedWeight} × ₹${"%.0f".format(cartItem.material.basePricePerKg)}/kg",
                                                style = KabadiwalaTypography.BodySmall,
                                                color = KabadiwalaColors.OnSurfaceVariant
                                            )
                                        }

                                        // Amount
                                        Text(
                                            text = cartItem.formattedAmount,
                                            style = KabadiwalaTypography.BodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20)
                                        )

                                        // Remove button
                                        IconButton(
                                            onClick = { cartItems.removeAt(index) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Remove",
                                                tint = KabadiwalaColors.Error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // ③ TOTAL & CONFIRM SECTION
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(localizedUi("total_payout"), style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant)
                                            Text(
                                                text = "${cartItems.size} item${if (cartItems.size > 1) "s" else ""} • ${"%.1f".format(cartTotalWeight)} kg total",
                                                style = KabadiwalaTypography.BodySmall,
                                                color = Color(0xFF2E7D32)
                                            )
                                        }
                                        Text(
                                            text = "₹${"%.2f".format(cartTotalAmount)}",
                                            style = KabadiwalaTypography.TitleLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF1B5E20)
                                        )
                                    }
                                    Text(
                                        text = localizedUi("free_pickup_weighbridge"),
                                        style = KabadiwalaTypography.BodySmall,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }

                            // Confirm CTA Button
                            Button(
                                onClick = {
                                    if (cartItems.isNotEmpty()) {
                                        coroutineScope.launch {
                                            val orderId = "KC-REC-${Random.nextInt(1000, 9999)}"
                                            val randomOtp = "%04d".format(Random.nextInt(1000, 9999))
                                            val txnItems = cartItems.map { cartItem ->
                                                TransactionItem(
                                                    materialId = cartItem.material.id,
                                                    materialName = EntityTranslations.getLocalizedMaterialName(cartItem.material.id, currentLangCode),
                                                    weightKg = cartItem.weightKg,
                                                    pricePerKg = cartItem.material.basePricePerKg,
                                                    amount = cartItem.estimatedAmount
                                                )
                                            }
                                            val txn = Transaction(
                                                id = orderId.lowercase(),
                                                transactionDate = Instant.now(),
                                                recyclerId = item.id,
                                                recyclerName = EntityTranslations.getLocalizedRecyclerName(item.id, currentLangCode),
                                                items = txnItems,
                                                totalWeight = cartTotalWeight,
                                                totalAmount = cartTotalAmount,
                                                status = TransactionStatus.CONFIRMED,
                                                otp = randomOtp,
                                                receiptNumber = orderId,
                                                handoverConfirmed = false,
                                                paymentMethod = PaymentMethod.CASH,
                                                notes = "Direct scrap sale (${cartItems.size} items) with ${item.name}"
                                            )
                                            KabadiwalaApplication.getInstance().repository.createTransaction(txn)
                                            PendingPickupStore.clear()
                                            completedTransaction = txn
                                            showQuickSellSheet = false
                                            showSuccessDialog = true
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = KabadiwalaColors.Primary, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = localizedUi("confirm_sale_schedule_pickup"),
                                    style = KabadiwalaTypography.LabelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Direct Sale Success Popup Message to Customer
        if (showSuccessDialog && completedTransaction != null) {
            ScrapSaleSuccessDialog(
                transaction = completedTransaction!!,
                recyclerPhone = item.phone,
                onDismiss = { showSuccessDialog = false },
                onViewReceipt = { txnId ->
                    showSuccessDialog = false
                    onNavigate(Screen.TransactionDetail(txnId))
                },
                onGoHome = {
                    showSuccessDialog = false
                    onNavigate(Screen.Home)
                }
            )
        }
    }
}

@Composable
private fun AmenityRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(KabadiwalaColors.PrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = KabadiwalaColors.Primary, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(text = title, style = KabadiwalaTypography.BodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = desc, style = KabadiwalaTypography.BodySmall, color = KabadiwalaColors.OnSurfaceVariant)
        }
    }
}

fun callRecycler(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open phone dialer: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

fun openWhatsApp(context: Context, phone: String, recyclerName: String) {
    try {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        val text = Uri.encode("Hello $recyclerName, I am a scrap collector on Kabadiwala Connect. I want to inquire about today's scrap rates.")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$text"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp not available: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

fun openDirections(context: Context, lat: Double, lng: Double, name: String) {
    try {
        val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(name)})")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not launch map navigation: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}
