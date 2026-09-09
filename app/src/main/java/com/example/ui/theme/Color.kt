package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==============================================================================
// 1. Soft UI Neumorphic & Pastel Gradient System (مطابق دقیق با طرح Gradient Soft UI)
// ==============================================================================
val SoftUiBg = Color(0xFFF2F4F8)                 // پس‌زمینه ابریشمی بسیار نرم و روشن
val SoftUiSurface = Color(0xFFFFFFFF)            // سطح سفید نئومورفیک
val SoftUiCardBorder = Color(0x66E2E8F0)         // لبه ظریف و برآمده کارت‌ها
val SoftUiSoftUiCardBorder = SoftUiCardBorder
val SoftUiShadowLight = Color(0xFFFFFFFF)        // نور بالا سمت چپ نئومورفیک
val SoftUiShadowDark = Color(0x181E293B)         // سایه نرم پایین سمت راست

// کارت‌های گرادیانت پاستلی نرم (Soft Pastel Gradients)
val SoftCardCyanGradient = listOf(Color(0xFFE4F7FA), Color(0xFFD0F0F7))
val SoftCardCyanAccent = Color(0xFF1CB4C8)

val SoftCardPurpleGradient = listOf(Color(0xFFECEBFC), Color(0xFFDDD9FA))
val SoftCardPurpleAccent = Color(0xFF7A6CF0)

val SoftCardMintGradient = listOf(Color(0xFFE3FAF4), Color(0xFFCFF5EC))
val SoftCardMintAccent = Color(0xFF1BBFA1)

val SoftCardPeachGradient = listOf(Color(0xFFFFF0E7), Color(0xFFFFE0D0))
val SoftCardPeachAccent = Color(0xFFFF7251)

val SoftCardRoseGradient = listOf(Color(0xFFFFECF3), Color(0xFFFFDCE8))
val SoftCardRoseAccent = Color(0xFFE83E8C)

val SoftCardOrangeGradient = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
val SoftCardOrangeAccent = Color(0xFFFF9800)

val SoftCardBlueGradient = listOf(Color(0xFFE8F1FD), Color(0xFFD6E5FA))
val SoftCardBlueAccent = Color(0xFF3B82F6)

// دکمه‌های اکشن CALL و PUT نئومورفیک گرادیانتی
val SoftCallGradient = listOf(Color(0xFF74B9FF), Color(0xFF4B7BEC))
val SoftPutGradient = listOf(Color(0xFFFF9E80), Color(0xFFFF6584))

// دانشنامه و آمار چارت
val SoftChartDonutPurple = Color(0xFF8B78FF)
val SoftChartDonutPink = Color(0xFFFF79B0)
val SoftChartDonutBlue = Color(0xFF4FC3F7)

// رنگ‌های متن عمیق و خوانا در محیط نرم
val TextDarkPrimary = Color(0xFF1E293B)          // مشکی/سرمه‌ای زغالی بسیار باکلاس
val TextDarkSecondary = Color(0xFF64748B)        // خاکستری اسلیتی نرم
val TextDarkMuted = Color(0xFF94A3B8)            // طوسی ملایم

val PearlWhiteBg = SoftUiBg
val PearlWhiteBgDeep = Color(0xFFEAEEF6)
val PearlWhiteSurface = SoftUiSurface
val PearlWhiteGlassHigh = Color(0xF8FFFFFF)
val PearlWhiteGlassBorder = Color(0x80FFFFFF)

// Legacy / Compatibility Colors
val TextPrimary = TextDarkPrimary
val TextSecondary = TextDarkSecondary
val TextMuted = TextDarkMuted
val CardSurface = SoftUiSurface
val CardBorder = SoftUiCardBorder

val CanaryYellow = Color(0xFFFFC107)
val CanaryYellowGlow = Color(0xFFFFD54F)
val CanaryYellowDark = Color(0xFFFFA000)

val PhosphorGreen = Color(0xFF10B981)
val PhosphorGreenGlow = Color(0xFF34D399)
val PhosphorGreenDark = Color(0xFF059669)

val ElectricOrange = Color(0xFFFF7251)
val ElectricOrangeGlow = Color(0xFFFF9E80)

val RoyalCyberBlue = Color(0xFF4B7BEC)
val RoyalCyberBlueGlow = Color(0xFF74B9FF)

val EmeraldNeon = Color(0xFF10B981)
val EmeraldGlow = Color(0xFF34D399)
val EmeraldDark = Color(0xFF047857)

val CyanNeon = Color(0xFF06B6D4)
val CyanGlow = Color(0xFF22D3EE)

val AmberGold = Color(0xFFF59E0B)
val GoldGlow = Color(0xFFFBBF24)

val CrimsonRed = Color(0xFFEF4444)
val CrimsonGlow = Color(0xFFF87171)

// Dark compatibility fallback (mapped to Soft UI light palette for complete visual theme consistency)
val SlateDark950 = Color(0xFFF2F4F8)                 // SoftUiBg
val SlateDark900 = Color(0xFFFFFFFF)                 // SoftUiSurface
val SlateDark800 = Color(0xFFEAEEF6)                 // PearlWhiteBgDeep
val SlateDark700 = Color(0xFFCBD5E1)                 // Light border accent
val SlateDark600 = Color(0xFF94A3B8)                 // Soft muted text

// ==============================================================================
// 2. The 60 Ultra-Vibrant Distinctive Color Palette (پالت ۶۰ رنگ متمایز و چشم‌نواز)
// ==============================================================================
val Luxury60Palette: List<Color> = listOf(
    // 1-10: Electric Cyans, Teals & Aquas
    Color(0xFF00E5FF), Color(0xFF00B0FF), Color(0xFF00BFA5), Color(0xFF1DE9B6), Color(0xFF64FFDA),
    Color(0xFF00ACC1), Color(0xFF26C6DA), Color(0xFF4DD0E1), Color(0xFF80DEEA), Color(0xFFB2EBF2),

    // 11-20: Neon Greens, Phosphors & Mints
    Color(0xFF00E676), Color(0xFF76FF03), Color(0xFFC6FF00), Color(0xFF69F0AE), Color(0xFFA7FFEB),
    Color(0xFF2E7D32), Color(0xFF43A047), Color(0xFF66BB6A), Color(0xFF81C784), Color(0xFFA5D6A7),

    // 21-30: Solar Yellows, Canaries & Ambers
    Color(0xFFFFD600), Color(0xFFFFEA00), Color(0xFFFFFF00), Color(0xFFFFAB00), Color(0xFFFF9100),
    Color(0xFFFFC107), Color(0xFFFFD54F), Color(0xFFFFE082), Color(0xFFFFF59D), Color(0xFFFFF9C4),

    // 31-40: Electric Oranges, Corals & Peaches
    Color(0xFFFF6D00), Color(0xFFFF3D00), Color(0xFFFF5722), Color(0xFFFF7043), Color(0xFFFF8A65),
    Color(0xFFFFAB91), Color(0xFFFFCCBC), Color(0xFFF4511E), Color(0xFFE64A19), Color(0xFFD84315),

    // 41-50: Crimson Flames, Magentas & Violets
    Color(0xFFFF1744), Color(0xFFF50057), Color(0xFFD500F9), Color(0xFF651FFF), Color(0xFF3D5AFE),
    Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2), Color(0xFF5C6BC0), Color(0xFF42A5F5),

    // 51-60: Royal Blues, Sapphires & Holographic Chromas
    Color(0xFF2979FF), Color(0xFF0091EA), Color(0xFF0288D1), Color(0xFF0277BD), Color(0xFF01579B),
    Color(0xFF536DFE), Color(0xFF304FFE), Color(0xFF7C4DFF), Color(0xFF6200EA), Color(0xFF3700B3)
)

// Helper function to pick distinct chromatic colors by index
fun getVibrantAccent(index: Int): Color = Luxury60Palette[Math.abs(index) % Luxury60Palette.size]

// Luxury Theme Modes (سفید صدفی غلیظ + شیشه مات با رنگ‌های متعدد)
enum class LuxuryThemeMode(
    val title: String,
    val bgPrimary: Color,
    val bgSecondary: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val cardBg: Color,
    val borderGlow: Color
) {
    PEARL_FROSTED_GLASS(
        "سفید صدفی براق & شیشه مات کریستالی 💎",
        PearlWhiteBg,
        PearlWhiteBgDeep,
        RoyalCyberBlue,
        PhosphorGreen,
        PearlWhiteSurface,
        Color(0x330066FF)
    ),
    PEARL_CANARY_GOLD(
        "سفید صدفی & زرد قناری و طلایی 👑",
        PearlWhiteBg,
        PearlWhiteBgDeep,
        CanaryYellowDark,
        AmberGold,
        PearlWhiteSurface,
        Color(0x44FFD600)
    ),
    PEARL_PHOSPHOR_EMERALD(
        "سفید صدفی & سبز فسفری نئون ⚡",
        PearlWhiteBg,
        PearlWhiteBgDeep,
        PhosphorGreenDark,
        EmeraldNeon,
        PearlWhiteSurface,
        Color(0x4400E676)
    ),
    PEARL_CRIMSON_FLAME(
        "سفید صدفی & زرشکی و نارنجی 🔥",
        PearlWhiteBg,
        PearlWhiteBgDeep,
        CrimsonRed,
        ElectricOrange,
        PearlWhiteSurface,
        Color(0x44FF1744)
    ),
    DARK_NEON_MATRIX(
        "نئومورفیک زمردی و طلایی 🌌",
        PearlWhiteBg,
        PearlWhiteBgDeep,
        PhosphorGreenDark,
        CanaryYellowDark,
        PearlWhiteSurface,
        Color(0x5500E676)
    )
}


