package com.kabadiwalaconnect.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.MarketPriceCard
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import androidx.compose.runtime.collectAsState
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton
import com.kabadiwalaconnect.viewmodel.MarketPricesViewModel
import com.kabadiwalaconnect.viewmodel.viewModelFactory
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun MarketPricesScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: MarketPricesViewModel = viewModel(factory = viewModelFactory()),
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val strings = currentStrings()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val prices by viewModel.prices.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading && prices.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = KabadiwalaColors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Loading market prices...", style = KabadiwalaTypography.BodyLarge, color = KabadiwalaColors.OnSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Language switcher and refresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = strings.liveMarketPrices, style = KabadiwalaTypography.HeadlineMedium, color = KabadiwalaColors.OnBackground)
                        Text(text = "Real-time rates for scrap materials", style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LanguageTopBarButton(
                            currentLanguage = currentLanguage,
                            onClick = { languageViewModel.openLanguageDialog() }
                        )
                        IconButton(onClick = { viewModel.refreshPrices() }, enabled = !isRefreshing) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh prices",
                                tint = KabadiwalaColors.Primary
                            )
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LegendItem(icon = Icons.Filled.TrendingUp, color = KabadiwalaColors.Success, label = "Price Up")
                    LegendItem(icon = Icons.Filled.TrendingDown, color = KabadiwalaColors.Error, label = "Price Down")
                    LegendItem(icon = Icons.Filled.Remove, color = KabadiwalaColors.OnSurfaceVariant, label = "Stable")
                }

                // Prices List
                if (prices.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(prices) { price ->
                            MarketPriceCard(price = price)
                        }
                    }
                } else {
                    EmptyState(
                        title = "No price data",
                        subtitle = "Pull to refresh or try again later",
                        icon = Icons.Filled.TrendingUp
                    )
                }

                // Last updated
                prices.firstOrNull()?.let { price ->
                    Text(
                        text = "Last updated: ${SimpleDateFormat("dd MMM yyyy, HH:mm").format(Date(price.lastUpdated.toEpochMilli()))}",
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
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

@Composable
fun RowScope.LegendItem(icon: ImageVector, color: Color, label: String) {
    Row(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.padding(start = 4.dp))
        Text(text = label, style = KabadiwalaTypography.LabelSmall, color = color)
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
    }
}