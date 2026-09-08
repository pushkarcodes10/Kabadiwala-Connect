package com.kabadiwalaconnect.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kabadiwalaconnect.language.Language
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography

import com.kabadiwalaconnect.language.LocalLanguageViewModel
import com.kabadiwalaconnect.language.LocalCurrentLanguage

/**
 * Prominent top bar button that displays the current language in native script
 * and opens the 28 Indian languages selection dialog on tap.
 */
@OptIn(ExperimentalUnitApi::class)
@Composable
fun LanguageTopBarButton(
    currentLanguage: Language = LocalCurrentLanguage.current,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val langVm = LocalLanguageViewModel.current
    val actualOnClick: () -> Unit = onClick ?: { langVm?.openLanguageDialog(); Unit }
    Surface(
        onClick = actualOnClick,
        shape = RoundedCornerShape(20.dp),
        color = KabadiwalaColors.PrimaryContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, KabadiwalaColors.Primary.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(KabadiwalaColors.Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Translate,
                    contentDescription = "Language",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = currentLanguage.nativeName,
                style = KabadiwalaTypography.LabelMedium,
                fontWeight = FontWeight.Bold,
                color = KabadiwalaColors.Primary,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = KabadiwalaColors.Primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Modal dialog that allows users to search and select any of the 28 Indian languages.
 */
@Composable
fun LanguageSelectionDialog(
    viewModel: LanguageViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val filteredLanguages by viewModel.filteredLanguages.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(
                containerColor = KabadiwalaColors.Surface,
                contentColor = KabadiwalaColors.OnSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with icon and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(KabadiwalaColors.PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = null,
                                tint = KabadiwalaColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Select Language / भाषा चुनें",
                                style = KabadiwalaTypography.TitleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "All 28 Languages of India • भारत की 28 भाषाएँ",
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(KabadiwalaColors.SurfaceVariant.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = KabadiwalaColors.OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search 28 languages (e.g. हिन्दी, தமிழ், Bengali...)",
                            style = KabadiwalaTypography.BodyMedium,
                            color = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = KabadiwalaColors.Primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = KabadiwalaColors.OnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredLanguages.size} of 28 Languages",
                        style = KabadiwalaTypography.LabelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = KabadiwalaColors.Primary
                    )
                    Text(
                        text = "Current: ${currentLanguage.nativeName}",
                        style = KabadiwalaTypography.LabelSmall,
                        color = KabadiwalaColors.OnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredLanguages, key = { it.code }) { language ->
                        val isSelected = language.code == currentLanguage.code

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.changeLanguage(language)
                                    Toast.makeText(
                                        context,
                                        "Language changed to ${language.nativeName} (${language.name})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .then(
                                    if (isSelected) {
                                        Modifier.border(
                                            2.dp,
                                            KabadiwalaColors.Primary,
                                            RoundedCornerShape(14.dp)
                                        )
                                    } else {
                                        Modifier.border(
                                            1.dp,
                                            KabadiwalaColors.OutlineVariant.copy(alpha = 0.5f),
                                            RoundedCornerShape(14.dp)
                                        )
                                    }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    KabadiwalaColors.PrimaryContainer.copy(alpha = 0.45f)
                                } else {
                                    KabadiwalaColors.Surface
                                }
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = language.nativeName,
                                            style = KabadiwalaTypography.TitleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) KabadiwalaColors.Primary else KabadiwalaColors.OnSurface
                                        )
                                        Text(
                                            text = "• ${language.name}",
                                            style = KabadiwalaTypography.BodyMedium,
                                            color = KabadiwalaColors.OnSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = language.region,
                                        style = KabadiwalaTypography.LabelSmall,
                                        color = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = KabadiwalaColors.Primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
