package com.kabadiwalaconnect.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kabadiwalaconnect.data.model.PaymentMethod
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.model.TransactionStatus
import com.kabadiwalaconnect.KabadiwalaApplication
import com.kabadiwalaconnect.data.repository.Result
import com.kabadiwalaconnect.navigation.Screen
import com.kabadiwalaconnect.ui.components.getMaterialIconRes
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography

import com.kabadiwalaconnect.language.LanguageViewModel
import com.kabadiwalaconnect.language.LocalLanguageViewModel
import com.kabadiwalaconnect.language.localizedRecyclerName
import com.kabadiwalaconnect.language.localizedMaterialName
import com.kabadiwalaconnect.ui.components.LanguageTopBarButton

@Composable
fun TransactionDetailScreen(
    transactionId: String,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit = { onNavigate(Screen.Transaction) },
    languageViewModel: LanguageViewModel? = LocalLanguageViewModel.current
) {
    val context = LocalContext.current
    var transaction by remember { mutableStateOf<Transaction?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(transactionId) {
        val repo = KabadiwalaApplication.getInstance().repository
        when (val res = repo.getTransactions()) {
            is Result.Success -> {
                transaction = res.data.find { 
                    it.id.equals(transactionId, ignoreCase = true) || 
                    it.receiptNumber.equals(transactionId, ignoreCase = true) 
                }
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

    val txn = transaction
    if (txn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Transaction not found", style = KabadiwalaTypography.TitleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val (statusBg, statusFg, statusText) = when (txn.status) {
        TransactionStatus.COMPLETED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Completed • Payout Settled")
        TransactionStatus.CONFIRMED -> Triple(Color(0xFFE3F2FD), Color(0xFF1565C0), "Confirmed • Handover in Progress")
        TransactionStatus.PENDING -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Pending • Awaiting Mandi Weighment")
        TransactionStatus.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Cancelled")
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
                    text = "Mandi Scrap Receipt",
                    style = KabadiwalaTypography.TitleLarge,
                    fontWeight = FontWeight.Bold,
                    color = KabadiwalaColors.OnBackground
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LanguageTopBarButton()
                IconButton(onClick = { shareReceipt(context, txn) }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = KabadiwalaColors.Primary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Digital Weighbridge Receipt Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Large,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Receipt Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KABADIWALA CONNECT",
                                style = KabadiwalaTypography.LabelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = KabadiwalaColors.Primary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = txn.receiptNumber,
                                style = KabadiwalaTypography.TitleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusBg
                        ) {
                            Text(
                                text = statusText,
                                style = KabadiwalaTypography.LabelSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusFg,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Divider(color = KabadiwalaColors.OutlineVariant.copy(alpha = 0.5f))

                    // Date and Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Date & Time",
                            style = KabadiwalaTypography.BodySmall,
                            color = KabadiwalaColors.OnSurfaceVariant
                        )
                        Text(
                            text = txn.formattedDate,
                            style = KabadiwalaTypography.BodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Testing / Inspection Slot
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Testing Slot",
                            style = KabadiwalaTypography.BodySmall,
                            color = KabadiwalaColors.OnSurfaceVariant
                        )
                        Text(
                            text = txn.displayTestingTime,
                            style = KabadiwalaTypography.BodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = KabadiwalaColors.Primary
                        )
                    }

                    // Total Payout Hero Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "NET PAYOUT AMOUNT",
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.Primary
                                )
                                Text(
                                    text = txn.formattedAmount,
                                    style = KabadiwalaTypography.HeadlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = KabadiwalaColors.Primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "TOTAL WEIGHT",
                                    style = KabadiwalaTypography.LabelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                                Text(
                                    text = "%.2f kg".format(txn.totalWeight),
                                    style = KabadiwalaTypography.TitleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.OnSurface
                                )
                            }
                        }
                    }
                }
            }

            // Handover Security OTP Banner (if pending or confirmed)
            if (txn.status == TransactionStatus.CONFIRMED || txn.status == TransactionStatus.PENDING) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = KabadiwalaShapes.Medium,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, KabadiwalaColors.Primary)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.Key, contentDescription = null, tint = KabadiwalaColors.Primary, modifier = Modifier.size(18.dp))
                            Text(
                                text = "HANDOVER SECURITY OTP",
                                style = KabadiwalaTypography.LabelMedium,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.Primary
                            )
                        }

                        // OTP Boxes
                        val otpCode = txn.otp ?: "5829"
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            otpCode.forEach { char ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = char.toString(),
                                            style = KabadiwalaTypography.HeadlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = KabadiwalaColors.Primary
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Share this 4-digit OTP with the dealer driver only after weighbridge inspection.",
                            style = KabadiwalaTypography.BodySmall,
                            textAlign = TextAlign.Center,
                            color = KabadiwalaColors.OnSurfaceVariant
                        )
                    }
                }
            }

            // Dealer / Recycler Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Medium,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(text = "Scrap Dealer / Recycler", style = KabadiwalaTypography.TitleMedium, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = txn.localizedRecyclerName, style = KabadiwalaTypography.BodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "Verified Mandi Yard Licensee", style = KabadiwalaTypography.BodySmall, color = KabadiwalaColors.Success)
                        }

                        OutlinedButton(
                            onClick = { onNavigate(Screen.RecyclerDetail(txn.recyclerId)) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("View Yard", style = KabadiwalaTypography.LabelSmall, color = KabadiwalaColors.Primary)
                        }
                    }
                }
            }

            // Itemized Scrap Breakdown Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Medium,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Itemized Scrap Weighment Breakdown",
                        style = KabadiwalaTypography.TitleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    txn.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, KabadiwalaColors.OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.localizedMaterialName, style = KabadiwalaTypography.BodyMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${item.formattedWeight} @ ₹%.2f/kg".format(item.pricePerKg),
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                            }
                            Text(
                                text = item.formattedAmount,
                                style = KabadiwalaTypography.TitleSmall,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.Primary
                            )
                        }
                    }

                    Divider(color = KabadiwalaColors.OutlineVariant.copy(alpha = 0.5f))

                    // Tare & Net Calculations
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gross Lot Weight", style = KabadiwalaTypography.BodySmall, color = KabadiwalaColors.OnSurfaceVariant)
                            Text("%.2f kg".format(txn.grossWeight), style = KabadiwalaTypography.BodyMedium)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tare Container Deduction", style = KabadiwalaTypography.BodySmall, color = KabadiwalaColors.OnSurfaceVariant)
                            Text("-%.2f kg".format(txn.tareWeight), style = KabadiwalaTypography.BodyMedium, color = Color(0xFFC62828))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Net Payable Scrap Weight", style = KabadiwalaTypography.BodyMedium, fontWeight = FontWeight.Bold)
                            Text("%.2f kg".format(txn.totalWeight), style = KabadiwalaTypography.BodyMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Payment & Mandi Settlement Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KabadiwalaShapes.Medium,
                colors = CardDefaults.cardColors(containerColor = KabadiwalaColors.Surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Payment & Settlement", style = KabadiwalaTypography.TitleMedium, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment Method", style = KabadiwalaTypography.BodySmall, color = KabadiwalaColors.OnSurfaceVariant)
                        Text(txn.paymentMethod.name, style = KabadiwalaTypography.BodyMedium, fontWeight = FontWeight.SemiBold)
                    }

                    if (txn.paymentReference.isNotBlank()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transaction Ref", style = KabadiwalaTypography.BodySmall, color = KabadiwalaColors.OnSurfaceVariant)
                            Text(txn.paymentReference, style = KabadiwalaTypography.BodySmall, fontFamily = FontFamily.Monospace)
                        }
                    }

                    if (txn.weighbridgeSlipNo.isNotBlank()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Weighbridge Slip", style = KabadiwalaTypography.BodySmall, color = KabadiwalaColors.OnSurfaceVariant)
                            Text(txn.weighbridgeSlipNo, style = KabadiwalaTypography.BodySmall, fontFamily = FontFamily.Monospace)
                        }
                    }

                    if (txn.notes.isNotBlank()) {
                        Text(
                            text = "Note: ${txn.notes}",
                            style = KabadiwalaTypography.BodySmall,
                            color = KabadiwalaColors.OnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Bottom CTA Bar
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
                OutlinedButton(
                    onClick = { shareReceipt(context, txn) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = KabadiwalaColors.Primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Slip", color = KabadiwalaColors.Primary)
                }

                Button(
                    onClick = {
                        val firstItem = txn.items.firstOrNull()
                        if (firstItem != null) {
                            onNavigate(Screen.MaterialEntryWithId(firstItem.materialId))
                        } else {
                            onNavigate(Screen.MaterialEntry)
                        }
                    },
                    modifier = Modifier.weight(1.3f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = KabadiwalaColors.OnPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sell Again", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun shareReceipt(context: Context, txn: Transaction) {
    try {
        val itemsSummary = txn.items.joinToString("\n") {
            "- ${it.materialName}: ${it.formattedWeight} @ ₹${"%.0f".format(it.pricePerKg)}/kg = ${it.formattedAmount}"
        }
        val text = """
            ==============================
            KABADIWALA CONNECT - MANDI SLIP
            ==============================
            Receipt No: ${txn.receiptNumber}
            Date: ${txn.formattedDate}
            Dealer: ${txn.recyclerName}
            Status: ${txn.status.name}
            
            SCRAP BREAKDOWN:
            $itemsSummary
            ------------------------------
            Total Weight: %.2f kg
            Total Payout: %s
            Payment Mode: %s
            Ref: %s
            ==============================
        """.trimIndent().format(txn.totalWeight, txn.formattedAmount, txn.paymentMethod.name, txn.paymentReference)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Mandi Scrap Receipt - ${txn.receiptNumber}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Scrap Receipt"))
    } catch (e: Exception) {
        Toast.makeText(context, "Could not share receipt: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}
