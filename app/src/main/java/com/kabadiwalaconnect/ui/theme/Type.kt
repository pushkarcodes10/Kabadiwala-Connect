package com.kabadiwalaconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle

@Composable
fun KabadiwalaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    Theme(darkTheme, content)
}

object KabadiwalaColors {
    val Primary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
    val PrimaryContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
    val OnPrimaryContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer
    val Secondary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondary
    val SecondaryContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondaryContainer
    val Tertiary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary
    val Background: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
    val Surface: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface
    val SurfaceVariant: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant
    val Error: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error
    val ErrorContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.errorContainer
    val OnPrimary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary
    val OnSecondary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondary
    val OnSecondaryContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSecondaryContainer
    val OnBackground: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onBackground
    val OnSurface: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
    val OnSurfaceVariant: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val Outline: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline
    val OutlineVariant: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outlineVariant
    val Success: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
    val Warning: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondary
    val Info: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary
}

object KabadiwalaTypography {
    val DisplayLarge: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.displayLarge
    val DisplayMedium: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.displayMedium
    val DisplaySmall: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.displaySmall
    val HeadlineLarge: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.headlineLarge
    val HeadlineMedium: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.headlineMedium
    val HeadlineSmall: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.headlineSmall
    val TitleLarge: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.titleLarge
    val TitleMedium: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.titleMedium
    val TitleSmall: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.titleSmall
    val BodyLarge: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.bodyLarge
    val BodyMedium: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.bodyMedium
    val BodySmall: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.bodySmall
    val LabelLarge: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.labelLarge
    val LabelMedium: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.labelMedium
    val LabelSmall: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.labelSmall
}

object KabadiwalaShapes {
    val ExtraSmall: Shape @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.extraSmall
    val Small: Shape @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.small
    val Medium: Shape @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.medium
    val Large: Shape @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.large
    val ExtraLarge: Shape @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.extraLarge
}