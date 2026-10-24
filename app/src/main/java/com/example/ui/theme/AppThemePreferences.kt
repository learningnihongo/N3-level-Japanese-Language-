package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(val titleBurmese: String, val titleEnglish: String) {
    SYSTEM("စနစ်အတိုင်း", "System Default"),
    LIGHT("အလင်း (နေ့ခင်း)", "Light Mode"),
    DARK("အမှောင် (ညဘက်)", "Dark Mode")
}

enum class ThemePalette(
    val titleBurmese: String,
    val titleJapanese: String,
    val titleEnglish: String,
    val primaryColor: Color,
    val primaryDarkColor: Color
) {
    CRIMSON(
        titleBurmese = "朱色 • ဂျပန်ကြက်သွေးနီ",
        titleJapanese = "朱色 (Kurenai)",
        titleEnglish = "Japanese Crimson",
        primaryColor = Color(0xFFC62828),
        primaryDarkColor = Color(0xFFFF8983)
    ),
    INDIGO(
        titleBurmese = "藍色 • လျှပ်စစ်နက်ပြာ (Electric Indigo)",
        titleJapanese = "藍色 (Electric Indigo & Violet)",
        titleEnglish = "Electric Indigo & Violet",
        primaryColor = ElectricIndigo,
        primaryDarkColor = ElectricIndigoDark
    ),

    MATCHA(
        titleBurmese = "抹茶 • ကျိုတိုလက်ဖက်စိမ်း",
        titleJapanese = "抹茶 (Kyoto Matcha)",
        titleEnglish = "Kyoto Matcha",
        primaryColor = Color(0xFF2E7D32),
        primaryDarkColor = Color(0xFF81C784)
    ),
    SAKURA(
        titleBurmese = "桜色 • ချယ်ရီပန်းရောင်",
        titleJapanese = "桜色 (Sakura)",
        titleEnglish = "Sakura Rose",
        primaryColor = Color(0xFFAD1457),
        primaryDarkColor = Color(0xFFF48FB1)
    ),
    FUJI(
        titleBurmese = "藤紫 • ဖူဂျီခရမ်းရောင်",
        titleJapanese = "藤紫 (Fuji Wisteria)",
        titleEnglish = "Fuji Wisteria",
        primaryColor = Color(0xFF6A1B9A),
        primaryDarkColor = Color(0xFFCE93D8)
    ),
    KOGANE(
        titleBurmese = "黄金 • ရွှေဝါရောင်နွေးထွေး",
        titleJapanese = "黄金 (Kogane)",
        titleEnglish = "Imperial Amber",
        primaryColor = Color(0xFFD97706),
        primaryDarkColor = Color(0xFFFBBF24)
    )
}

enum class IconThemeStyle(
    val titleBurmese: String,
    val titleEnglish: String,
    val description: String
) {
    CLASSIC_DYNAMIC(
        titleBurmese = "ခိုင်မာသော အိုင်ကွန်များ",
        titleEnglish = "Filled Dynamic",
        description = "Standard bold filled icons with Japanese Kanji badges"
    ),
    MODERN_OUTLINE(
        titleBurmese = "လိုင်းသန့် အိုင်ကွန်များ",
        titleEnglish = "Minimalist Outline",
        description = "Clean, refined outline contours with modern spacing"
    ),
    DUOTONE_GLOW(
        titleBurmese = "အရောင်စုံ အိုင်ကွန်များ",
        titleEnglish = "Duotone Vibrant",
        description = "Multi-color accent badges with subtle contrast glowing"
    )
}

enum class ThemeAesthetic(
    val titleBurmese: String,
    val titleJapanese: String,
    val titleEnglish: String,
    val description: String,
    val sealSymbol: String
) {
    MATERIAL3(
        titleBurmese = "Material 3 စံဒီဇိုင်း",
        titleJapanese = "マテリアル3 (M3)",
        titleEnglish = "Material 3 Default",
        description = "Clean modern Android design system with indigo accents and dynamic tonal surfaces",
        sealSymbol = "M3"
    ),
    WASHI_MINIMALIST(
        titleBurmese = "ဂျပန် ဝါရှီစက္ကူ မီနီမဲလ်",
        titleJapanese = "和紙ミニマル (Washi Minimalist)",
        titleEnglish = "Modern Japanese Minimalist",
        description = "Authentic tactile Washi paper texture, vibrant Japanese Crimson (朱色) seals & Sumi ink lines",
        sealSymbol = "和"
    )
}

data class AppThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themePalette: ThemePalette = ThemePalette.INDIGO,
    val iconThemeStyle: IconThemeStyle = IconThemeStyle.CLASSIC_DYNAMIC,
    val oledBlack: Boolean = true,
    val themeAesthetic: ThemeAesthetic = ThemeAesthetic.MATERIAL3
)

val LocalAppThemeSettings = staticCompositionLocalOf { AppThemeSettings() }

class AppThemePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("kanji_kotoba_theme_prefs", Context.MODE_PRIVATE)

    private val _themeSettingsFlow = MutableStateFlow(loadSettings())
    val themeSettingsFlow: StateFlow<AppThemeSettings> = _themeSettingsFlow.asStateFlow()

    val settingsFlow: StateFlow<AppThemeSettings>
        get() = themeSettingsFlow

    @get:JvmName("getSettingsProperty")
    val settings: AppThemeSettings
        get() = loadSettings()

    fun getSettings(): AppThemeSettings = loadSettings()

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeSettingsFlow.value = loadSettings()
    }

    fun setThemePalette(palette: ThemePalette) {
        prefs.edit().putString(KEY_THEME_PALETTE, palette.name).apply()
        _themeSettingsFlow.value = loadSettings()
    }

    fun setIconThemeStyle(style: IconThemeStyle) {
        prefs.edit().putString(KEY_ICON_STYLE, style.name).apply()
        _themeSettingsFlow.value = loadSettings()
    }

    fun setOledBlack(oled: Boolean) {
        prefs.edit().putBoolean(KEY_OLED_BLACK, oled).apply()
        _themeSettingsFlow.value = loadSettings()
    }

    fun setThemeAesthetic(aesthetic: ThemeAesthetic) {
        prefs.edit().putString(KEY_THEME_AESTHETIC, aesthetic.name).apply()
        _themeSettingsFlow.value = loadSettings()
    }

    fun toggleDarkMode(currentIsDark: Boolean) {
        val nextMode = if (currentIsDark) ThemeMode.LIGHT else ThemeMode.DARK
        setThemeMode(nextMode)
    }

    private fun loadSettings(): AppThemeSettings {
        val modeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        val paletteStr = prefs.getString(KEY_THEME_PALETTE, ThemePalette.INDIGO.name) ?: ThemePalette.INDIGO.name
        val iconStr = prefs.getString(KEY_ICON_STYLE, IconThemeStyle.CLASSIC_DYNAMIC.name) ?: IconThemeStyle.CLASSIC_DYNAMIC.name
        val oled = prefs.getBoolean(KEY_OLED_BLACK, true)
        val aestheticStr = prefs.getString(KEY_THEME_AESTHETIC, ThemeAesthetic.MATERIAL3.name) ?: ThemeAesthetic.MATERIAL3.name

        val mode = try { ThemeMode.valueOf(modeStr) } catch (e: Exception) { ThemeMode.SYSTEM }
        val palette = try { ThemePalette.valueOf(paletteStr) } catch (e: Exception) { ThemePalette.INDIGO }
        val iconStyle = try { IconThemeStyle.valueOf(iconStr) } catch (e: Exception) { IconThemeStyle.CLASSIC_DYNAMIC }
        val aesthetic = try { ThemeAesthetic.valueOf(aestheticStr) } catch (e: Exception) { ThemeAesthetic.MATERIAL3 }

        return AppThemeSettings(
            themeMode = mode,
            themePalette = palette,
            iconThemeStyle = iconStyle,
            oledBlack = oled,
            themeAesthetic = aesthetic
        )
    }

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_THEME_PALETTE = "key_theme_palette"
        private const val KEY_ICON_STYLE = "key_icon_style"
        private const val KEY_OLED_BLACK = "key_oled_black"
        private const val KEY_THEME_AESTHETIC = "key_theme_aesthetic"
    }
}
