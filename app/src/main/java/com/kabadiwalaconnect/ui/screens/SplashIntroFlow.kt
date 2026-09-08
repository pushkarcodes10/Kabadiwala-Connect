package com.kabadiwalaconnect.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kabadiwalaconnect.R
import com.kabadiwalaconnect.ui.theme.KabadiwalaColors
import com.kabadiwalaconnect.ui.theme.KabadiwalaShapes
import com.kabadiwalaconnect.ui.theme.KabadiwalaTypography

@Composable
fun SplashIntroFlow(
    onComplete: () -> Unit
) {
    var currentScreen by remember { mutableStateOf(0) }
    // 0 = Splash, 1 = Intro 1, 2 = Intro 2, 3 = Intro 3, 4 = Logo

    val primary = KabadiwalaColors.Primary
    val primaryContainer = KabadiwalaColors.PrimaryContainer
    val secondary = KabadiwalaColors.Secondary
    val secondaryContainer = KabadiwalaColors.SecondaryContainer
    val info = KabadiwalaColors.Info

    val screens = remember(primary, primaryContainer, secondary, secondaryContainer, info) {
        listOf(
            SplashData(
                title = "Kabadiwala Connect",
                subtitle = "Connecting You with Recyclers",
                showProgress = true
            ),
            SplashData(
                title = "Find Recyclers Nearby",
                subtitle = "Discover trusted kabadiwalas in your area for easy pickup",
                icon = Icons.Filled.LocationOn,
                backgroundColor = primaryContainer,
                iconColor = primary
            ),
            SplashData(
                title = "Live Market Prices",
                subtitle = "Get real-time rates for paper, plastic, metal, and more",
                icon = Icons.Filled.TrendingUp,
                backgroundColor = secondaryContainer,
                iconColor = secondary
            ),
            SplashData(
                title = "Track Your Earnings",
                subtitle = "Maintain digital khata and track all transactions",
                icon = Icons.Filled.AccountBalanceWallet,
                backgroundColor = Color(0xFFBBDEFB),
                iconColor = info
            ),
            SplashData(
                title = "Kabadiwala Connect",
                subtitle = "Your trusted scrap recycling partner",
                icon = Icons.Filled.Shield,
                backgroundColor = primaryContainer,
                iconColor = primary,
                isLast = true
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val data = screens[currentScreen]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (data.showProgress) {
                // Splash screen
                Icon(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Kabadiwala Connect Logo",
                    tint = KabadiwalaColors.Primary,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = data.title,
                    style = KabadiwalaTypography.HeadlineMedium,
                    color = KabadiwalaColors.Primary
                )
                Text(
                    text = data.subtitle,
                    style = KabadiwalaTypography.BodyLarge,
                    color = KabadiwalaColors.OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    color = KabadiwalaColors.Primary,
                    trackColor = KabadiwalaColors.PrimaryContainer
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { currentScreen = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = KabadiwalaColors.OnPrimary
                    )
                ) {
                    Text(text = "Get Started", style = KabadiwalaTypography.LabelLarge)
                }
            } else if (data.isLast) {
                // Logo screen
                Icon(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Kabadiwala Connect Logo",
                    tint = KabadiwalaColors.Primary,
                    modifier = Modifier.size(140.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = data.title,
                    style = KabadiwalaTypography.DisplaySmall,
                    color = KabadiwalaColors.Primary
                )
                Text(
                    text = data.subtitle,
                    style = KabadiwalaTypography.BodyLarge,
                    color = KabadiwalaColors.OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KabadiwalaColors.Primary,
                        contentColor = KabadiwalaColors.OnPrimary
                    )
                ) {
                    Text(text = "Continue to App", style = KabadiwalaTypography.LabelLarge)
                }
            } else {
                // Intro screens
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(data.backgroundColor, KabadiwalaShapes.ExtraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    data.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = data.title,
                            tint = data.iconColor,
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = data.title,
                    style = KabadiwalaTypography.HeadlineMedium,
                    textAlign = TextAlign.Center,
                    color = KabadiwalaColors.OnBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = data.subtitle,
                    style = KabadiwalaTypography.BodyLarge,
                    textAlign = TextAlign.Center,
                    color = KabadiwalaColors.OnSurfaceVariant,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (index in 1 until screens.size) {
                        val isSelected = (index == currentScreen)
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 24.dp else 8.dp, 8.dp)
                                .clip(KabadiwalaShapes.ExtraSmall)
                                .background(if (isSelected) KabadiwalaColors.Primary else KabadiwalaColors.OutlineVariant)
                                .animateContentSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { currentScreen = screens.size - 1 }) {
                        Text(text = "Skip", style = KabadiwalaTypography.LabelLarge, color = KabadiwalaColors.OnSurfaceVariant)
                    }
                    Button(
                        onClick = {
                            if (currentScreen < screens.size - 1) {
                                currentScreen++
                            } else {
                                onComplete()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KabadiwalaColors.Primary,
                            contentColor = KabadiwalaColors.OnPrimary
                        )
                    ) {
                        Text(text = if (currentScreen == screens.size - 2) "Get Started" else "Next", style = KabadiwalaTypography.LabelLarge)
                    }
                }
            }
        }
    }
}

data class SplashData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector? = null,
    val backgroundColor: Color = Color.Transparent,
    val iconColor: Color = Color.Unspecified,
    val showProgress: Boolean = false,
    val isLast: Boolean = false
)