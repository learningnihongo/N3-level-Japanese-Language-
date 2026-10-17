package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val IndigoPrimary = Color(0xFF1E3A8A)
val IndigoOnPrimary = Color(0xFFFFFFFF)
val IndigoPrimaryContainer = Color(0xFFDBEAFE)
val IndigoOnPrimaryContainer = Color(0xFF1E293B)

val CrimsonSecondary = Color(0xFFBE123C)
val CrimsonOnSecondary = Color(0xFFFFFFFF)
val CrimsonSecondaryContainer = Color(0xFFFFE4E6)
val CrimsonOnSecondaryContainer = Color(0xFF4C0519)

val TealTertiary = Color(0xFF0F766E)
val TealOnTertiary = Color(0xFFFFFFFF)
val TealTertiaryContainer = Color(0xFFCCFBF1)
val TealOnTertiaryContainer = Color(0xFF134E4A)

val BackgroundLight = Color(0xFFF8FAFC)
val OnBackgroundLight = Color(0xFF0F172A)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF0F172A)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val OnSurfaceVariantLight = Color(0xFF475569)

val GoldAccent = Color(0xFFD97706)
val SuccessGreen = Color(0xFF16A34A)
val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)

// Brand and Theme Accent Colors
val JapaneseCrimson = Color(0xFFBE123C)
val JapaneseIndigo = Color(0xFF1E3A8A)
val MasteredGreen = Color(0xFF16A34A)
val MasteredGreenDark = Color(0xFF4ADE80)
val ReviewBlue = Color(0xFF2563EB)
val ReviewBlueDark = Color(0xFF60A5FA)
val WeakOrange = Color(0xFFEA580C)
val StreakOrange = Color(0xFFF97316)
val AmberGold = Color(0xFFD97706)
val SakuraPinkDark = Color(0xFFF48FB1)
val PolishPrimary = Color(0xFF1E3A8A)
val PolishTertiary = Color(0xFF0F766E)
val PolishOutline = Color(0xFF94A3B8)
val PolishOutlineVariant = Color(0xFFCBD5E1)
val ElectricIndigo = Color(0xFF4F46E5)
val ElectricIndigoDark = Color(0xFF818CF8)

// Modern Japanese Minimalist Washi Colors & Crimson Accents
val WashiPaperLight = Color(0xFFFAF7F2)
val WashiPaperCardLight = Color(0xFFFFFDF9)
val WashiPaperSurfaceVariantLight = Color(0xFFF2ECE1)
val WashiPaperDark = Color(0xFF171513)
val WashiPaperCardDark = Color(0xFF23201D)
val WashiPaperSurfaceVariantDark = Color(0xFF2D2925)
val SumiInkLine = Color(0xFF2B2622)
val SumiInkSoft = Color(0xFF5E564D)
val WashiBorderLight = Color(0xFFE2DCD0)
val WashiBorderDark = Color(0xFF3D3730)
val VermilionRed = Color(0xFFC92A2A)
val CrimsonAccentDark = Color(0xFFFF6B6B)
val WashiFiberLight = Color(0xFF9E9282)
val WashiFiberDark = Color(0xFF4E473E)

// SRS Status Helper Colors
fun getSrsAgainBg(isDark: Boolean): Color = if (isDark) Color(0xFF450A0A) else Color(0xFFFEE2E2)
fun getSrsAgainText(isDark: Boolean): Color = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
fun getSrsHardBg(isDark: Boolean): Color = if (isDark) Color(0xFF431407) else Color(0xFFFFEDD5)
fun getSrsHardText(isDark: Boolean): Color = if (isDark) Color(0xFFFB923C) else Color(0xFFEA580C)
fun getSrsGoodBg(isDark: Boolean): Color = if (isDark) Color(0xFF172554) else Color(0xFFDBEAFE)
fun getSrsGoodText(isDark: Boolean): Color = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
fun getSrsEasyBg(isDark: Boolean): Color = if (isDark) Color(0xFF052E16) else Color(0xFFDCFCE7)
fun getSrsEasyText(isDark: Boolean): Color = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A)
