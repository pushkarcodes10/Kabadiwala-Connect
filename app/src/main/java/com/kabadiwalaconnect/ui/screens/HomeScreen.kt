package com.kabadiwalaconnect.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.kabadiwalaconnect.ui.components.EarningsSummaryCard
import com.kabadiwalaconnect.ui.components.MarketPriceCard
import com.kabadiwalaconnect.ui.components.MaterialCard
import com.kabadiwalaconnect.ui.components.QuickActionCard
import com.kabadiwalaconnect.ui.components.RecyclerCard
import com.kabadiwalaconnect.ui.components.TransactionCard
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import androidx.compose.runtime.collectAsState
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.language.localizedUi
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton
import com.kabadiwalaconnect.viewmodel.HomeViewModel
import com.kabadiwalaconnect.viewmodel.viewModelFactory

@Composable
fun HomeScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = viewModelFactory()),
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val strings = currentStrings()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val earningsSummary by viewModel.earningsSummary.collectAsStateWithLifecycle()
    val recentTransactions by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val topMaterials by viewModel.topMaterials.collectAsStateWithLifecycle()
    val marketPrices by viewModel.marketPrices.collectAsStateWithLifecycle()
    val nearbyRecyclers by viewModel.nearbyRecyclers.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KabadiwalaColors.Background),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading && earningsSummary == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = KabadiwalaColors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Loading dashboard...", style = KabadiwalaTypography.BodyLarge, color = KabadiwalaColors.OnSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with refresh and Change Language button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = strings.dashboard, style = KabadiwalaTypography.HeadlineMedium, color = KabadiwalaColors.OnBackground)
                        Text(text = strings.welcomeBack, style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LanguageTopBarButton(
                            currentLanguage = currentLanguage,
                            onClick = { languageViewModel.openLanguageDialog() }
                        )
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                tint = KabadiwalaColors.Primary
                            )
                        }
                    }
                }

                // Earnings Summary Cards
                earningsSummary?.let { summary ->
                    val summaryItems = listOf(
                        HomeEarningsItem(strings.totalEarnings, summary.formattedTotalEarnings, localizedUi("all_time"), Icons.Filled.AccountBalanceWallet, KabadiwalaColors.Primary),
                        HomeEarningsItem(strings.thisMonth, summary.formattedThisMonthEarnings, "${summary.thisMonthTransactions} txns", Icons.Filled.CalendarMonth, KabadiwalaColors.Secondary),
                        HomeEarningsItem(strings.totalWeight, summary.formattedTotalWeight, localizedUi("recycled"), Icons.Filled.Scale, KabadiwalaColors.Success),
                        HomeEarningsItem(strings.avgPerTransaction, summary.formattedAverage, "${summary.totalTransactions} total", Icons.Filled.TrendingUp, KabadiwalaColors.Info)
                    )
                    summaryItems.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { data ->
                                Box(modifier = Modifier.weight(1f)) {
                                    EarningsSummaryCard(
                                        title = data.title,
                                        amount = data.amount,
                                        subtitle = data.subtitle,
                                        icon = data.icon,
                                        iconColor = data.iconColor
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Photo Scanner Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(KabadiwalaShapes.Large)
                        .clickable { onNavigate(Screen.MaterialScanner) },
                    colors = CardDefaults.cardColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.aiScannerTitle,
                                style = KabadiwalaTypography.TitleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = strings.aiScannerSubtitle,
                                style = KabadiwalaTypography.BodySmall,
                                color = Color.White.copy(alpha = 0.88f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowForward,
                                contentDescription = "Scan",
                                tint = KabadiwalaColors.Primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Quick Actions
                HomeSectionHeader(
                    title = strings.quickActions,
                    onActionClick = { },
                    actionText = null
                )
                val quickActions = listOf(
                    HomeQuickAction(localizedUi("snap_and_price"), Icons.Filled.CameraAlt, KabadiwalaColors.PrimaryContainer, KabadiwalaColors.Primary) { onNavigate(Screen.MaterialScanner) },
                    HomeQuickAction(strings.sellScrap, Icons.Filled.Add, Color(0xFFE8F5E9), KabadiwalaColors.Primary) { onNavigate(Screen.MaterialEntry) },
                    HomeQuickAction(strings.checkPrices, Icons.Filled.TrendingUp, Color(0xFFBBDEFB), KabadiwalaColors.Info) { onNavigate(Screen.MarketPrices) },
                    HomeQuickAction(strings.findRecycler, Icons.Filled.LocationOn, Color(0xFFC8E6C9), KabadiwalaColors.Success) { onNavigate(Screen.RecyclerDiscovery) }
                )
                quickActions.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { action ->
                            Box(modifier = Modifier.weight(1f)) {
                                QuickActionCard(
                                    title = action.title,
                                    icon = action.icon,
                                    onClick = action.onClick,
                                    backgroundColor = action.backgroundColor,
                                    iconColor = action.iconColor
                                )
                            }
                        }
                    }
                }

                // Market Prices
                HomeSectionHeader(
                    title = strings.liveMarketPrices,
                    onActionClick = { onNavigate(Screen.MarketPrices) },
                    actionText = strings.viewAll
                )
                if (marketPrices.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        marketPrices.take(4).forEach { price ->
                            MarketPriceCard(price = price, onClick = { onNavigate(Screen.MarketPrices) })
                        }
                    }
                }

                // Top Materials
                HomeSectionHeader(
                    title = strings.popularMaterials,
                    onActionClick = { onNavigate(Screen.MaterialEntry) },
                    actionText = strings.viewAll
                )
                if (topMaterials.isNotEmpty()) {
                    topMaterials.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            pair.forEach { material ->
                                Box(modifier = Modifier.weight(1f)) {
                                    MaterialCard(
                                        material = material,
                                        onClick = { onNavigate(Screen.MaterialEntry) },
                                        isGrid = true
                                    )
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Nearby Recyclers
                HomeSectionHeader(
                    title = strings.nearbyRecyclers,
                    onActionClick = { onNavigate(Screen.RecyclerDiscovery) },
                    actionText = strings.viewAll
                )
                if (nearbyRecyclers.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        nearbyRecyclers.take(3).forEach { recycler ->
                            RecyclerCard(
                                recycler = recycler,
                                onClick = { onNavigate(Screen.RecyclerDetail(recycler.id)) },
                                onBookPickup = { onNavigate(Screen.RecyclerDetail(recycler.id)) }
                            )
                        }
                    }
                }

                // Recent Transactions
                HomeSectionHeader(
                    title = localizedUi("recent_transactions"),
                    onActionClick = { onNavigate(Screen.Transaction) },
                    actionText = strings.viewAll
                )
                if (recentTransactions.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentTransactions.take(5).forEach { transaction ->
                            TransactionCard(transaction = transaction, onClick = { onNavigate(Screen.TransactionDetail(transaction.id)) })
                        }
                    }
                } else {
                    HomeEmptyStateCard(
                        title = localizedUi("no_transactions_yet"),
                        subtitle = localizedUi("start_selling_scrap_history"),
                        icon = Icons.Filled.ReceiptLong,
                        onAction = { onNavigate(Screen.MaterialEntry) },
                        actionText = strings.sellScrap
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
private fun HomeSectionHeader(
    title: String,
    onActionClick: () -> Unit,
    actionText: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = KabadiwalaTypography.TitleLarge)
        actionText?.let { text ->
            TextButton(onClick = onActionClick) {
                Text(text = text, style = KabadiwalaTypography.LabelLarge, color = KabadiwalaColors.Primary)
            }
        }
    }
}

@Composable
private fun HomeEmptyStateCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onAction: () -> Unit,
    actionText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.SurfaceVariant,
            contentColor = KabadiwalaColors.OnSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Text(text = title, style = KabadiwalaTypography.TitleMedium, textAlign = TextAlign.Center)
            Text(text = subtitle, style = KabadiwalaTypography.BodyMedium, textAlign = TextAlign.Center, maxLines = 2)
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = KabadiwalaColors.Primary, contentColor = KabadiwalaColors.OnPrimary)
            ) {
                Text(text = actionText, style = KabadiwalaTypography.LabelLarge)
            }
        }
    }
}

private data class HomeEarningsItem(
    val title: String,
    val amount: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color
)

private data class HomeQuickAction(
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color,
    val onClick: () -> Unit
)