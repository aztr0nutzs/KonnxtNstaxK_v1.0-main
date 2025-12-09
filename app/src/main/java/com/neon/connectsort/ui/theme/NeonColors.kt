package com.neon.connectsort.ui.theme

import androidx.compose.ui.graphics.Color

object NeonColors {
    val neonCyan = Color(0xFF00FFFF)
    val neonMagenta = Color(0xFFFF00FF)
    val neonBlue = Color(0xFF00B6FF)
    val neonGreen = Color(0xFF00FFAA)
    val neonYellow = Color(0xFFFFFF66)
    val neonOrange = Color(0xFFFF8800)
    val neonRed = Color(0xFFFF3366)
    val neonPurple = Color(0xFF9D00FF)
    val cardBackground = Color(0xE6050515)
    val neonBackground = Color(0xFF0B0B1C)
    val textPrimary = Color(0xFFE8EAF6)
    val textSecondary = Color(0xFF9BA4C4)
    val surfaceVariant = Color(0xFF161628)

    val hologramBlue = Color(0xFF00E5FF)
    val hologramCyan = Color(0xFF00FFFF)
    val hologramPurple = Color(0xFF9D00FF)
    val hologramPink = Color(0xFFFF00FF)
    val hologramGreen = Color(0xFF00FFAA)
    val hologramYellow = Color(0xFFFFFF00)
    val hologramRed = Color(0xFFFF0055)
    val hologramOrange = Color(0xFFFF8000)
    val hologramViolet = Color(0xFF8000FF)
    val hologramAqua = Color(0xFF00FFE5)
    
    val depthBlack = Color(0xFF000011)
    val depthVoid = Color(0xFF050515)
    val depthMidnight = Color(0xFF0A0A3A)
    val depthOcean = Color(0xFF001144)
    val depthAbyss = Color(0xFF000833)
    val depthDark = Color(0xFF080820)
    
    val gridPrimary = Color(0x6000FFFF)
    val gridSecondary = Color(0x409D00FF)
    val gridTertiary = Color(0x20FFFFFF)
    
    val uiGlowPrimary = Color(0x8000FFFF)
    val uiGlowSecondary = Color(0x60FF00FF)
    val uiGlowTertiary = Color(0x40FFFFFF)
    val uiHighlight = Color(0xB0FFFFFF)
    val uiShadow = Color(0xA0000088)
    
    val textHologram = Color(0xFFFFFFFF)
    val textGlow = Color(0xFF00FFFF)
    val textShadow = Color(0xFF0000AA)
    val textNeon = Color(0xFFFF00FF)
    val textWarning = Color(0xFFFFFF00)
    
    val interactiveActive = Color(0xFF00FFAA)
    val interactiveHover = Color(0xFF00E5FF)
    val interactivePressed = Color(0xFFFF00FF)
    val interactiveDisabled = Color(0xFF6666FF)
    val interactiveSuccess = Color(0xFF00FF80)
    val interactiveError = Color(0xFFFF0055)
    
    val playerOne = Color(0xFF00FFFF)
    val playerTwo = Color(0xFFFF00FF)
    val playerThree = Color(0xFFFFFF00)
    val playerFour = Color(0xFF00FFAA)
    val winGlow = Color(0xFFFFFF00)
    val scoreActive = Color(0xFF00FFAA)
    val scoreBackground = Color(0x2000FFAA)
    val scoreGlow = Color(0x40FF00FF)
    
    val particleGlow = Color(0x60FFFFFF)
    val scanLine = Color(0x80FFFFFF)
    val lensFlare = Color(0x30FFFFFF)
    val chromaticAberration = Color(0x40FF0000)
    
    val buttonPrimary = Color(0xFF00FFFF)
    val buttonSecondary = Color(0xFFFF00FF)
    val buttonSuccess = Color(0xFF00FFAA)
    val buttonWarning = Color(0xFFFFFF00)
    val buttonDanger = Color(0xFFFF0055)
    val buttonInfo = Color(0xFF9D00FF)
}

val holographicDarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = NeonColors.hologramCyan,
    secondary = NeonColors.hologramPurple,
    tertiary = NeonColors.hologramGreen,
    background = NeonColors.depthVoid,
    surface = NeonColors.depthMidnight,
    surfaceVariant = NeonColors.depthOcean,
    error = NeonColors.hologramRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = NeonColors.textHologram,
    onSurface = NeonColors.textHologram.copy(alpha = 0.9f),
    onSurfaceVariant = NeonColors.textHologram.copy(alpha = 0.7f),
    onError = Color.Black
)
