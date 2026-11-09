package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.domain.model.AccentPalette
import com.example.domain.model.AppThemeMode

private val DefaultLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

private val DefaultDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = Color(0xFFF8FAFC),
    surface = DarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SmartAgeZodiacTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    accentPalette: AccentPalette = AccentPalette.DEFAULT,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val baseScheme = if (isDark) DefaultDarkColorScheme else DefaultLightColorScheme

    val colorScheme = when (accentPalette) {
        AccentPalette.DEFAULT -> baseScheme
        AccentPalette.CELESTIAL_GOLD -> if (isDark) {
            baseScheme.copy(
                primary = GoldDarkPrimary,
                onPrimary = Color(0xFF451A03),
                primaryContainer = GoldDarkPrimaryContainer,
                onPrimaryContainer = Color(0xFFFEF3C7),
                secondary = GoldDarkSecondary
            )
        } else {
            baseScheme.copy(
                primary = GoldLightPrimary,
                onPrimary = Color.White,
                primaryContainer = GoldLightPrimaryContainer,
                onPrimaryContainer = Color(0xFF451A03),
                secondary = GoldLightSecondary
            )
        }
        AccentPalette.COSMIC_VIOLET -> if (isDark) {
            baseScheme.copy(
                primary = VioletDarkPrimary,
                onPrimary = Color(0xFF2E1065),
                primaryContainer = VioletDarkPrimaryContainer,
                onPrimaryContainer = Color(0xFFF3E8FF),
                secondary = VioletDarkSecondary
            )
        } else {
            baseScheme.copy(
                primary = VioletLightPrimary,
                onPrimary = Color.White,
                primaryContainer = VioletLightPrimaryContainer,
                onPrimaryContainer = Color(0xFF2E1065),
                secondary = VioletLightSecondary
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    SmartAgeZodiacTheme(
        themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
        accentPalette = AccentPalette.DEFAULT,
        content = content
    )
}
