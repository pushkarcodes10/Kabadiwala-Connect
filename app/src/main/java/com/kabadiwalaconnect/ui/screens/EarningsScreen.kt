package com.kabadiwalaconnect.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.TrendingUp
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
import com.kabadiwalaconnect.data.model.EarningsSummary
import com.kabadiwalaconnect.viewmodel.EarningsTab
import com.kabadiwalaconnect.data.model.KhataEntry
import com.kabadiwalaconnect.data.model.MonthlyEarnings
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.EarningsSummaryCard
import com.kabadiwalaconnect.ui.components.KhataEntryCard
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import androidx.compose.runtime.collectAsState
import com.kabadiwalaconnect.language.EntityTranslations
import com.kabadiwalaconnect.language.LocalCurrentLanguage
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton
import com.kabadiwalaconnect.viewmodel.EarningsViewModel
import com.kabadiwalaconnect.viewmodel.viewModelFactory

@Composable
fun EarningsScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: EarningsViewModel = viewModel(factory = viewModelFactory()),
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val strings = currentStrings()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val earningsSummary by viewModel.earningsSummary.collectAsStateWithLifecycle()
    val khataEntries by viewModel.khataEntries.collectAsStateWithLifecycle()
    val monthlyEarnings by viewModel.monthlyEarnings.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
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
                Text(text = "Loading earnings...", style = KabadiwalaTypography.BodyLarge, color = KabadiwalaColors.OnSurfaceVariant)
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
                        Text(text = strings.digitalKhata, style = KabadiwalaTypography.HeadlineMedium, color = KabadiwalaColors.OnBackground)
                        Text(text = "Track your income and transaction history", style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant)
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

                // Tab Selection
                TabRow(
                    tabs = EarningsTab.entries,
                    selectedTab = selectedTab,
                    onTabClick = { viewModel.setSelectedTab(it) }
                )

                // Tab Content
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    when (selectedTab) {
                        EarningsTab.SUMMARY -> SummaryTab(earningsSummary = earningsSummary)
                        EarningsTab.KHATA -> KhataTab(entries = khataEntries)
                        EarningsTab.MONTHLY -> MonthlyTab(monthlyEarnings = monthlyEarnings)
                    }
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
fun TabRow(
    tabs: List<EarningsTab>,
    selectedTab: EarningsTab,
    onTabClick: (EarningsTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            TextButton(
                onClick = { onTabClick(tab) },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp)
                    .background(
                        color = if (isSelected) KabadiwalaColors.PrimaryContainer else Color.Transparent,
                        shape = KabadiwalaShapes.Medium
                    )
            ) {
                Text(
                    text = tab.name,
                    style = KabadiwalaTypography.LabelLarge,
                    color = if (isSelected) KabadiwalaColors.Primary else KabadiwalaColors.OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SummaryTab(earningsSummary: EarningsSummary?) {
    earningsSummary?.let { summary ->
        val items = listOf(
            EarningsItem("Total Earnings", summary.formattedTotalEarnings, "All time", Icons.Filled.AccountBalanceWallet, KabadiwalaColors.Primary),
            EarningsItem("This Month", summary.formattedThisMonthEarnings, "${summary.thisMonthTransactions} transactions", Icons.Filled.CalendarMonth, KabadiwalaColors.Secondary),
            EarningsItem("Total Weight", summary.formattedTotalWeight, "Recycled", Icons.Filled.Scale, KabadiwalaColors.Success),
            EarningsItem("Avg/Transaction", summary.formattedAverage, "${summary.totalTransactions} total", Icons.Filled.TrendingUp, KabadiwalaColors.Info)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.chunked(2).forEach { pair ->
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
    }
}

@Composable
fun KhataTab(entries: List<KhataEntry>) {
    if (entries.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(entries) { entry ->
                KhataEntryCard(entry = entry)
            }
        }
    } else {
        EmptyState(
            title = "No khata entries",
            subtitle = "Your transaction history will appear here",
            icon = Icons.Filled.ReceiptLong
        )
    }
}

@Composable
fun MonthlyTab(monthlyEarnings: List<MonthlyEarnings>) {
    if (monthlyEarnings.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            items(monthlyEarnings) { monthly ->
                MonthlyEarningsCard(monthly = monthly)
            }
        }
    } else {
        EmptyState(
            title = "No monthly data",
            subtitle = "Monthly earnings will appear here",
            icon = Icons.Filled.CalendarMonth
        )
    }
}

@Composable
fun MonthlyEarningsCard(monthly: MonthlyEarnings) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = monthly.formattedMonth, style = KabadiwalaTypography.TitleMedium)
                Text(text = monthly.formattedEarnings, style = KabadiwalaTypography.HeadlineSmall, color = KabadiwalaColors.Primary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MonthlyStatItem(label = "Weight", value = "${monthly.totalWeight} kg", icon = Icons.Filled.Scale, color = KabadiwalaColors.Success)
                MonthlyStatItem(label = "Transactions", value = monthly.transactionCount.toString(), icon = Icons.Filled.ReceiptLong, color = KabadiwalaColors.Info)
            }
            Spacer(modifier = Modifier.height(12.dp))
            monthly.materialBreakdown.forEach { (material, earnings) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = EntityTranslations.getLocalizedMaterialName(material, LocalCurrentLanguage.current.code),
                        style = KabadiwalaTypography.BodyMedium
                    )
                    Text(text = "₹${"%.0f".format(earnings.earnings)} (${earnings.transactionCount} txns)", style = KabadiwalaTypography.BodyMedium, color = KabadiwalaColors.OnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun RowScope.MonthlyStatItem(label: String, value: String, icon: ImageVector, color: Color) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(text = value, style = KabadiwalaTypography.TitleSmall, color = color)
        Text(text = label, style = KabadiwalaTypography.LabelSmall, color = KabadiwalaColors.OnSurfaceVariant)
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

private data class EarningsItem(
    val title: String,
    val amount: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color
)