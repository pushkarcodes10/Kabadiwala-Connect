package com.kabadiwalaconnect.ui.components

import android.content.Intent
import android.net.Uri
import com.kabadiwalaconnect.language.localizedUi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography

@Composable
fun ScrapSaleSuccessDialog(
    transaction: Transaction,
    recyclerPhone: String? = null,
    onDismiss: () -> Unit,
    onViewReceipt: (String) -> Unit,
    onGoHome: () -> Unit
) {
    val context = LocalContext.current
    val item = transaction.items.firstOrNull()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp)),
            color = KabadiwalaColors.Surface,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = KabadiwalaColors.OnSurfaceVariant
                        )
                    }
                }

                // Celebratory Badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(KabadiwalaColors.Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Headers
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = localizedUi("pickup_booked_success"),
                        style = KabadiwalaTypography.HeadlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = KabadiwalaColors.OnSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = localizedUi("pickup_order_confirmed_sub"),
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // Order Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = localizedUi("order_id_label"),
                                style = KabadiwalaTypography.LabelSmall,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                            Text(
                                text = "#${transaction.receiptNumber}",
                                style = KabadiwalaTypography.LabelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = KabadiwalaColors.Primary
                            )
                        }

                        HorizontalDivider(color = Color(0xFFDCEDC8), thickness = 1.dp)

                        // Recycler Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = localizedUi("buyer_yard_label"),
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = transaction.recyclerName,
                                    style = KabadiwalaTypography.BodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = KabadiwalaColors.OnSurface
                                )
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = KabadiwalaColors.Success,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Scrap Material & Weight
                        if (item != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = localizedUi("material_and_weight_label"),
                                    style = KabadiwalaTypography.BodySmall,
                                    color = KabadiwalaColors.OnSurfaceVariant
                                )
                                Text(
                                    text = "${item.materialName} (${"%.1f".format(item.weightKg)} kg)",
                                    style = KabadiwalaTypography.BodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = KabadiwalaColors.OnSurface
                                )
                            }
                        }

                        // Estimated Payout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = localizedUi("item_est_price"),
                                style = KabadiwalaTypography.BodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = KabadiwalaColors.OnSurface
                            )
                            Text(
                                text = transaction.formattedAmount,
                                style = KabadiwalaTypography.TitleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }

                // Security OTP Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = localizedUi("otp_verification_title"),
                                style = KabadiwalaTypography.LabelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = transaction.otp ?: "5842",
                                style = KabadiwalaTypography.HeadlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE65100),
                                letterSpacing = 6.sp,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = localizedUi("otp_share_note"),
                            style = KabadiwalaTypography.BodySmall,
                            color = Color(0xFF795548),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick Recycler Contact (if phone available)
                if (!recyclerPhone.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$recyclerPhone"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = "Call", tint = KabadiwalaColors.Primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localizedUi("call_dealer"), style = KabadiwalaTypography.LabelMedium, color = KabadiwalaColors.Primary)
                        }

                        Button(
                            onClick = {
                                try {
                                    val clean = recyclerPhone.replace(Regex("[^0-9]"), "")
                                    val msg = Uri.encode("Hello, I have booked a scrap pickup with order #${transaction.receiptNumber}. When will the pickup agent arrive?")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=$msg"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {}
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(localizedUi("whatsapp"), style = KabadiwalaTypography.LabelMedium)
                        }
                    }
                }

                // Primary Action: View Receipt
                Button(
                    onClick = {
                        onDismiss()
                        onViewReceipt(transaction.id)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = localizedUi("view_digital_slip"),
                        style = KabadiwalaTypography.LabelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Secondary Action: Back to Home
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onGoHome()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = localizedUi("back_to_home"),
                        style = KabadiwalaTypography.LabelLarge,
                        color = KabadiwalaColors.Primary
                    )
                }
            }
        }
    }
}
