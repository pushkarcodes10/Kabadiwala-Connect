package com.kabadiwalaconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    primaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFFF8F00),
    secondaryContainer = Color(0xFFFFF3E0),
    tertiary = Color(0xFF1565C0),
    tertiaryContainer = Color(0xFFBBDEFB),
    error = Color(0xFFC62828),
    errorContainer = Color(0xFFFFCDD2),
    background = Color(0xFFF1F8E9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8F5E9),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFF1B5E20),
    onSecondary = Color(0xFFFFFFFF),
    onSecondaryContainer = Color(0xFFF57F17),
    onTertiary = Color(0xFFFFFFFF),
    onTertiaryContainer = Color(0xFF0D47A1),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFFB71C1C),
    onBackground = Color(0xFF1B5E20),
    onSurface = Color(0xFF1B5E20),
    onSurfaceVariant = Color(0xFF424242),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFBDBDBD),
    inverseSurface = Color(0xFF313131),
    inverseOnSurface = Color(0xFFF1F8E9),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFF2E7D32),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    primaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFFFFB74D),
    secondaryContainer = Color(0xFFF57F17),
    tertiary = Color(0xFF64B5F6),
    tertiaryContainer = Color(0xFF0D47A1),
    error = Color(0xFFEF9A9A),
    errorContainer = Color(0xFFB71C1C),
    background = Color(0xFF1B5E20),
    surface = Color(0xFF2E7D32),
    surfaceVariant = Color(0xFF3A3A3A),
    onPrimary = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFA5D6A7),
    onSecondary = Color(0xFFF57F17),
    onSecondaryContainer = Color(0xFFFFB74D),
    onTertiary = Color(0xFF0D47A1),
    onTertiaryContainer = Color(0xFF64B5F6),
    onError = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFEF9A9A),
    onBackground = Color(0xFFF1F8E9),
    onSurface = Color(0xFFF1F8E9),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFF757575),
    inverseSurface = Color(0xFFF1F8E9),
    inverseOnSurface = Color(0xFF1B5E20),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFA5D6A7),
)

@Composable
fun Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}