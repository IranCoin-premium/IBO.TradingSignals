package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldNeon,
    onPrimary = Color.Black,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldGlow,
    secondary = CyanNeon,
    onSecondary = Color.Black,
    secondaryContainer = SlateDark700,
    onSecondaryContainer = CyanGlow,
    tertiary = AmberGold,
    onTertiary = Color.Black,
    tertiaryContainer = SlateDark800,
    onTertiaryContainer = GoldGlow,
    background = SlateDark950,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateDark900,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = CrimsonRed,
    onError = Color.White
)

@Composable
fun IranBinaryTheme(
    darkTheme: Boolean = true, // Default to sleek high-tech dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
