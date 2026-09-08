package com.kabadiwalaconnect.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.model.TransactionStatus
import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.currentStrings
import com.kabadiwalaconnect.language.localizedMaterialName
import com.kabadiwalaconnect.language.localizedRecyclerName
import com.kabadiwalaconnect.language.localizedUi
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton
import com.kabadiwalaconnect.ui.components.StatusChip
import com.kabadiwalaconnect.ui.components.TransactionCard
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import com.kabadiwalaconnect.viewmodel.TransactionViewModel
import com.kabadiwalaconnect.viewmodel.viewModelFactory

@Composable
fun TransactionScreen(
    onNavigate: (Screen) -> Unit,
    viewModel: TransactionViewModel = viewModel(factory = viewModelFactory()),
    languageViewModel: LanguageViewModel = viewModel(factory = viewModelFactory())
) {
    val strings = currentStrings()
    val currentLanguage by languageViewModel.currentLanguage.collectAsState()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val pendingTransactions by viewModel.pendingTransactions.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val verifyingOtp by viewModel.verifyingOtp.collectAsStateWithLifecycle()

    var selectedPendingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var otpInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KabadiwalaColors.Background),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading && transactions.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = KabadiwalaColors.Primary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading transactions...",
                    style = KabadiwalaTypography.BodyLarge,
                    color = KabadiwalaColors.OnSurfaceVariant
                )
            }
        } else {
            // Single LazyColumn eliminates nested scrolling and unbounded height distortion
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with Language switcher
                item(key = "header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Transactions / लेन-देन",
                                style = KabadiwalaTypography.HeadlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.OnBackground
                            )
                            Text(
                                text = "Manage your scrap transactions & testing slots",
                                style = KabadiwalaTypography.BodyMedium,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }
                        LanguageTopBarButton(
                            currentLanguage = currentLanguage,
                            onClick = { languageViewModel.openLanguageDialog() }
                        )
                    }
                }

                // Pending Transactions Section
                if (pendingTransactions.isNotEmpty()) {
                    item(key = "pending_header") {
                        SectionHeader(
                            title = localizedUi("pending_handover"),
                            count = pendingTransactions.size
                        )
                    }

                    items(pendingTransactions, key = { "pending_${it.id}" }) { transaction ->
                        PendingTransactionCard(
                            transaction = transaction,
                            onClick = { onNavigate(Screen.TransactionDetail(transaction.id)) },
                            onConfirmClick = {
                                selectedPendingTransaction = transaction
                                otpInput = transaction.otp ?: ""
                            }
                        )
                    }
                }

                // All Transactions Section Header
                item(key = "all_header") {
                    SectionHeader(
                        title = localizedUi("all_transactions"),
                        count = transactions.size
                    )
                }

                if (transactions.isNotEmpty()) {
                    items(transactions, key = { "all_${it.id}" }) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            onClick = {
                                onNavigate(Screen.TransactionDetail(transaction.id))
                            },
                            onConfirmClick = if (transaction.status == TransactionStatus.PENDING || transaction.status == TransactionStatus.CONFIRMED) {
                                {
                                    selectedPendingTransaction = transaction
                                    otpInput = transaction.otp ?: ""
                                }
                            } else null
                        )
                    }
                } else {
                    item(key = "empty_state") {
                        EmptyState(
                            title = localizedUi("no_transactions_yet"),
                            subtitle = localizedUi("start_selling_scrap_history"),
                            icon = Icons.Filled.ReceiptLong,
                            onAction = { onNavigate(Screen.MaterialEntry) },
                            actionText = strings.sellScrap
                        )
                    }
                }

                error?.let { errorMsg ->
                    item(key = "error_banner") {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = KabadiwalaColors.PrimaryContainer,
                                contentColor = KabadiwalaColors.Primary
                            )
                        ) {
                            Text(
                                text = errorMsg,
                                style = KabadiwalaTypography.BodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // OTP Verification Dialog with auto-fill demo OTP and robust UI
    selectedPendingTransaction?.let { transaction ->
        OTPVerificationDialog(
            transaction = transaction,
            otpValue = otpInput,
            isVerifying = verifyingOtp == transaction.id,
            onOtpChange = { otpInput = it },
            onConfirm = { otp ->
                viewModel.confirmHandover(transaction, otp)
                selectedPendingTransaction = null
            },
            onCancel = {
                selectedPendingTransaction = null
                otpInput = ""
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = KabadiwalaTypography.TitleLarge,
            fontWeight = FontWeight.Bold,
            color = KabadiwalaColors.OnBackground
        )
        Surface(
            shape = KabadiwalaShapes.Small,
            color = KabadiwalaColors.Primary,
            contentColor = KabadiwalaColors.OnPrimary
        ) {
            Text(
                text = "$count items",
                style = KabadiwalaTypography.LabelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PendingTransactionCard(
    transaction: Transaction,
    onClick: () -> Unit = {},
    onConfirmClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(KabadiwalaShapes.Medium)
            .border(
                1.dp,
                KabadiwalaColors.Primary.copy(alpha = 0.25f),
                KabadiwalaShapes.Medium
            ),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = transaction.localizedRecyclerName,
                        style = KabadiwalaTypography.TitleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = transaction.formattedDate,
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = transaction.formattedAmount,
                        style = KabadiwalaTypography.HeadlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = KabadiwalaColors.Primary
                    )
                    StatusChip(status = transaction.status)
                }
            }

            // Testing / Inspection Slot Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                shape = KabadiwalaShapes.Small,
                color = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Testing Slot",
                        tint = KabadiwalaColors.Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = transaction.displayTestingTime,
                        style = KabadiwalaTypography.LabelSmall,
                        fontWeight = FontWeight.Bold,
                        color = KabadiwalaColors.Primary
                    )
                }
            }

            if (transaction.items.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    transaction.items.forEach { item ->
                        Surface(
                            shape = KabadiwalaShapes.Small,
                            color = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.35f),
                            contentColor = KabadiwalaColors.Primary
                        ) {
                            Text(
                                text = "${item.localizedMaterialName}: ${item.formattedWeight}",
                                style = KabadiwalaTypography.LabelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onConfirmClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = KabadiwalaColors.Primary,
                    contentColor = KabadiwalaColors.OnPrimary
                ),
                shape = KabadiwalaShapes.Small
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Confirm",
                        tint = KabadiwalaColors.OnPrimary,
                        modifier = Modifier.size(18.dp).padding(end = 8.dp)
                    )
                    Text(
                        text = localizedUi("confirm_handover"),
                        style = KabadiwalaTypography.LabelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OTPVerificationDialog(
    transaction: Transaction,
    otpValue: String,
    isVerifying: Boolean = false,
    onOtpChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    val demoOtp = transaction.otp ?: "5829"

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onCancel),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(KabadiwalaShapes.Large)
                    .clickable(enabled = false, onClick = {}),
                colors = CardDefaults.cardColors(
                    containerColor = KabadiwalaColors.Surface,
                    contentColor = KabadiwalaColors.OnSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                                imageVector = Icons.Filled.Key,
                                contentDescription = null,
                                tint = KabadiwalaColors.Primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = localizedUi("otp_verification"),
                                style = KabadiwalaTypography.TitleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = KabadiwalaColors.OnSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "Enter the 4-digit Handover OTP provided to dealer for ${transaction.localizedRecyclerName} weighbridge testing.",
                        style = KabadiwalaTypography.BodyMedium,
                        color = KabadiwalaColors.OnSurfaceVariant
                    )

                    // 4 Digit Boxes backed by a hidden BasicTextField
                    BasicTextField(
                        value = otpValue,
                        onValueChange = { newValue ->
                            val filtered = newValue.filter { it.isDigit() }.take(4)
                            onOtpChange(filtered)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        decorationBox = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                            ) {
                                for (i in 0 until 4) {
                                    val digit = otpValue.getOrNull(i)?.toString() ?: ""
                                    val isFocused = otpValue.length == i
                                    Surface(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .border(
                                                width = if (isFocused) 2.dp else 1.dp,
                                                color = if (isFocused) KabadiwalaColors.Primary else KabadiwalaColors.OutlineVariant,
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (digit.isNotEmpty()) KabadiwalaColors.PrimaryContainer.copy(alpha = 0.25f) else KabadiwalaColors.SurfaceVariant.copy(alpha = 0.3f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = digit,
                                                style = KabadiwalaTypography.HeadlineMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = KabadiwalaColors.Primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )

                    // Demo OTP Quick-Fill Button
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOtpChange(demoOtp) },
                        shape = RoundedCornerShape(8.dp),
                        color = KabadiwalaColors.SecondaryContainer.copy(alpha = 0.4f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = null,
                                    tint = KabadiwalaColors.Primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Demo OTP: $demoOtp",
                                    style = KabadiwalaTypography.LabelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.Primary
                                )
                            }
                            Text(
                                text = localizedUi("quick_fill_otp"),
                                style = KabadiwalaTypography.LabelSmall,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.Primary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cancel",
                                style = KabadiwalaTypography.LabelLarge,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { onConfirm(otpValue) },
                            modifier = Modifier.weight(1.3f),
                            enabled = otpValue.length == 4 && !isVerifying,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KabadiwalaColors.Primary,
                                contentColor = KabadiwalaColors.OnPrimary,
                                disabledContainerColor = KabadiwalaColors.OnSurfaceVariant.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isVerifying) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = KabadiwalaColors.OnPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = localizedUi("confirm_handover"),
                                    style = KabadiwalaTypography.LabelLarge,
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
        Text(
            text = title,
            style = KabadiwalaTypography.TitleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = KabadiwalaTypography.BodyMedium,
            textAlign = TextAlign.Center,
            color = KabadiwalaColors.OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAction,
            colors = ButtonDefaults.buttonColors(
                containerColor = KabadiwalaColors.Primary,
                contentColor = KabadiwalaColors.OnPrimary
            ),
            shape = KabadiwalaShapes.Small
        ) {
            Text(text = actionText, style = KabadiwalaTypography.LabelLarge)
        }
    }
}