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
    val palettePrimary = if (darkTheme) themeSettings.themePalette.primaryDarkColor else themeSettings.themePalette.primaryColor
    val colorScheme = if (darkTheme) {
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
            content = content
        )
    }
}
