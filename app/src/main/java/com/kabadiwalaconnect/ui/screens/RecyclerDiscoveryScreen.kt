package com.kabadiwalaconnect.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import com.kabadiwalaconnect.KabadiwalaApplication
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.PaymentMethod
import com.kabadiwalaconnect.data.model.PendingPickupStore
import com.kabadiwalaconnect.data.model.ScrapCartItem
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.model.TransactionItem
import com.kabadiwalaconnect.data.model.TransactionStatus
import com.kabadiwalaconnect.ui.components.MaterialImage
import com.kabadiwalaconnect.ui.components.ScrapSaleSuccessDialog
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.random.Random
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kabadiwalaconnect.data.model.Recycler
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.RecyclerCard
import com.kabadiwalaconnect.ui.components.getMaterialIconRes
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import com.kabadiwalaconnect.viewmodel.RecyclerDiscoveryViewModel
import com.kabadiwalaconnect.viewmodel.viewModelFactory
import kotlin.math.roundToInt

import androidx.compose.runtime.collectAsState
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.language.localizedName
import com.kabadiwalaconnect.language.localizedMaterialName
import com.kabadiwalaconnect.language.localizedUi
import com.kabadiwalaconnect.language.EntityTranslations
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton

enum class RecyclerDiscoveryTab {
    LIST,
    MAP
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RecyclerDiscoveryScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: RecyclerDiscoveryViewModel = viewModel(factory = viewModelFactory()),
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val strings = currentStrings()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    val recyclers by viewModel.recyclers.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(RecyclerDiscoveryTab.LIST) }
    var searchText by remember { mutableStateOf(searchQuery) }
    var showFilters by remember { mutableStateOf(false) }
    var selectedRecyclerOnMap by remember { mutableStateOf<Recycler?>(null) }
    var maxDistanceKm by remember { mutableDoubleStateOf(10.0) }

    val coroutineScope = rememberCoroutineScope()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var completedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var selectedRecyclerForDialog by remember { mutableStateOf<Recycler?>(null) }
    var quickSellRecycler by remember { mutableStateOf<Recycler?>(null) }

    fun bookPickupWithRecycler(recycler: Recycler, cartItems: List<ScrapCartItem>? = null, customMat: Material? = null, customWeight: Double? = null) {
        val effectiveCart = cartItems ?: PendingPickupStore.pendingCartItems.ifEmpty { null }
        if (effectiveCart != null && effectiveCart.isNotEmpty()) {
            // Multi-item cart flow
            val orderId = "KC-REC-${Random.nextInt(1000, 9999)}"
            val randomOtp = "%04d".format(Random.nextInt(1000, 9999))
            val txnItems = effectiveCart.map { cartItem ->
                TransactionItem(
                    materialId = cartItem.material.id,
                    materialName = EntityTranslations.getLocalizedMaterialName(cartItem.material.id, currentLanguage.code),
                    weightKg = cartItem.weightKg,
                    pricePerKg = cartItem.material.basePricePerKg,
                    amount = cartItem.estimatedAmount
                )
            }
            val totalWeight = effectiveCart.sumOf { it.weightKg }
            val totalAmount = effectiveCart.sumOf { it.estimatedAmount }

            val newTxn = Transaction(
                id = orderId.lowercase(),
                transactionDate = Instant.now(),
                recyclerId = recycler.id,
                recyclerName = EntityTranslations.getLocalizedRecyclerName(recycler.id, currentLanguage.code),
                items = txnItems,
                totalWeight = totalWeight,
                totalAmount = totalAmount,
                status = TransactionStatus.CONFIRMED,
                otp = randomOtp,
                receiptNumber = orderId,
                handoverConfirmed = false,
                paymentMethod = PaymentMethod.CASH,
                notes = "Pickup scheduled (${effectiveCart.size} items) through Kabadiwala Connect"
            )

            coroutineScope.launch {
                val appRepo = KabadiwalaApplication.getInstance().repository
                appRepo.createTransaction(newTxn)
                PendingPickupStore.clear()
                completedTransaction = newTxn
                selectedRecyclerForDialog = recycler
                showSuccessDialog = true
            }
        } else {
            // Single-item legacy flow (from pending pickup or direct)
            val pending = PendingPickupStore.pendingPickup
            val mat = customMat ?: pending?.materialId?.let { Material.getById(it) } ?: recycler.acceptedMaterials.firstOrNull()?.let { Material.getById(it) } ?: Material.ALL_MATERIALS.first()
            val weight = customWeight ?: pending?.weightKg ?: recycler.minPickupKg.coerceAtLeast(10.0)
            val rate = mat.basePricePerKg
            val totalAmt = if (customWeight != null) weight * rate else (pending?.estimatedPrice ?: (weight * rate))
            val orderId = "KC-REC-${Random.nextInt(1000, 9999)}"
            val randomOtp = "%04d".format(Random.nextInt(1000, 9999))

            val newTxn = Transaction(
                id = orderId.lowercase(),
                transactionDate = Instant.now(),
                recyclerId = recycler.id,
                recyclerName = EntityTranslations.getLocalizedRecyclerName(recycler.id, currentLanguage.code),
                items = listOf(
                    TransactionItem(
                        materialId = mat.id,
                        materialName = EntityTranslations.getLocalizedMaterialName(mat.id, currentLanguage.code),
                        weightKg = weight,
                        pricePerKg = rate,
                        amount = totalAmt
                    )
                ),
                totalWeight = weight,
                totalAmount = totalAmt,
                status = TransactionStatus.CONFIRMED,
                otp = randomOtp,
                receiptNumber = orderId,
                handoverConfirmed = false,
                paymentMethod = PaymentMethod.CASH,
                notes = "Pickup scheduled through Kabadiwala Connect"
            )

            coroutineScope.launch {
                val appRepo = KabadiwalaApplication.getInstance().repository
                appRepo.createTransaction(newTxn)
                PendingPickupStore.clear()
                completedTransaction = newTxn
                selectedRecyclerForDialog = recycler
                showSuccessDialog = true
            }
        }
    }

    // Filter recyclers locally for fast responsive feedback
    val filteredRecyclers = remember(recyclers, selectedFilter, maxDistanceKm, searchText) {
        recyclers.filter { r ->
            (maxDistanceKm >= 10.0 || r.distanceKm <= maxDistanceKm) &&
            (selectedFilter == null || r.acceptedMaterials.any { it.contains(selectedFilter!!, ignoreCase = true) }) &&
            (searchText.isBlank() || r.name.contains(searchText, ignoreCase = true) ||
                    r.address.contains(searchText, ignoreCase = true) ||
                    r.contactPerson.contains(searchText, ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KabadiwalaColors.Background)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.findRecycler,
                        style = KabadiwalaTypography.HeadlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = KabadiwalaColors.OnBackground
                    )
                    Text(
                        text = localizedUi("live_dealers_sub"),
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LanguageTopBarButton(
                        currentLanguage = currentLanguage,
                        onClick = { languageViewModel.openLanguageDialog() }
                    )

                    // Quick Mode Switcher
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = KabadiwalaColors.PrimaryContainer,
                        modifier = Modifier.clickable {
                            selectedTab = if (selectedTab == RecyclerDiscoveryTab.LIST) RecyclerDiscoveryTab.MAP else RecyclerDiscoveryTab.LIST
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedTab == RecyclerDiscoveryTab.LIST) Icons.Filled.Map else Icons.Filled.ViewList,
                                contentDescription = null,
                                tint = KabadiwalaColors.Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (selectedTab == RecyclerDiscoveryTab.LIST) localizedUi("map") else localizedUi("list"),
                                style = KabadiwalaTypography.LabelSmall,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.Primary
                            )
                        }
                    }
                }
            }

            // Search Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { newValue ->
                        searchText = newValue
                        viewModel.updateSearchQuery(newValue)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeholder = {
                        Text(
                            text = localizedUi("search_dealers_hint"),
                            style = KabadiwalaTypography.BodyMedium,
                            color = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = KabadiwalaColors.OnSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchText.isNotBlank()) {
                            IconButton(onClick = {
                                searchText = ""
                                viewModel.updateSearchQuery("")
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = KabadiwalaColors.OnSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search)
                )

                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Filters",
                        tint = if (showFilters || selectedFilter != null) KabadiwalaColors.Primary else KabadiwalaColors.OnSurfaceVariant
                    )
                }
            }

            // Material Filter Chips
            if (showFilters) {
                MaterialFilterChips(
                    selectedFilter = selectedFilter,
                    onFilterChange = { materialId -> viewModel.filterByMaterial(materialId) }
                )
            }

            // Active Pickup Request Banner (when arriving from MaterialEntry "Send for Pickup")
            val activePickup = PendingPickupStore.pendingPickup
            if (activePickup != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF81C784))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = localizedUi("ready_for_pickup"),
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1B5E20)
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFC8E6C9)
                                ) {
                                    Text(
                                        text = "Est. ₹%.0f".format(activePickup.estimatedPrice),
                                        style = KabadiwalaTypography.LabelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            val pendingCart = PendingPickupStore.pendingCartItems
                            val pickupTitle = if (pendingCart.size > 1) {
                                "${pendingCart.size} scrap items • ${"%.1f".format(activePickup.weightKg)} kg total"
                            } else {
                                "${"%.1f".format(activePickup.weightKg)} kg • ${EntityTranslations.getLocalizedMaterialName(activePickup.materialId, currentLanguage.code)}"
                            }
                            Text(
                                text = pickupTitle,
                                style = KabadiwalaTypography.BodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.OnSurface
                            )
                            Text(
                                text = localizedUi("ready_for_pickup_sub"),
                                style = KabadiwalaTypography.BodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }

                        IconButton(
                            onClick = { PendingPickupStore.clear() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cancel Pickup",
                                tint = Color(0xFF757575)
                            )
                        }
                    }
                }
            }
        }

        // Segmented Tab Row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.Primary
        ) {
            Tab(
                selected = selectedTab == RecyclerDiscoveryTab.LIST,
                onClick = { selectedTab = RecyclerDiscoveryTab.LIST },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.ViewList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("List View (${filteredRecyclers.size})", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            Tab(
                selected = selectedTab == RecyclerDiscoveryTab.MAP,
                onClick = { selectedTab = RecyclerDiscoveryTab.MAP },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Interactive Map", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }

        if (isLoading && recyclers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = KabadiwalaColors.Primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Finding scrap dealers near you...", style = KabadiwalaTypography.BodyMedium)
                }
            }
        } else if (selectedTab == RecyclerDiscoveryTab.MAP) {
            // Full Interactive Map View
            InteractiveDummyMapView(
                recyclers = filteredRecyclers,
                selectedRecycler = selectedRecyclerOnMap ?: filteredRecyclers.firstOrNull(),
                onSelectRecycler = { selectedRecyclerOnMap = it },
                onNavigate = onNavigate,
                maxDistanceKm = maxDistanceKm,
                onDistanceChange = { maxDistanceKm = it },
                onBookPickup = { r ->
                    if (PendingPickupStore.hasPending()) {
                        bookPickupWithRecycler(r)
                    } else {
                        quickSellRecycler = r
                    }
                }
            )
        } else {
            // List View with Mini Map Preview Banner
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
            ) {
                // Mini Map Teaser Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(KabadiwalaShapes.Medium)
                            .clickable { selectedTab = RecyclerDiscoveryTab.MAP },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Mini Canvas Map Background
                            MiniMapBackground()

                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = KabadiwalaColors.Primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = localizedUi("view_on_live_map"),
                                            style = KabadiwalaTypography.TitleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = KabadiwalaColors.Primary
                                        )
                                    }
                                    Text(
                                        text = "${filteredRecyclers.size} verified scrap merchants pinned near you",
                                        style = KabadiwalaTypography.BodySmall,
                                        color = KabadiwalaColors.OnSurfaceVariant
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = KabadiwalaColors.Primary,
                                    shadowElevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = localizedUi("open_map"),
                                            style = KabadiwalaTypography.LabelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Distance Quick Chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2.0 to "Within 2 km", 5.0 to "Within 5 km", 10.0 to "All Nearby (10 km)").forEach { (dist, label) ->
                            FilterChip(
                                selected = maxDistanceKm == dist,
                                onClick = { maxDistanceKm = dist },
                                label = { Text(label, style = KabadiwalaTypography.LabelSmall) }
                            )
                        }
                    }
                }

                // Results list
                if (filteredRecyclers.isNotEmpty()) {
                    items(filteredRecyclers) { recycler ->
                        RecyclerCard(
                            recycler = recycler,
                            onClick = { onNavigate(Screen.RecyclerDetail(recycler.id)) },
                            onBookPickup = { r ->
                                if (PendingPickupStore.hasPending()) {
                                    bookPickupWithRecycler(r)
                                } else {
                                    quickSellRecycler = r
                                }
                            }
                        )
                    }
                } else if (!isSearching) {
                    item {
                        EmptyState(
                            title = "No scrap recyclers found",
                            subtitle = if (searchText.isNotBlank()) "Try searching another scrap type or area" else "Try expanding the search radius",
                            icon = Icons.Filled.LocationOn,
                            onAction = {
                                searchText = ""
                                maxDistanceKm = 10.0
                                viewModel.filterByMaterial(null)
                                viewModel.refresh()
                            },
                            actionText = localizedUi("reset_filters")
                        )
                    }
                }
            }
        }
    }

    // Quick Sell Modal Dialog for selling directly from Recycler Card — MULTI-ITEM CART
    if (quickSellRecycler != null) {
        val targetRecycler = quickSellRecycler!!
        val acceptedList = remember(targetRecycler.acceptedMaterials) {
            targetRecycler.acceptedMaterials.mapNotNull { Material.getById(it) }.ifEmpty { Material.ALL_MATERIALS.take(6) }
        }

        // Cart state
        val cartItems = remember {
            mutableStateListOf<ScrapCartItem>().apply {
                addAll(PendingPickupStore.pendingCartItems)
            }
        }

        // Input state for adding new items
        var selectedMatId by remember { mutableStateOf(acceptedList.firstOrNull()?.id ?: "") }
        var weightInput by remember { mutableStateOf("15") }
        val currentMat = acceptedList.find { it.id == selectedMatId } ?: acceptedList.firstOrNull() ?: Material.ALL_MATERIALS.first()
        val parsedWeight = weightInput.toDoubleOrNull() ?: 0.0
        val singleEstPayout = parsedWeight * currentMat.basePricePerKg

        // Cart totals
        val cartTotalWeight = cartItems.sumOf { it.weightKg }
        val cartTotalAmount = cartItems.sumOf { it.estimatedAmount }

        Dialog(
            onDismissRequest = { quickSellRecycler = null },
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
                                text = "Sell Scrap to ${targetRecycler.localizedName.split(" ").firstOrNull() ?: "Dealer"}",
                                style = KabadiwalaTypography.TitleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = localizedUi("add_multiple_items_sub"),
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }
                        IconButton(onClick = { quickSellRecycler = null }) {
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                                contentPadding = PaddingValues(vertical = 4.dp)
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
                                            overflow = TextOverflow.Ellipsis
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
                                    text = localizedUi("free_doorstep_pickup"),
                                    style = KabadiwalaTypography.BodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        // Confirm CTA Button
                        Button(
                            onClick = {
                                if (cartItems.isNotEmpty()) {
                                    val target = targetRecycler
                                    val items = cartItems.toList()
                                    quickSellRecycler = null
                                    bookPickupWithRecycler(target, cartItems = items)
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

    // Success Popup Dialog upon booking sale/pickup
    if (showSuccessDialog && completedTransaction != null) {
        ScrapSaleSuccessDialog(
            transaction = completedTransaction!!,
            recyclerPhone = selectedRecyclerForDialog?.phone,
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

/**
 * Interactive Vector Dummy Map showing arterial highways, green parks, water body,
 * pulsing user location pin, and tap-selectable recycler pins with a sliding bottom sheet.
 */
@Composable
fun InteractiveDummyMapView(
    recyclers: List<Recycler>,
    selectedRecycler: Recycler?,
    onSelectRecycler: (Recycler) -> Unit,
    onNavigate: (Screen) -> Unit,
    maxDistanceKm: Double,
    onDistanceChange: (Double) -> Unit,
    onBookPickup: ((Recycler) -> Unit)? = null
) {
    val context = LocalContext.current
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }

    // Pulsing radar animation for user location
    val infiniteTransition = rememberInfiniteTransition(label = "RadarPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F1EC))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    panX += dragAmount.x
                    panY += dragAmount.y
                }
            }
    ) {
        // 1. Vector Map Canvas (Roads, Green belts, Water, Districts)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f + panX
            val cy = h / 2f + panY

            // Water Body / Lake
            drawCircle(
                color = Color(0xFFD6E6F2),
                radius = 90f * zoomLevel,
                center = Offset(cx + 120f * zoomLevel, cy - 100f * zoomLevel)
            )

            // Green Reserve / Park
            drawRoundRect(
                color = Color(0xFFE2EED8),
                topLeft = Offset(cx - 160f * zoomLevel, cy - 130f * zoomLevel),
                size = Size(100f * zoomLevel, 80f * zoomLevel),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Secondary Park
            drawRoundRect(
                color = Color(0xFFE2EED8),
                topLeft = Offset(cx - 80f * zoomLevel, cy + 120f * zoomLevel),
                size = Size(140f * zoomLevel, 70f * zoomLevel),
                cornerRadius = CornerRadius(14f, 14f)
            )

            // Secondary street grid lines
            val gridStep = 50f * zoomLevel
            for (i in -10..10) {
                // Vertical street
                drawLine(
                    color = Color(0xFFEAE6DC),
                    start = Offset(cx + i * gridStep, 0f),
                    end = Offset(cx + i * gridStep, h),
                    strokeWidth = 2f
                )
                // Horizontal street
                drawLine(
                    color = Color(0xFFEAE6DC),
                    start = Offset(0f, cy + i * gridStep),
                    end = Offset(w, cy + i * gridStep),
                    strokeWidth = 2f
                )
            }

            // Arterial Ring Road (Wide curve)
            val ringRoad = Path().apply {
                moveTo(0f, cy + 100f * zoomLevel)
                cubicTo(
                    cx - 50f * zoomLevel, cy + 50f * zoomLevel,
                    cx + 50f * zoomLevel, cy - 80f * zoomLevel,
                    w, cy - 60f * zoomLevel
                )
            }
            // Highway casing
            drawPath(ringRoad, color = Color(0xFFDCD5C5), style = Stroke(width = 14f * zoomLevel))
            // Highway body
            drawPath(ringRoad, color = Color(0xFFFFF7DB), style = Stroke(width = 10f * zoomLevel))

            // Main Mandi Link Road
            val linkRoad = Path().apply {
                moveTo(cx - 150f * zoomLevel, 0f)
                lineTo(cx + 80f * zoomLevel, h)
            }
            drawPath(linkRoad, color = Color(0xFFDCD5C5), style = Stroke(width = 12f * zoomLevel))
            drawPath(linkRoad, color = Color.White, style = Stroke(width = 8f * zoomLevel))

            // Pulse Radar around Collector's current location
            drawCircle(
                color = Color(0xFF2E7D32).copy(alpha = pulseAlpha),
                radius = pulseRadius * zoomLevel * 1.5f,
                center = Offset(cx, cy)
            )

            // Collector's current location dot
            drawCircle(
                color = Color.White,
                radius = 11f * zoomLevel,
                center = Offset(cx, cy)
            )
            drawCircle(
                color = Color(0xFF2E7D32),
                radius = 8f * zoomLevel,
                center = Offset(cx, cy)
            )
        }

        // 2. Interactive Map Pins for Recyclers
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val w = constraints.maxWidth.toFloat()
            val h = constraints.maxHeight.toFloat()
            val cx = w / 2f + panX
            val cy = h / 2f + panY

            // "You are here" label badge
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (cx - 40.dp.toPx()).roundToInt(),
                            (cy + 14.dp.toPx()).roundToInt()
                        )
                    }
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.9f))
                    .border(1.dp, KabadiwalaColors.Primary, RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "📍 You (Collector)",
                    style = KabadiwalaTypography.LabelSmall,
                    fontWeight = FontWeight.Bold,
                    color = KabadiwalaColors.Primary,
                    fontSize = 10.sp
                )
            }

            // Recycler Pins
            recyclers.forEach { recycler ->
                // Base center: 12.9716, 77.5946
                val dLng = (recycler.longitude - 77.5946).toFloat()
                val dLat = (recycler.latitude - 12.9716).toFloat()
                // Map scaling
                val pinX = cx + (dLng * 8500f * zoomLevel)
                val pinY = cy - (dLat * 8500f * zoomLevel)

                val isSelected = selectedRecycler?.id == recycler.id

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (pinX - 45.dp.toPx()).roundToInt(),
                                (pinY - 40.dp.toPx()).roundToInt()
                            )
                        }
                        .clickable { onSelectRecycler(recycler) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(90.dp)
                    ) {
                        // Floating pin pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) KabadiwalaColors.Primary else Color.White,
                            shadowElevation = if (isSelected) 8.dp else 4.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) Color.White else KabadiwalaColors.Primary
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else KabadiwalaColors.Primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = recycler.localizedName.split(" ").firstOrNull() ?: "Yard",
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else KabadiwalaColors.OnSurface,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Distance tag below pin
                        Text(
                            text = recycler.formattedDistance,
                            style = KabadiwalaTypography.LabelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            fontSize = 9.sp,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }

        // 3. Floating Map Controls (Zoom +, Zoom -, Recenter, Radius Filter)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(40.dp).clickable { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(2.5f) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Add, contentDescription = "Zoom In", tint = KabadiwalaColors.OnSurface)
                }
            }

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(40.dp).clickable { zoomLevel = (zoomLevel * 0.8f).coerceAtLeast(0.6f) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Remove, contentDescription = "Zoom Out", tint = KabadiwalaColors.OnSurface)
                }
            }

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(40.dp).clickable {
                    panX = 0f
                    panY = 0f
                    zoomLevel = 1.0f
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Recenter", tint = KabadiwalaColors.Primary)
                }
            }
        }

        // Floating Radius Filter Chips at top left
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(2.0 to "< 2 km", 5.0 to "< 5 km", 10.0 to "All 10 km").forEach { (dist, label) ->
                val isSel = maxDistanceKm == dist
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSel) KabadiwalaColors.Primary else Color.White,
                    shadowElevation = 3.dp,
                    modifier = Modifier.clickable { onDistanceChange(dist) }
                ) {
                    Text(
                        text = label,
                        style = KabadiwalaTypography.LabelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) Color.White else KabadiwalaColors.OnSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // 4. Selected Recycler Highlight Sheet (at bottom of map)
        AnimatedVisibility(
            visible = selectedRecycler != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            selectedRecycler?.let { recycler ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = KabadiwalaShapes.Large,
                    colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Title & Rating Header
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
                                        text = recycler.localizedName,
                                        style = KabadiwalaTypography.TitleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (recycler.isVerified) {
                                        Icon(
                                            imageVector = Icons.Filled.Verified,
                                            contentDescription = "Verified",
                                            tint = KabadiwalaColors.Success,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                if (recycler.contactPerson.isNotBlank()) {
                                    Text(
                                        text = "👤 ${recycler.contactPerson}",
                                        style = KabadiwalaTypography.LabelSmall,
                                        color = KabadiwalaColors.Primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "📍 ${recycler.address}",
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.OnSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = KabadiwalaColors.Secondary, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = recycler.formattedRating,
                                        style = KabadiwalaTypography.LabelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = KabadiwalaColors.Secondary
                                    )
                                }
                                Text(
                                    text = recycler.formattedDistance,
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                            }
                        }

                        // Open Now & Minimum pickup weight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (recycler.isOpenNow) KabadiwalaColors.PrimaryContainer else KabadiwalaColors.SurfaceVariant
                            ) {
                                Text(
                                    text = if (recycler.isOpenNow) "Open Now" else "Closed",
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (recycler.isOpenNow) KabadiwalaColors.Primary else KabadiwalaColors.OnSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${recycler.workingHours} • Min ${recycler.minPickupKg.toInt()}kg",
                                style = KabadiwalaTypography.LabelSmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }

                        // Action Buttons: Call, WhatsApp, Directions, View Rates
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { callRecycler(context, recycler.phone) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = KabadiwalaColors.Primary,
                                    contentColor = KabadiwalaColors.OnPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", style = KabadiwalaTypography.LabelSmall)
                            }

                            Button(
                                onClick = {
                                    val wp = if (recycler.whatsappNumber.isNotBlank()) recycler.whatsappNumber else recycler.phone
                                    openWhatsApp(context, wp, recycler.name)
                                },
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF25D366),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", style = KabadiwalaTypography.LabelSmall)
                            }

                            OutlinedButton(
                                onClick = { openDirections(context, recycler.latitude, recycler.longitude, recycler.name) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Navigation, contentDescription = "Route", tint = KabadiwalaColors.Primary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Route", style = KabadiwalaTypography.LabelSmall, color = KabadiwalaColors.Primary)
                            }

                            OutlinedButton(
                                onClick = { onNavigate(Screen.RecyclerDetail(recycler.id)) },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text("Rates & Info", style = KabadiwalaTypography.LabelSmall, color = KabadiwalaColors.Primary)
                            }
                        }

                        // Prominent Book Pickup / Sell Button on Map Bottom Sheet
                        if (onBookPickup != null) {
                            val pending = PendingPickupStore.pendingPickup
                            val mapBtnText = if (pending != null) {
                                "Sell Scrap to ${recycler.localizedName.split(" ").firstOrNull() ?: "Dealer"} • ₹%.0f".format(pending.estimatedPrice)
                            } else {
                                "Sell Scrap to ${recycler.localizedName.split(" ").firstOrNull() ?: "Dealer"}"
                            }
                            Button(
                                onClick = { onBookPickup(recycler) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = KabadiwalaColors.Primary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mapBtnText,
                                    style = KabadiwalaTypography.LabelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Visual mini canvas map background for the List View banner
 */
@Composable
private fun MiniMapBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Mini road grid
        drawLine(Color(0xFFDCD5C5), Offset(0f, h * 0.4f), Offset(w, h * 0.35f), strokeWidth = 8f)
        drawLine(Color(0xFFFFFFFF), Offset(0f, h * 0.4f), Offset(w, h * 0.35f), strokeWidth = 5f)

        drawLine(Color(0xFFDCD5C5), Offset(w * 0.3f, 0f), Offset(w * 0.35f, h), strokeWidth = 6f)
        drawLine(Color(0xFFFFFFFF), Offset(w * 0.3f, 0f), Offset(w * 0.35f, h), strokeWidth = 3f)

        // Mini green patch
        drawRoundRect(Color(0xFFDCEDC8), Offset(w * 0.6f, h * 0.1f), Size(w * 0.3f, h * 0.5f), CornerRadius(8f, 8f))

        // Mini lake
        drawCircle(Color(0xFFBBDEFB), radius = 25f, center = Offset(w * 0.15f, h * 0.7f))

        // Pins
        drawCircle(Color(0xFF2E7D32), radius = 6f, center = Offset(w * 0.33f, h * 0.38f))
        drawCircle(Color(0xFFFF8F00), radius = 5f, center = Offset(w * 0.65f, h * 0.65f))
        drawCircle(Color(0xFF2E7D32), radius = 5f, center = Offset(w * 0.8f, h * 0.3f))
    }
}

@Composable
fun MaterialFilterChips(
    selectedFilter: String?,
    onFilterChange: (String?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterChange(null) },
                label = { Text(text = localizedUi("all_materials"), style = KabadiwalaTypography.LabelMedium) }
            )
            val filterCategories = listOf(
                "metal" to "Metal Scrap",
                "paper" to "Paper & Raddi",
                "plastic" to "Plastics",
                "electronics" to "E-Waste & Batteries",
                "glass" to "Glass",
                "textile" to "Textiles",
                "rubber" to "Tyres & Rubber"
            )
            filterCategories.forEach { (key, label) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { onFilterChange(key) },
                    label = { Text(text = label, style = KabadiwalaTypography.LabelMedium) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = getMaterialIconRes(key)),
                            contentDescription = label,
                            tint = KabadiwalaColors.Primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onAction: () -> Unit,
    actionText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, style = KabadiwalaTypography.TitleMedium, textAlign = TextAlign.Center)
        Text(text = subtitle, style = KabadiwalaTypography.BodyMedium, textAlign = TextAlign.Center, color = KabadiwalaColors.OnSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(
                containerColor = KabadiwalaColors.Primary,
                contentColor = KabadiwalaColors.OnPrimary
            )
        ) {
            Text(text = actionText, style = KabadiwalaTypography.LabelLarge)
        }
    }
}