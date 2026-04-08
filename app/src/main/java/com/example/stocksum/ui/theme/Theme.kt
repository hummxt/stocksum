package com.example.stocksum.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class StocksumColors(
    val bgBase: Color,
    val bgCard: Color,
    val bgElevated: Color,
    val bgInput: Color,
    val border: Color,
    val borderSubtle: Color,
    val gain: Color,
    val gainBg: Color,
    val loss: Color,
    val lossBg: Color,
    val neutral: Color,
    val neutralBg: Color,
    val accent: Color,
    val accentBg: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textGain: Color,
    val textLoss: Color,
    val heroCardBg: Color,
    val isDark: Boolean
)

val LocalStocksumColors = staticCompositionLocalOf { stocksumDarkColors() }
val LocalStocksumTypography = staticCompositionLocalOf { StocksumTypography() }

object StocksumTheme {
    val colors: StocksumColors
        @Composable
        @ReadOnlyComposable
        get() = LocalStocksumColors.current

    val typography: StocksumTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalStocksumTypography.current
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

@Composable
fun StocksumTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colors = if (isDark) stocksumDarkColors() else stocksumLightColors()

    CompositionLocalProvider(
        LocalStocksumColors provides colors,
        LocalStocksumTypography provides StocksumTypography(),
        content = content
    )
}