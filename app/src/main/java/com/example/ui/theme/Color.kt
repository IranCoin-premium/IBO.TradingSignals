package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// High-end Cybernetic & Luxury Fintech Theme Palette
val SlateDark950 = Color(0xFF060911)
val SlateDark900 = Color(0xFF0A0F1D)
val SlateDark800 = Color(0xFF111827)
val SlateDark700 = Color(0xFF1F2937)
val SlateDark600 = Color(0xFF374151)

// Vibrant Requested Modern Palettes
val CanaryYellow = Color(0xFFFFE600)      // زرد قناری درخشان
val CanaryYellowGlow = Color(0xFFFFF066)
val CanaryYellowDark = Color(0xFF807300)

val PhosphorGreen = Color(0xFF39FF14)     // سبز فسفری نئون
val PhosphorGreenGlow = Color(0xFF70FF54)
val PhosphorGreenDark = Color(0xFF1B8009)

val ElectricOrange = Color(0xFFFF6B00)    // نارنجی پرانرژی
val ElectricOrangeGlow = Color(0xFFFF944D)

val RoyalCyberBlue = Color(0xFF0066FF)    // آبی رویال درخشان
val RoyalCyberBlueGlow = Color(0xFF4D94FF)

val EmeraldNeon = Color(0xFF10B981)
val EmeraldGlow = Color(0xFF34D399)
val EmeraldDark = Color(0xFF064E3B)

val CyanNeon = Color(0xFF00F0FF)
val CyanGlow = Color(0xFF67E8F9)

val AmberGold = Color(0xFFF59E0B)
val GoldGlow = Color(0xFFFFD700)

val CrimsonRed = Color(0xFFFF1E44)        // قرمز پرحرارت
val CrimsonGlow = Color(0xFFFF6B81)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val CardSurface = Color(0xFF0D1322)
val CardSurfaceElevated = Color(0xFF141C30)
val CardBorder = Color(0xFF22304A)
val CardBorderGlow = Color(0x4439FF14)

// Luxury Background Theme Modes (قابل تنظیم از پنل ادمین و سوییچ کلاینت)
enum class LuxuryThemeMode(
    val title: String,
    val bgPrimary: Color,
    val bgSecondary: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val cardBg: Color,
    val borderGlow: Color
) {
    PHOSPHOR_CANARY(
        "فسفری - قناری سوپرلوکس ⚡",
        Color(0xFF060A10),
        Color(0xFF0B131F),
        PhosphorGreen,
        CanaryYellow,
        Color(0xFF0E1726),
        Color(0x5539FF14)
    ),
    CANARY_CYBER(
        "زرد قناری & آبی سایبر 🌟",
        Color(0xFF080C16),
        Color(0xFF0E1526),
        CanaryYellow,
        CyanNeon,
        Color(0xFF111A2E),
        Color(0x55FFE600)
    ),
    CRIMSON_ORANGE(
        "نارنجی پرحرارت & زرشکی 🔥",
        Color(0xFF120608),
        Color(0xFF1E0A0E),
        ElectricOrange,
        CrimsonRed,
        Color(0xFF240E14),
        Color(0x55FF6B00)
    ),
    ROYAL_SAPPHIRE(
        "آبی رویال & طلای ۲۴ عیار 💎",
        Color(0xFF050B18),
        Color(0xFF0A1530),
        RoyalCyberBlue,
        AmberGold,
        Color(0xFF0D1D42),
        Color(0x550066FF)
    ),
    LIGHT_LUXURY(
        "سفید عاجی و کریستالی لوکس (تم روشن) ☀️",
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0),
        Color(0xFF0F172A),
        PhosphorGreenDark,
        Color(0xFFFFFFFF),
        Color(0x330F172A)
    )
}

