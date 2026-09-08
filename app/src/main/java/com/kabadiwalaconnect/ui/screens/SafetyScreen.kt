package com.kabadiwalaconnect.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kabadiwalaconnect.data.model.EmergencyContact
import com.kabadiwalaconnect.data.model.SafetyCategory
import com.kabadiwalaconnect.data.model.SafetyTip
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.EmergencyContactCard
import com.kabadiwalaconnect.ui.components.SafetyTipCard
import androidx.compose.runtime.collectAsState
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import com.kabadiwalaconnect.viewmodel.SafetyViewModel
import com.kabadiwalaconnect.viewmodel.viewModelFactory

@Composable
fun SafetyScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: SafetyViewModel = viewModel(factory = viewModelFactory()),
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val strings = currentStrings()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val safetyTips by viewModel.safetyTips.collectAsStateWithLifecycle()
    val emergencyContacts by viewModel.emergencyContacts.collectAsStateWithLifecycle()
    val filteredTips by viewModel.filteredTips.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading && safetyTips.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = KabadiwalaColors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Loading safety guidelines...", style = KabadiwalaTypography.BodyLarge, color = KabadiwalaColors.OnSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Language switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = strings.safetyTips, style = KabadiwalaTypography.HeadlineMedium, color = KabadiwalaColors.OnBackground)
                        Text(text = "Stay safe while handling scrap materials", style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant)
                    }
                    LanguageTopBarButton(
                        currentLanguage = currentLanguage,
                        onClick = { languageViewModel.openLanguageDialog() }
                    )
                }

                // Category Filter
                CategoryFilterRow(
                    categories = listOf(null) + SafetyCategory.entries,
                    selectedCategory = selectedCategory,
                    onCategoryClick = { viewModel.filterByCategory(it) }
                )

                // Safety Tips
                SectionHeader(title = "Safety Guidelines", count = filteredTips.size)

                if (filteredTips.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(filteredTips) { tip ->
                            SafetyTipCard(tip = tip)
                        }
                    }
                } else {
                    EmptyState(
                        title = "No tips in this category",
                        subtitle = "Try selecting a different category",
                        icon = Icons.Filled.Shield
                    )
                }

                // Emergency Contacts
                SectionHeader(title = "Emergency Contacts", count = emergencyContacts.size)

                if (emergencyContacts.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(emergencyContacts) { contact ->
                            EmergencyContactCard(contact = contact)
                        }
                    }
                }

                if (error != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = KabadiwalaColors.ErrorContainer,
                            contentColor = KabadiwalaColors.Error
                        )
                    ) {
                        Text(
                            text = error ?: "",
                            style = KabadiwalaTypography.BodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    categories: List<SafetyCategory?>,
    selectedCategory: SafetyCategory?,
    onCategoryClick: (SafetyCategory?) -> Unit
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
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                val label = category?.name?.replace("_", " ") ?: "All"
                val icon = category?.let { getCategoryIcon(it) } ?: Icons.Filled.FilterList
                val color = category?.let { getCategoryColor(it) } ?: KabadiwalaColors.Primary

                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryClick(category) },
                    label = { Text(text = label, style = KabadiwalaTypography.LabelMedium) },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) KabadiwalaColors.OnPrimary else color,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color,
                        selectedLabelColor = KabadiwalaColors.OnPrimary,
                        selectedLeadingIconColor = KabadiwalaColors.OnPrimary,
                        containerColor = color.copy(alpha = 0.12f),
                        labelColor = color,
                        iconColor = color
                    )
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = KabadiwalaTypography.TitleLarge)
        Badge(
            containerColor = KabadiwalaColors.Primary,
            contentColor = KabadiwalaColors.OnPrimary
        ) {
            Text(text = "$count", style = KabadiwalaTypography.LabelSmall)
        }
    }
}

@Composable
private fun EmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
    }
}

fun getCategoryIcon(category: SafetyCategory): ImageVector {
    return when (category) {
        SafetyCategory.PERSONAL_PROTECTION -> Icons.Filled.Shield
        SafetyCategory.MATERIAL_HANDLING -> Icons.Filled.Backpack
        SafetyCategory.HAZARDOUS_MATERIALS -> Icons.Filled.Warning
        SafetyCategory.TRANSACTION_SAFETY -> Icons.Filled.Verified
        SafetyCategory.ENVIRONMENTAL -> Icons.Filled.Eco
    }
}

@Composable
fun getCategoryColor(category: SafetyCategory): Color {
    return when (category) {
        SafetyCategory.PERSONAL_PROTECTION -> KabadiwalaColors.Primary
        SafetyCategory.MATERIAL_HANDLING -> KabadiwalaColors.Secondary
        SafetyCategory.HAZARDOUS_MATERIALS -> KabadiwalaColors.Error
        SafetyCategory.TRANSACTION_SAFETY -> KabadiwalaColors.Info
        SafetyCategory.ENVIRONMENTAL -> KabadiwalaColors.Success
    }
}