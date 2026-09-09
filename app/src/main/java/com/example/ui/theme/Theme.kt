package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun createColorSchemeForMode(mode: LuxuryThemeMode): androidx.compose.material3.ColorScheme {
    return lightColorScheme(
        primary = mode.accentPrimary,
        onPrimary = Color.White,
        primaryContainer = mode.accentPrimary.copy(alpha = 0.12f),
        onPrimaryContainer = mode.accentPrimary,
        secondary = mode.accentSecondary,
        onSecondary = Color.White,
        secondaryContainer = SoftUiSurface,
        onSecondaryContainer = mode.accentSecondary,
        tertiary = SoftCardPurpleAccent,
        onTertiary = Color.White,
        tertiaryContainer = SoftUiBg,
        onTertiaryContainer = SoftCardPurpleAccent,
        background = SoftUiBg,
        onBackground = TextDarkPrimary,
        surface = SoftUiSurface,
        onSurface = TextDarkPrimary,
        surfaceVariant = SoftUiBg,
        onSurfaceVariant = TextDarkSecondary,
        outline = SoftUiCardBorder,
        error = SoftCardPeachAccent,
        onError = Color.White
    )
}

@Composable
fun IranBinaryTheme(
    luxuryMode: LuxuryThemeMode = LuxuryThemeMode.PEARL_FROSTED_GLASS,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = createColorSchemeForMode(luxuryMode),
        typography = Typography,
        content = content
    )
}


