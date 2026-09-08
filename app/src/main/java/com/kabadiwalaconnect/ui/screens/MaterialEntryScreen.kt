package com.kabadiwalaconnect.ui.screens

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.MaterialCategory
import com.kabadiwalaconnect.data.model.PendingPickupStore
import com.kabadiwalaconnect.data.model.ScrapCartItem
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.MaterialCard
import com.kabadiwalaconnect.ui.components.MaterialImage
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import com.kabadiwalaconnect.viewmodel.MaterialEntryViewModel
import com.kabadiwalaconnect.viewmodel.viewModelFactory
import com.kabadiwalaconnect.KabadiwalaApplication
import com.kabadiwalaconnect.data.model.PaymentMethod
import com.kabadiwalaconnect.data.model.Recycler
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.model.TransactionItem
import com.kabadiwalaconnect.data.model.TransactionStatus
import com.kabadiwalaconnect.ui.components.ScrapSaleSuccessDialog
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.random.Random

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.language.EntityTranslations
import com.kabadiwalaconnect.language.localizedName
import com.kabadiwalaconnect.language.localizedUi
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton

@Composable
fun MaterialEntryScreen(
    onNavigate: (Screen) -> Unit,
    preselectedMaterialId: String? = null,
    viewModel: MaterialEntryViewModel = viewModel(factory = viewModelFactory()),
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val strings = currentStrings()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val materials by viewModel.materials.collectAsStateWithLifecycle()
    val selectedMaterial by viewModel.selectedMaterial.collectAsStateWithLifecycle()
    val weight by viewModel.weight.collectAsStateWithLifecycle()
    val estimatedPrice by viewModel.estimatedPrice.collectAsStateWithLifecycle()
    val minEstimatedPrice by viewModel.minEstimatedPrice.collectAsStateWithLifecycle()
    val maxEstimatedPrice by viewModel.maxEstimatedPrice.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(preselectedMaterialId, materials) {
        if (!preselectedMaterialId.isNullOrBlank() && materials.isNotEmpty()) {
            val target = materials.find { it.id == preselectedMaterialId }
            if (target != null) {
                viewModel.selectMaterial(target)
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<MaterialCategory?>(null) }
    val weightText = remember { mutableStateOf(weight) }

    // Multi-item cart state
    val cartItems = remember {
        mutableStateListOf<ScrapCartItem>().apply {
            addAll(PendingPickupStore.pendingCartItems)
        }
    }
    val cartTotalWeight = cartItems.sumOf { it.weightKg }
    val cartTotalAmount = cartItems.sumOf { it.estimatedAmount }

    val filteredMaterials = remember(materials, searchQuery, selectedCategory, currentLanguage) {
        materials.filter { material ->
            val localizedName = EntityTranslations.getLocalizedMaterialName(material.id, currentLanguage.code)
            val matchesCategory = selectedCategory == null || material.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                    material.name.contains(searchQuery, ignoreCase = true) ||
                    localizedName.contains(searchQuery, ignoreCase = true) ||
                    material.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading && materials.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = KabadiwalaColors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Loading scrap goods...", style = KabadiwalaTypography.BodyLarge, color = KabadiwalaColors.OnSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Language switcher button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = strings.sellScrap, style = KabadiwalaTypography.HeadlineMedium, color = KabadiwalaColors.OnBackground)
                        Text(text = strings.selectMaterial, style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant)
                    }
                    LanguageTopBarButton(
                        currentLanguage = currentLanguage,
                        onClick = { languageViewModel.openLanguageDialog() }
                    )
                }

                // AI Photo Scanner Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.MaterialScanner) },
                    colors = CardDefaults.cardColors(
                        containerColor = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.7f),
                        contentColor = KabadiwalaColors.OnPrimaryContainer
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(KabadiwalaColors.Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = localizedUi("snap_photo_title"),
                                style = KabadiwalaTypography.TitleSmall,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.Primary
                            )
                            Text(
                                text = localizedUi("snap_photo_sub"),
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Scan",
                            tint = KabadiwalaColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(localizedUi("search_goods_hint")) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = KabadiwalaColors.Primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Mini Cart Sticky Bar when cart is non-empty
                if (cartItems.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Cart: ${cartItems.size} item${if (cartItems.size > 1) "s" else ""} (${"%.1f".format(cartTotalWeight)} kg)",
                                    style = KabadiwalaTypography.BodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            Text(
                                text = "₹${"%.2f".format(cartTotalAmount)}",
                                style = KabadiwalaTypography.TitleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }

                // Category Filter Chips
                val categories = listOf(
                    null to "${EntityTranslations.getLocalizedCategoryName(MaterialCategory.ALL, currentLanguage.code)} (${materials.size})",
                    MaterialCategory.METAL to EntityTranslations.getLocalizedCategoryName(MaterialCategory.METAL, currentLanguage.code),
                    MaterialCategory.PAPER to EntityTranslations.getLocalizedCategoryName(MaterialCategory.PAPER, currentLanguage.code),
                    MaterialCategory.PLASTIC to EntityTranslations.getLocalizedCategoryName(MaterialCategory.PLASTIC, currentLanguage.code),
                    MaterialCategory.ELECTRONICS to EntityTranslations.getLocalizedCategoryName(MaterialCategory.ELECTRONICS, currentLanguage.code),
                    MaterialCategory.RUBBER to EntityTranslations.getLocalizedCategoryName(MaterialCategory.RUBBER, currentLanguage.code),
                    MaterialCategory.TEXTILE to EntityTranslations.getLocalizedCategoryName(MaterialCategory.TEXTILE, currentLanguage.code),
                    MaterialCategory.GLASS to EntityTranslations.getLocalizedCategoryName(MaterialCategory.GLASS, currentLanguage.code),
                    MaterialCategory.OTHER to EntityTranslations.getLocalizedCategoryName(MaterialCategory.OTHER, currentLanguage.code)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (cat, label) ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(label, style = KabadiwalaTypography.LabelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = KabadiwalaColors.PrimaryContainer,
                                selectedLabelColor = KabadiwalaColors.Primary
                            )
                        )
                    }
                }

                // Material Selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = localizedUi("select_good"), style = KabadiwalaTypography.TitleMedium)
                        Text(
                            text = "${filteredMaterials.size} items",
                            style = KabadiwalaTypography.LabelSmall,
                            color = KabadiwalaColors.OnSurfaceVariant
                        )
                    }

                    if (filteredMaterials.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.SurfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "No scrap goods matching \"$searchQuery\"",
                                modifier = Modifier.padding(24.dp),
                                style = KabadiwalaTypography.BodyMedium,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 340.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredMaterials, key = { it.id }) { material ->
                                val isSelected = selectedMaterial?.id == material.id
                                MaterialCard(
                                    material = material,
                                    onClick = { viewModel.selectMaterial(material) },
                                    showPrice = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(
                                            if (isSelected)
                                                Modifier.border(2.dp, KabadiwalaColors.Primary, KabadiwalaShapes.Medium)
                                            else
                                                Modifier
                                        )
                                )
                            }
                        }
                    }
                }

                // Weight Entry
                selectedMaterial?.let { material ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = localizedUi("selected_mat_weight"), style = KabadiwalaTypography.TitleMedium)

                        // Hero preview card of selected scrap material with picture
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = KabadiwalaColors.Surface,
                                contentColor = KabadiwalaColors.OnSurface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                MaterialImage(
                                    material = material,
                                    modifier = Modifier.size(68.dp)
                                )
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = material.localizedName,
                                        style = KabadiwalaTypography.TitleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = localizedUi("current_mandi_rate_val", material.rateRange),
                                        style = KabadiwalaTypography.BodyMedium,
                                        color = KabadiwalaColors.Primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    if (material.acceptedTypes.isNotEmpty()) {
                                        Text(
                                            text = material.acceptedTypes.joinToString(" • "),
                                            style = KabadiwalaTypography.LabelSmall,
                                            color = KabadiwalaColors.OnSurfaceVariant,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = KabadiwalaColors.Surface,
                                contentColor = KabadiwalaColors.OnSurface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Scale,
                                            contentDescription = "Weight",
                                            tint = KabadiwalaColors.Primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    OutlinedTextField(
                                        value = weightText.value,
                                        onValueChange = { newValue ->
                                            weightText.value = newValue.filter { it.isDigit() || it == '.' }
                                            viewModel.updateWeight(weightText.value)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp),
                                        label = { Text(text = localizedUi("weight_kg"), style = KabadiwalaTypography.BodyMedium) },
                                        placeholder = { Text(text = localizedUi("enter_weight_kg"), style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.5f)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        visualTransformation = VisualTransformation.None,
                                        singleLine = true,
                                        isError = weightText.value.isNotBlank() && weightText.value.toDoubleOrNull() == null
                                    )
                                }

                                // Quick weight selection buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(5.0, 10.0, 25.0, 50.0).forEach { wt ->
                                        OutlinedButton(
                                            onClick = {
                                                weightText.value = "%.0f".format(wt)
                                                viewModel.updateWeight(weightText.value)
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                                        ) {
                                            Text("%.0f kg".format(wt), style = KabadiwalaTypography.LabelSmall)
                                        }
                                    }
                                }

                                if (weightText.value.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = localizedUi("selected_prefix", material.localizedName),
                                            style = KabadiwalaTypography.BodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = KabadiwalaColors.OnSurfaceVariant
                                        )
                                        Text(
                                            text = localizedUi("rate_prefix", material.rateRange),
                                            style = KabadiwalaTypography.BodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = KabadiwalaColors.Primary
                                        )
                                    }
                                }
                            }
                        }

                        // Estimated Price for current selection
                        val currentWtNum = weightText.value.toDoubleOrNull() ?: 0.0
                        if (currentWtNum > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = KabadiwalaColors.PrimaryContainer,
                                    contentColor = KabadiwalaColors.Primary
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = localizedUi("item_est_price"), style = KabadiwalaTypography.LabelMedium, color = KabadiwalaColors.Primary.copy(alpha = 0.8f))
                                            Text(
                                                text = "₹%.2f".format(if (estimatedPrice > 0) estimatedPrice else currentWtNum * material.basePricePerKg),
                                                style = KabadiwalaTypography.HeadlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = KabadiwalaColors.Primary
                                            )
                                            Text(
                                                text = localizedUi("based_on_weight_rate", weightText.value, material.rateRange),
                                                style = KabadiwalaTypography.BodySmall,
                                                color = KabadiwalaColors.Primary.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // + ADD MATERIAL TO CART BUTTON
                        Button(
                            onClick = {
                                val wt = weightText.value.toDoubleOrNull() ?: 0.0
                                if (wt > 0) {
                                    val existingIdx = cartItems.indexOfFirst { it.material.id == material.id }
                                    if (existingIdx >= 0) {
                                        val existing = cartItems[existingIdx]
                                        cartItems[existingIdx] = existing.copy(weightKg = existing.weightKg + wt)
                                    } else {
                                        cartItems.add(ScrapCartItem(material = material, weightKg = wt))
                                    }
                                    weightText.value = ""
                                    viewModel.updateWeight("")
                                    val cartedIds = cartItems.map { it.material.id }.toSet()
                                    val nextMat = filteredMaterials.firstOrNull { it.id !in cartedIds } ?: materials.firstOrNull { it.id !in cartedIds }
                                    if (nextMat != null) {
                                        viewModel.selectMaterial(nextMat)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = currentWtNum > 0,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KabadiwalaColors.Secondary,
                                contentColor = KabadiwalaColors.OnSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = localizedUi("add_material_to_cart"),
                                style = KabadiwalaTypography.LabelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 🛒 YOUR SCRAP CART SECTION (when items are added)
                if (cartItems.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF81C784)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Cart Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.ShoppingCart,
                                        contentDescription = null,
                                        tint = Color(0xFF1B5E20),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = localizedUi("your_cart"),
                                        style = KabadiwalaTypography.TitleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2E7D32)),
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
                                TextButton(onClick = {
                                    cartItems.clear()
                                    PendingPickupStore.clear()
                                }) {
                                    Text(
                                        text = localizedUi("clear_all"),
                                        color = KabadiwalaColors.Error,
                                        style = KabadiwalaTypography.LabelMedium
                                    )
                                }
                            }

                            // Cart Items
                            cartItems.forEachIndexed { index, item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, KabadiwalaColors.OutlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        MaterialImage(
                                            material = item.material,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.material.localizedName,
                                                style = KabadiwalaTypography.BodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${item.formattedWeight} × ₹${"%.0f".format(item.material.basePricePerKg)}/kg",
                                                style = KabadiwalaTypography.BodySmall,
                                                color = KabadiwalaColors.OnSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = item.formattedAmount,
                                            style = KabadiwalaTypography.BodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1B5E20)
                                        )
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

                            // Total Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = localizedUi("total_estimated_payout"),
                                            style = KabadiwalaTypography.BodyMedium,
                                            color = KabadiwalaColors.OnSurfaceVariant
                                        )
                                        Text(
                                            text = "${cartItems.size} items • ${"%.1f".format(cartTotalWeight)} kg total",
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
                            }
                        }
                    }
                }

                // SUBMIT / SEND FOR PICKUP BUTTON
                val currentWeightNum = weightText.value.toDoubleOrNull() ?: 0.0
                val hasSelectedExtra = selectedMaterial != null && currentWeightNum > 0
                val canSubmit = cartItems.isNotEmpty() || hasSelectedExtra

                val submitButtonLabel = if (cartItems.isNotEmpty()) {
                    val totalCount = cartItems.size + if (hasSelectedExtra) 1 else 0
                    val extraAmt = if (hasSelectedExtra) currentWeightNum * selectedMaterial!!.basePricePerKg else 0.0
                    localizedUi("send_for_pickup_cart", totalCount, cartTotalAmount + extraAmt)
                } else if (hasSelectedExtra) {
                    localizedUi("send_for_pickup_single", currentWeightNum * selectedMaterial!!.basePricePerKg)
                } else {
                    strings.sendForPickup
                }

                Button(
                    onClick = {
                        val finalCart = cartItems.toMutableList()
                        val curMat = selectedMaterial
                        val curWt = weightText.value.toDoubleOrNull() ?: 0.0
                        if (curMat != null && curWt > 0) {
                            val existingIdx = finalCart.indexOfFirst { it.material.id == curMat.id }
                            if (existingIdx >= 0) {
                                val existing = finalCart[existingIdx]
                                finalCart[existingIdx] = existing.copy(weightKg = existing.weightKg + curWt)
                            } else {
                                finalCart.add(ScrapCartItem(material = curMat, weightKg = curWt))
                            }
                        }

                        if (finalCart.isNotEmpty()) {
                            PendingPickupStore.setPendingItems(finalCart)
                            onNavigate(Screen.RecyclerDiscovery)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = KabadiwalaColors.OnPrimary,
                        disabledContainerColor = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.12f),
                        disabledContentColor = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.38f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(text = submitButtonLabel, style = KabadiwalaTypography.LabelLarge, fontWeight = FontWeight.Bold)
                    }
                }

                if (error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = KabadiwalaColors.ErrorContainer,
                            contentColor = KabadiwalaColors.Error
                        )
                    ) {
                        Text(text = error ?: "", style = KabadiwalaTypography.BodyMedium, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}