package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = IndigoOnPrimary,
    primaryContainer = IndigoPrimaryContainer,
    onPrimaryContainer = IndigoOnPrimaryContainer,
    secondary = CrimsonSecondary,
    onSecondary = CrimsonOnSecondary,
    secondaryContainer = CrimsonSecondaryContainer,
    onSecondaryContainer = CrimsonOnSecondaryContainer,
    tertiary = TealTertiary,
    onTertiary = TealOnTertiary,
    tertiaryContainer = TealTertiaryContainer,
    onTertiaryContainer = TealOnTertiaryContainer,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryContainer,
    onPrimary = IndigoOnPrimaryContainer,
    secondary = CrimsonSecondaryContainer,
    onSecondary = CrimsonOnSecondaryContainer,
    background = DarkBackground,
    surface = DarkSurface
)

// ====================================================================
// Modern Japanese Minimalist Aesthetic Schemes (Washi & Crimson)
// ====================================================================
private val WashiMinimalistLightColorScheme = lightColorScheme(
    primary = JapaneseCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEAEA),
    onPrimaryContainer = Color(0xFF7A0C1E),
    secondary = SumiInkLine,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1ECE1),
    onSecondaryContainer = Color(0xFF2B2622),
    tertiary = VermilionRed,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9DF),
    onTertiaryContainer = Color(0xFF4C0519),
    background = WashiPaperLight,
    onBackground = SumiInkLine,
    surface = WashiPaperCardLight,
    onSurface = SumiInkLine,
    surfaceVariant = WashiPaperSurfaceVariantLight,
    onSurfaceVariant = SumiInkSoft,
    outline = WashiBorderLight,
    outlineVariant = Color(0xFFDED6C7)
)

private val WashiMinimalistDarkColorScheme = darkColorScheme(
    primary = CrimsonAccentDark,
    onPrimary = Color(0xFF38000A),
    primaryContainer = Color(0xFF6B1123),
    onPrimaryContainer = Color(0xFFFFD9DF),
    secondary = Color(0xFFDCD4C6),
    onSecondary = Color(0xFF221F1C),
    secondaryContainer = Color(0xFF3A352F),
    onSecondaryContainer = Color(0xFFECE5D9),
    tertiary = Color(0xFFF87171),
    onTertiary = Color(0xFF450A0A),
    background = WashiPaperDark,
    onBackground = Color(0xFFEFE8DD),
    surface = WashiPaperCardDark,
    onSurface = Color(0xFFEFE8DD),
    surfaceVariant = WashiPaperSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFB8B0A2),
    outline = WashiBorderDark,
    outlineVariant = Color(0xFF332F2A)
)

@Composable
fun KanjiKotobaTheme(
    themeSettings: AppThemeSettings = AppThemeSettings(),
    darkTheme: Boolean = when (themeSettings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    },
    content: @Composable () -> Unit
) {
    val isWashi = themeSettings.themeAesthetic == ThemeAesthetic.WASHI_MINIMALIST
    val palettePrimary = if (darkTheme) themeSettings.themePalette.primaryDarkColor else themeSettings.themePalette.primaryColor

    val colorScheme = if (isWashi) {
        if (darkTheme) {
            WashiMinimalistDarkColorScheme.copy(
                surface = if (themeSettings.oledBlack) Color.Black else WashiPaperCardDark,
                background = if (themeSettings.oledBlack) Color.Black else WashiPaperDark
            )
        } else {
            WashiMinimalistLightColorScheme
        }
    } else {
        if (darkTheme) {
            DarkColorScheme.copy(
                primary = palettePrimary,
                surface = if (themeSettings.oledBlack) Color.Black else DarkSurface,
                background = if (themeSettings.oledBlack) Color.Black else DarkBackground
            )
        } else {
            LightColorScheme.copy(
                primary = palettePrimary
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalAppThemeSettings provides themeSettings) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = {
                if (isWashi) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .washiPaperTexture(
                                isDark = darkTheme,
                                enabled = true,
                                fiberBaseAlpha = if (darkTheme) 0.05f else 0.08f
                            )
                    ) {
                        content()
                    }
                } else {
                    content()
                }
            }
        )
    }
}
