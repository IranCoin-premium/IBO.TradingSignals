package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun createColorSchemeForMode(mode: LuxuryThemeMode): androidx.compose.material3.ColorScheme {
    return darkColorScheme(
        primary = mode.accentPrimary,
        onPrimary = if (mode == LuxuryThemeMode.LIGHT_LUXURY) Color.White else Color.Black,
        primaryContainer = mode.accentPrimary.copy(alpha = 0.25f),
        onPrimaryContainer = mode.accentPrimary,
        secondary = mode.accentSecondary,
        onSecondary = Color.Black,
        secondaryContainer = mode.cardBg,
        onSecondaryContainer = mode.accentSecondary,
        tertiary = AmberGold,
        onTertiary = Color.Black,
        tertiaryContainer = SlateDark800,
        onTertiaryContainer = GoldGlow,
        background = mode.bgPrimary,
        onBackground = if (mode == LuxuryThemeMode.LIGHT_LUXURY) Color(0xFF0F172A) else TextPrimary,
        surface = mode.cardBg,
        onSurface = if (mode == LuxuryThemeMode.LIGHT_LUXURY) Color(0xFF0F172A) else TextPrimary,
        surfaceVariant = mode.bgSecondary,
        onSurfaceVariant = TextSecondary,
        outline = mode.borderGlow,
        error = CrimsonRed,
        onError = Color.White
    )
}

@Composable
fun IranBinaryTheme(
    luxuryMode: LuxuryThemeMode = LuxuryThemeMode.PHOSPHOR_CANARY,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = createColorSchemeForMode(luxuryMode),
        typography = Typography,
        content = content
    )
}

