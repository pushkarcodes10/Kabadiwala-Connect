package com.kabadiwalaconnect.ui.components

import com.kabadiwalaconnect.data.model.MaterialCategory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.CheckCircle
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kabadiwalaconnect.data.model.Material
import com.kabadiwalaconnect.data.model.MarketPrice
import com.kabadiwalaconnect.data.model.Recycler
import com.kabadiwalaconnect.data.model.SafetyTip
import com.kabadiwalaconnect.data.model.Transaction
import com.kabadiwalaconnect.data.model.KhataEntry
import com.kabadiwalaconnect.data.model.EmergencyContact
import com.kabadiwalaconnect.data.model.PaymentStatus
import com.kabadiwalaconnect.data.model.SafetyCategory
import com.kabadiwalaconnect.data.model.PriceTrend
import com.kabadiwalaconnect.data.model.TransactionStatus
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kabadiwalaconnect.language.localizedUi
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography
import com.kabadiwalaconnect.language.localizedName
import com.kabadiwalaconnect.language.localizedFacilityType
import com.kabadiwalaconnect.language.localizedMaterialName
import com.kabadiwalaconnect.language.localizedRecyclerName

@Composable
fun MaterialImage(
    material: Material,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val fallbackRes = material.getImageDrawableRes()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(KabadiwalaColors.SurfaceVariant.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        if (material.imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(material.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = material.name,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = fallbackRes),
                error = painterResource(id = fallbackRes)
            )
        } else {
            androidx.compose.foundation.Image(
                painter = painterResource(id = fallbackRes),
                contentDescription = material.name,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun MaterialCard(
    material: Material,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPrice: Boolean = true,
    isGrid: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isGrid) {
            // E-Commerce style product card for 2-column home view
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                ) {
                    MaterialImage(
                        material = material,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = KabadiwalaColors.Primary.copy(alpha = 0.90f),
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = material.category.localizedName,
                            style = KabadiwalaTypography.LabelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = material.localizedName,
                    style = KabadiwalaTypography.TitleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (showPrice) {
                    Text(
                        text = material.rateRange,
                        style = KabadiwalaTypography.LabelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (material.basePricePerKg > 0) KabadiwalaColors.Primary else KabadiwalaColors.OnSurfaceVariant
                    )
                }
            }
        } else {
            // Horizontal list item with prominent product photo
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MaterialImage(
                    material = material,
                    modifier = Modifier.size(56.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = material.localizedName,
                        style = KabadiwalaTypography.TitleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (material.acceptedTypes.isNotEmpty()) material.acceptedTypes.take(3).joinToString(", ") else material.description,
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (showPrice) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = material.rateRange,
                            style = KabadiwalaTypography.TitleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (material.basePricePerKg > 0) KabadiwalaColors.Primary else KabadiwalaColors.OnSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = material.category.localizedName,
                                style = KabadiwalaTypography.LabelSmall,
                                color = KabadiwalaColors.Primary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = KabadiwalaColors.PrimaryContainer,
    iconColor: Color = KabadiwalaColors.Primary
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = iconColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                style = KabadiwalaTypography.LabelLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(KabadiwalaShapes.Medium)
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = transaction.formattedAmount,
                        style = KabadiwalaTypography.TitleMedium,
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
                    .padding(top = 8.dp),
                shape = KabadiwalaShapes.Small,
                color = KabadiwalaColors.PrimaryContainer.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Testing Slot",
                        tint = KabadiwalaColors.Primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = transaction.displayTestingTime,
                        style = KabadiwalaTypography.LabelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = KabadiwalaColors.Primary
                    )
                }
            }

            if (transaction.items.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    transaction.items.forEach { item ->
                        Surface(
                            shape = KabadiwalaShapes.Small,
                            color = KabadiwalaColors.SurfaceVariant.copy(alpha = 0.55f)
                        ) {
                            Text(
                                text = "${item.localizedMaterialName}: ${item.formattedWeight}",
                                style = KabadiwalaTypography.BodySmall,
                                color = KabadiwalaColors.OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            if (onConfirmClick != null && (transaction.status == TransactionStatus.PENDING || transaction.status == TransactionStatus.CONFIRMED)) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(10.dp))
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
                            contentDescription = null,
                            modifier = Modifier.size(16.dp).padding(end = 6.dp)
                        )
                        Text(
                            text = localizedUi("confirm_handover"),
                            style = KabadiwalaTypography.LabelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: TransactionStatus) {
    val (color, textKey) = when (status) {
        TransactionStatus.PENDING -> KabadiwalaColors.Warning to "status_pending"
        TransactionStatus.CONFIRMED -> KabadiwalaColors.Info to "status_confirmed"
        TransactionStatus.COMPLETED -> KabadiwalaColors.Success to "status_completed"
        TransactionStatus.CANCELLED -> KabadiwalaColors.Error to "status_cancelled"
    }
    Surface(
        modifier = Modifier.padding(top = 4.dp),
        shape = KabadiwalaShapes.Small,
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = localizedUi(textKey),
            style = KabadiwalaTypography.LabelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RecyclerCard(
    recycler: Recycler,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBookPickup: ((Recycler) -> Unit)? = null
) {
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium)
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Dedicated Category Badge
            recycler.dedicatedCategory?.let { category ->
                val (badgeText, badgeBg, badgeTextColor) = when (category) {
                    MaterialCategory.ELECTRONICS -> Triple("⚡ Dedicated E-Waste Recycler", Color(0xFFE8F5E9), Color(0xFF1B5E20))
                    MaterialCategory.METAL -> Triple("🔩 Dedicated Metal Scrap Buyer", Color(0xFFFFF3E0), Color(0xFFE65100))
                    MaterialCategory.PAPER -> Triple("📰 Dedicated Paper & Raddi Depot", Color(0xFFE3F2FD), Color(0xFF0D47A1))
                    MaterialCategory.PLASTIC -> Triple("🧴 Dedicated Plastic Processing Yard", Color(0xFFEDE7F6), Color(0xFF4A148C))
                    MaterialCategory.GLASS -> Triple("🍾 Dedicated Glass & Cullet Buyer", Color(0xFFE0F7FA), Color(0xFF006064))
                    MaterialCategory.TEXTILE -> Triple("👕 Dedicated Textile & Fabric Recycler", Color(0xFFFCE4EC), Color(0xFF880E4F))
                    MaterialCategory.RUBBER -> Triple("🛞 Dedicated Rubber & Tyre Reclaimer", Color(0xFFEFEBE9), Color(0xFF3E2723))
                    MaterialCategory.OTHER -> Triple("📦 Dedicated Refuse & Multi-Scrap Hub", Color(0xFFF1F8E9), Color(0xFF33691E))
                    else -> Triple("♻️ Certified Scrap Recycler", Color(0xFFE8F5E9), Color(0xFF2E7D32))
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeBg,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = KabadiwalaTypography.LabelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                        text = recycler.address,
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = KabadiwalaColors.Secondary,
                            modifier = Modifier.size(14.dp)
                        )
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

            // Status & Facility Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (recycler.isOpenNow) KabadiwalaColors.PrimaryContainer else KabadiwalaColors.SurfaceVariant
                ) {
                    Text(
                        text = if (recycler.isOpenNow) localizedUi("open_now") else localizedUi("closed"),
                        style = KabadiwalaTypography.LabelSmall,
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

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))

            // Material chips - FlowRow wraps to new line if space is insufficient
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                recycler.acceptedMaterials.take(3).forEach { materialId ->
                    Material.getById(materialId)?.let { material ->
                        Surface(
                            shape = KabadiwalaShapes.Small,
                            color = KabadiwalaColors.PrimaryContainer,
                            contentColor = KabadiwalaColors.Primary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                MaterialImage(
                                    material = material,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = material.localizedName,
                                    style = KabadiwalaTypography.LabelSmall
                                )
                            }
                        }
                    }
                }
                if (recycler.acceptedMaterials.size > 3) {
                    Surface(
                        shape = KabadiwalaShapes.Small,
                        color = KabadiwalaColors.SurfaceVariant,
                        contentColor = KabadiwalaColors.OnSurfaceVariant
                    ) {
                        Text(
                            text = "+${recycler.acceptedMaterials.size - 3} more",
                            style = KabadiwalaTypography.LabelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Recycler Selling Option
            if (onBookPickup != null) {
                val pending = com.kabadiwalaconnect.data.model.PendingPickupStore.pendingPickup
                val pendingCart = com.kabadiwalaconnect.data.model.PendingPickupStore.pendingCartItems
                val btnText = if (pendingCart.size > 1 && pending != null) {
                    localizedUi("sell_items_to_recycler", pendingCart.size, pending.estimatedPrice)
                } else if (pending != null) {
                    localizedUi("sell_scrap_to_recycler", pending.estimatedPrice)
                } else {
                    localizedUi("sell_scrap_to_recycler_simple")
                }
                androidx.compose.material3.Button(
                    onClick = { onBookPickup(recycler) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = btnText,
                        style = KabadiwalaTypography.LabelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Contact Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${recycler.phone}"))
                            context.startActivity(intent)
                        } catch (e: Exception) {}
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Call, contentDescription = "Call", tint = KabadiwalaColors.Primary, modifier = Modifier.size(14.dp))
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    Text(localizedUi("call"), style = KabadiwalaTypography.LabelSmall, color = KabadiwalaColors.Primary)
                }

                OutlinedButton(
                    onClick = {
                        try {
                            val clean = (if (recycler.whatsappNumber.isNotBlank()) recycler.whatsappNumber else recycler.phone).replace(Regex("[^0-9]"), "")
                            val msg = Uri.encode("Hello ${recycler.name}, I am a scrap collector. What are your rates today?")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$clean&text=$msg"))
                            context.startActivity(intent)
                        } catch (e: Exception) {}
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(14.dp))
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    Text(localizedUi("whatsapp"), style = KabadiwalaTypography.LabelSmall, color = Color(0xFF1B5E20))
                }

                OutlinedButton(
                    onClick = {
                        try {
                            val uri = Uri.parse("geo:${recycler.latitude},${recycler.longitude}?q=${recycler.latitude},${recycler.longitude}(${Uri.encode(recycler.name)})")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } catch (e: Exception) {}
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Navigation, contentDescription = "Route", tint = KabadiwalaColors.Primary, modifier = Modifier.size(14.dp))
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(4.dp))
                    Text(localizedUi("route"), style = KabadiwalaTypography.LabelSmall, color = KabadiwalaColors.Primary)
                }
            }
        }
    }
}

@Composable
fun MarketPriceCard(
    price: MarketPrice,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium),
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    text = price.localizedMaterialName,
                    style = KabadiwalaTypography.TitleMedium
                )
                Text(
                    text = "Updated ${java.text.SimpleDateFormat("HH:mm").format(java.util.Date(price.lastUpdated.toEpochMilli()))}",
                    style = KabadiwalaTypography.BodySmall,
                    color = KabadiwalaColors.OnSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = price.formattedPrice,
                    style = KabadiwalaTypography.HeadlineSmall,
                    color = KabadiwalaColors.Primary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (price.trend) {
                            PriceTrend.UP -> androidx.compose.material.icons.Icons.Filled.TrendingUp
                            PriceTrend.DOWN -> androidx.compose.material.icons.Icons.Filled.TrendingDown
                            else -> androidx.compose.material.icons.Icons.Filled.Remove
                        },
                        contentDescription = price.trend.name,
                        tint = Color(price.trendColor),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = price.formattedChange,
                        style = KabadiwalaTypography.LabelMedium,
                        color = Color(price.trendColor)
                    )
                }
            }
        }
    }
}

@Composable
fun SafetyTipCard(
    tip: SafetyTip,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(KabadiwalaShapes.Small),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = getSafetyIconRes(tip.iconName)),
                    contentDescription = tip.title,
                    tint = KabadiwalaColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = tip.title,
                    style = KabadiwalaTypography.TitleMedium
                )
                Text(
                    text = tip.description,
                    style = KabadiwalaTypography.BodyMedium,
                    color = KabadiwalaColors.OnSurfaceVariant
                )
                Surface(
                    shape = KabadiwalaShapes.Small,
                    color = getCategoryColor(tip.category).copy(alpha = 0.15f),
                    contentColor = getCategoryColor(tip.category)
                ) {
                    Text(
                        text = tip.category.name.replace("_", " "),
                        style = KabadiwalaTypography.LabelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyContactCard(
    contact: EmergencyContact,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    text = contact.name,
                    style = KabadiwalaTypography.TitleMedium
                )
                Text(
                    text = contact.description,
                    style = KabadiwalaTypography.BodySmall,
                    color = KabadiwalaColors.OnSurfaceVariant
                )
            }
            androidx.compose.material3.Button(
                onClick = { /* TODO: Make call */ },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = KabadiwalaColors.Primary,
                    contentColor = KabadiwalaColors.OnPrimary
                )
            ) {
                Text(text = "Call", style = KabadiwalaTypography.LabelLarge)
            }
        }
    }
}

@Composable
fun KhataEntryCard(
    entry: KhataEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium)
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = entry.localizedRecyclerName,
                        style = KabadiwalaTypography.TitleMedium
                    )
                    Text(
                        text = "${entry.localizedMaterialName} • ${entry.formattedWeight} @ ₹${"%.0f".format(entry.ratePerKg)}/kg",
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.OnSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = entry.formattedAmount,
                        style = KabadiwalaTypography.TitleMedium,
                        color = KabadiwalaColors.Primary
                    )
                    PaymentStatusChip(status = entry.paymentStatus)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.formattedDate,
                    style = KabadiwalaTypography.BodySmall,
                    color = KabadiwalaColors.OnSurfaceVariant
                )
                if (entry.paymentDate != null) {
                    Text(
                        text = "Paid: ${java.text.SimpleDateFormat("dd MMM").format(java.util.Date(entry.paymentDate!!.toEpochMilli()))}",
                        style = KabadiwalaTypography.BodySmall,
                        color = KabadiwalaColors.Success
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentStatusChip(status: PaymentStatus) {
    val (color, textKey) = when (status) {
        PaymentStatus.PENDING -> KabadiwalaColors.Warning to "status_pending"
        PaymentStatus.PARTIAL -> KabadiwalaColors.Info to "status_partial"
        PaymentStatus.PAID -> KabadiwalaColors.Success to "status_paid"
        PaymentStatus.OVERDUE -> KabadiwalaColors.Error to "status_overdue"
    }
    Surface(
        modifier = Modifier.padding(top = 4.dp),
        shape = KabadiwalaShapes.Small,
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = localizedUi(textKey),
            style = KabadiwalaTypography.LabelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EarningsSummaryCard(
    title: String,
    amount: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(KabadiwalaShapes.Medium),
        colors = CardDefaults.cardColors(
            containerColor = KabadiwalaColors.Surface,
            contentColor = KabadiwalaColors.OnSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = subtitle,
                    style = KabadiwalaTypography.LabelSmall,
                    color = KabadiwalaColors.OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = KabadiwalaTypography.LabelMedium,
                    color = KabadiwalaColors.OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = amount,
                    style = KabadiwalaTypography.TitleMedium,
                    fontWeight = FontWeight.Bold,
                    color = KabadiwalaColors.Primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

fun getMaterialIconRes(iconName: String): Int {
    return when (iconName) {
        "paper" -> com.kabadiwalaconnect.R.drawable.ic_paper
        "plastic" -> com.kabadiwalaconnect.R.drawable.ic_plastic
        "metal" -> com.kabadiwalaconnect.R.drawable.ic_metal
        "electronics" -> com.kabadiwalaconnect.R.drawable.ic_electronics
        "glass" -> com.kabadiwalaconnect.R.drawable.ic_glass
        "textile" -> com.kabadiwalaconnect.R.drawable.ic_textile
        "battery" -> com.kabadiwalaconnect.R.drawable.ic_battery
        "hazardous" -> com.kabadiwalaconnect.R.drawable.ic_hazardous
        else -> com.kabadiwalaconnect.R.drawable.ic_logo
    }
}

fun getSafetyIconRes(iconName: String): Int {
    return when (iconName) {
        "gloves" -> com.kabadiwalaconnect.R.drawable.ic_gloves
        "boots" -> com.kabadiwalaconnect.R.drawable.ic_boots
        "segregate" -> com.kabadiwalaconnect.R.drawable.ic_segregate
        "battery" -> com.kabadiwalaconnect.R.drawable.ic_battery
        "sharp" -> com.kabadiwalaconnect.R.drawable.ic_sharp
        "hazardous" -> com.kabadiwalaconnect.R.drawable.ic_hazardous
        "no_fire" -> com.kabadiwalaconnect.R.drawable.ic_no_fire
        "verified" -> com.kabadiwalaconnect.R.drawable.ic_verified
        "receipt" -> com.kabadiwalaconnect.R.drawable.ic_receipt
        "digital_payment" -> com.kabadiwalaconnect.R.drawable.ic_digital_payment
        "clean" -> com.kabadiwalaconnect.R.drawable.ic_clean
        "storage" -> com.kabadiwalaconnect.R.drawable.ic_storage
        else -> com.kabadiwalaconnect.R.drawable.ic_logo
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