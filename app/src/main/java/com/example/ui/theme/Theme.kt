package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

private val WashiMinimalistLightColorScheme = lightColorScheme(
    primary = JapaneseCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD5D8),
    onPrimaryContainer = Color(0xFF410008),
    secondary = SumiInkLine,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E2E2),
    onSecondaryContainer = Color(0xFF191C1C),
    tertiary = VermilionRed,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAD6),
    onTertiaryContainer = Color(0xFF410002),
    background = WashiPaperLight,
    onBackground = SumiInkLine,
    surface = WashiPaperCardLight,
    onSurface = SumiInkLine,
    surfaceVariant = WashiPaperSurfaceVariantLight,
    onSurfaceVariant = SumiInkSoft,
    outline = WashiBorderLight
)

private val WashiMinimalistDarkColorScheme = darkColorScheme(
    primary = CrimsonAccentDark,
    onPrimary = Color(0xFF5A1017),
    primaryContainer = Color(0xFF7A1B24),
    onPrimaryContainer = Color(0xFFFFD5D8),
    secondary = Color(0xFFC7C6CA),
    onSecondary = Color(0xFF303034),
    background = WashiPaperDark,
    onBackground = Color(0xFFE5E1E6),
    surface = WashiPaperCardDark,
    onSurface = Color(0xFFE5E1E6),
    surfaceVariant = WashiPaperSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC9C5D0),
    outline = WashiBorderDark
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
    val colorScheme = when (themeSettings.themeAesthetic) {
        ThemeAesthetic.WASHI_MINIMALIST -> if (darkTheme) WashiMinimalistDarkColorScheme else WashiMinimalistLightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = {
            CompositionLocalProvider(
                LocalAppThemeSettings provides themeSettings
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    )
}
