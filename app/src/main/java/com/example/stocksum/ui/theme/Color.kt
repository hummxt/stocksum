package com.example.stocksum.ui.theme

import androidx.compose.ui.graphics.Color

val DarkBgBase = Color(0xFF0A0F1E)
val DarkBgCard = Color(0xFF141929)
val DarkBgElevated = Color(0xFF1C2540)
val DarkBgInput = Color(0xFF1A2035)
val DarkBorderDefault = Color(0xFF252E4A)
val DarkBorderSubtle = Color(0xFF1C2540)
val DarkHeroCardBg = Color(0xFF0F1E3D)

val DarkTextPrimary = Color(0xFFF0F4FF)
val DarkTextSecondary = Color(0xFF7A86A8)
val DarkTextTertiary = Color(0xFF3D4A70)

val DarkGain = Color(0xFF4CAF50)
val DarkGainBg = Color(0x1A4CAF50) 
val DarkLoss = Color(0xFFE57373)
val DarkLossBg = Color(0x1AE57373)
val DarkNeutral = Color(0xFFFFCA28)
val DarkNeutralBg = Color(0x1AFFFFCA28)
val DarkAccent = Color(0xFF64B5F6)
val DarkAccentBg = Color(0x1A64B5F6)

val LightBgBase = Color(0xFFF5F7FA)
val LightBgCard = Color(0xFFFFFFFF)
val LightBgElevated = Color(0xFFE8EEF8)
val LightBgInput = Color(0xFFE8EEF8)
val LightBorderDefault = Color(0xFFD1D9E6)
val LightBorderSubtle = Color(0xFFE8EEF8)
val LightHeroCardBg = Color(0xFFE8EEF8)

val LightTextPrimary = Color(0xFF141929)
val LightTextSecondary = Color(0xFF5A6682)
val LightTextTertiary = Color(0xFF8A95AE)

val LightGain = Color(0xFF00C853)
val LightGainBg = Color(0xFFE8F5E9)
val LightLoss = Color(0xFFD50000)
val LightLossBg = Color(0xFFFFEBEE)
val LightNeutral = Color(0xFFFF8F00)
val LightNeutralBg = Color(0xFFFFF8E1)
val LightAccent = Color(0xFF2962FF)
val LightAccentBg = Color(0xFFE3F2FD)

fun stocksumDarkColors() = StocksumColors(
    bgBase = DarkBgBase,
    bgCard = DarkBgCard,
    bgElevated = DarkBgElevated,
    bgInput = DarkBgInput,
    border = DarkBorderDefault,
    borderSubtle = DarkBorderSubtle,
    gain = DarkGain,
    gainBg = DarkGainBg,
    loss = DarkLoss,
    lossBg = DarkLossBg,
    neutral = DarkNeutral,
    neutralBg = DarkNeutralBg,
    accent = DarkAccent,
    accentBg = DarkAccentBg,
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textTertiary = DarkTextTertiary,
    textGain = DarkGain,
    textLoss = DarkLoss,
    heroCardBg = DarkHeroCardBg,
    isDark = true
)

fun stocksumLightColors() = StocksumColors(
    bgBase = LightBgBase,
    bgCard = LightBgCard,
    bgElevated = LightBgElevated,
    bgInput = LightBgInput,
    border = LightBorderDefault,
    borderSubtle = LightBorderSubtle,
    gain = LightGain,
    gainBg = LightGainBg,
    loss = LightLoss,
    lossBg = LightLossBg,
    neutral = LightNeutral,
    neutralBg = LightNeutralBg,
    accent = LightAccent,
    accentBg = LightAccentBg,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textTertiary = LightTextTertiary,
    textGain = LightGain,
    textLoss = LightLoss,
    heroCardBg = LightHeroCardBg,
    isDark = false
)