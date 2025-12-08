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
    // Convenience aliases for screens
    val neonBackground = Color(0xFF0B0B1C)
    val textPrimary = Color(0xFFE8EAF6)
    val textSecondary = Color(0xFF9BA4C4)
    val surfaceVariant = Color(0xFF161628)
}

object HolographicColors {
    // Core Holographic Colors with enhanced vibrancy
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
    
    // 3D Depth Colors with enhanced contrast
    val depthBlack = Color(0xFF000011)
    val depthVoid = Color(0xFF050515)
    val depthMidnight = Color(0xFF0A0A3A)
    val depthOcean = Color(0xFF001144)
    val depthAbyss = Color(0xFF000833)
    val depthDark = Color(0xFF080820)
    
    // Enhanced Grid Lines with glow
    val gridPrimary = Color(0x6000FFFF)
    val gridSecondary = Color(0x409D00FF)
    val gridTertiary = Color(0x20FFFFFF)
    
    // UI Elements with enhanced glow
    val uiGlowPrimary = Color(0x8000FFFF)
    val uiGlowSecondary = Color(0x60FF00FF)
    val uiGlowTertiary = Color(0x40FFFFFF)
    val uiHighlight = Color(0xB0FFFFFF)
    val uiShadow = Color(0xA0000088)
    
    // Text Colors with enhanced readability
    val textHologram = Color(0xFFFFFFFF)
    val textGlow = Color(0xFF00FFFF)
    val textShadow = Color(0xFF0000AA)
    val textNeon = Color(0xFFFF00FF)
    val textWarning = Color(0xFFFFFF00)
    
    // Interactive States with enhanced feedback
    val interactiveActive = Color(0xFF00FFAA)
    val interactiveHover = Color(0xFF00E5FF)
    val interactivePressed = Color(0xFFFF00FF)
    val interactiveDisabled = Color(0xFF6666FF)
    val interactiveSuccess = Color(0xFF00FF80)
    val interactiveError = Color(0xFFFF0055)
    
    // Game Specific Colors with enhanced vibrancy
    val playerOne = Color(0xFF00FFFF)
    val playerTwo = Color(0xFFFF00FF)
    val playerThree = Color(0xFFFFFF00)
    val playerFour = Color(0xFF00FFAA)
    val winGlow = Color(0xFFFFFF00)
    val scoreActive = Color(0xFF00FFAA)
    val scoreBackground = Color(0x2000FFAA)
    val scoreGlow = Color(0x40FF00FF)
    
    // Special Effect Colors
    val particleGlow = Color(0x60FFFFFF)
    val scanLine = Color(0x80FFFFFF)
    val lensFlare = Color(0x30FFFFFF)
    val chromaticAberration = Color(0x40FF0000)
    
    // Button Colors by Type
    val buttonPrimary = Color(0xFF00FFFF)
    val buttonSecondary = Color(0xFFFF00FF)
    val buttonSuccess = Color(0xFF00FFAA)
    val buttonWarning = Color(0xFFFFFF00)
    val buttonDanger = Color(0xFFFF0055)
    val buttonInfo = Color(0xFF9D00FF)
}

// Light/Dark theme configuration with holographic focus
val holographicDarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = HolographicColors.hologramCyan,
    secondary = HolographicColors.hologramPurple,
    tertiary = HolographicColors.hologramGreen,
    background = HolographicColors.depthVoid,
    surface = HolographicColors.depthMidnight,
    surfaceVariant = HolographicColors.depthOcean,
    error = HolographicColors.hologramRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = HolographicColors.textHologram,
    onSurface = HolographicColors.textHologram.copy(alpha = 0.9f),
    onSurfaceVariant = HolographicColors.textHologram.copy(alpha = 0.7f),
    onError = Color.Black
)

val holographicLightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Color(0xFF0066FF),
    secondary = Color(0xFFAA00FF),
    tertiary = Color(0xFF00AA88),
    background = Color(0xFFF0F8FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFE6F0FF),
    error = Color(0xFFFF3366),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF001144),
    onSurface = Color(0xFF002266),
    onSurfaceVariant = Color(0xFF003388),
    onError = Color.White
)

// Specialized palettes for 3D effects
object HolographicGradients {
    // Main gradients
    val cyanToPurple = listOf(
        HolographicColors.hologramCyan,
        HolographicColors.hologramBlue,
        HolographicColors.hologramPurple,
        HolographicColors.hologramPink
    )
    
    val fullSpectrum = listOf(
        HolographicColors.hologramRed,
        HolographicColors.hologramOrange,
        HolographicColors.hologramYellow,
        HolographicColors.hologramGreen,
        HolographicColors.hologramCyan,
        HolographicColors.hologramBlue,
        HolographicColors.hologramPurple,
        HolographicColors.hologramPink
    )
    
    val depthGradient = listOf(
        HolographicColors.depthBlack,
        HolographicColors.depthVoid,
        HolographicColors.depthMidnight,
        HolographicColors.depthOcean
    )
    
    val neonGlow = listOf(
        Color.Transparent,
        HolographicColors.hologramCyan.copy(alpha = 0.3f),
        HolographicColors.hologramCyan.copy(alpha = 0.6f),
        HolographicColors.hologramCyan.copy(alpha = 0.3f),
        Color.Transparent
    )
    
    val buttonGlow = listOf(
        HolographicColors.buttonPrimary,
        HolographicColors.buttonSecondary,
        HolographicColors.buttonPrimary
    )
    
    val chipGlow = listOf(
        Color.White.copy(alpha = 0.8f),
        HolographicColors.hologramCyan.copy(alpha = 0.6f),
        HolographicColors.hologramPurple.copy(alpha = 0.6f),
        Color.White.copy(alpha = 0.8f)
    )
    
    val winGradient = listOf(
        HolographicColors.winGlow,
        HolographicColors.hologramYellow,
        HolographicColors.hologramOrange,
        HolographicColors.winGlow
    )
}

// Grid patterns for 3D effects
object GridPatterns {
    val holographicGrid = listOf(
        Pair(0.0f to 0.0f, 1.0f to 1.0f),
        Pair(0.25f to 0.0f, 1.0f to 0.75f),
        Pair(0.0f to 0.25f, 0.75f to 1.0f),
        Pair(0.5f to 0.0f, 1.0f to 0.5f),
        Pair(0.0f to 0.5f, 0.5f to 1.0f)
    )
    
    val circuitGrid = listOf(
        Pair(0.0f to 0.333f, 1.0f to 0.333f),
        Pair(0.0f to 0.666f, 1.0f to 0.666f),
        Pair(0.166f to 0.0f, 0.166f to 1.0f),
        Pair(0.333f to 0.0f, 0.333f to 1.0f),
        Pair(0.5f to 0.0f, 0.5f to 1.0f),
        Pair(0.666f to 0.0f, 0.666f to 1.0f),
        Pair(0.833f to 0.0f, 0.833f to 1.0f)
    )
    
    val hexGrid = listOf(
        Pair(0.166f to 0.0f, 0.166f to 1.0f),
        Pair(0.5f to 0.0f, 0.5f to 1.0f),
        Pair(0.833f to 0.0f, 0.833f to 1.0f),
        Pair(0.0f to 0.25f, 1.0f to 0.25f),
        Pair(0.0f to 0.5f, 1.0f to 0.5f),
        Pair(0.0f to 0.75f, 1.0f to 0.75f)
    )
}

// Particle effects colors for 3D animations
object ParticleColors {
    val hologramParticles = listOf(
        HolographicColors.hologramCyan,
        HolographicColors.hologramBlue,
        HolographicColors.hologramPurple,
        HolographicColors.hologramGreen,
        HolographicColors.hologramPink
    )
    
    val sparkleParticles = listOf(
        Color.White,
        HolographicColors.hologramCyan,
        HolographicColors.hologramYellow,
        Color(0x80FFFFFF),
        HolographicColors.hologramPink
    )
    
    val energyParticles = listOf(
        HolographicColors.hologramCyan,
        HolographicColors.hologramBlue,
        Color(0x80FFFFFF),
        HolographicColors.hologramPurple
    )
    
    val winParticles = listOf(
        HolographicColors.winGlow,
        HolographicColors.hologramYellow,
        Color.White,
        HolographicColors.hologramOrange
    )
}

// Character chip colors
object CharacterColors {
    val nexus = Color(0xFF00FFFF)
    val cypher = Color(0xFF9D00FF)
    val spectre = Color(0xFF00FFAA)
    val valkyrie = Color(0xFFFF0055)
    val oracle = Color(0xFFFFFF00)
    val chimera = Color(0xFFFF00FF)
}

// Color extension functions for enhanced effects
fun Color.darken(factor: Float): Color {
    return Color(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

fun Color.lighten(factor: Float): Color {
    return Color(
        red = (red + (1 - red) * factor).coerceIn(0f, 1f),
        green = (green + (1 - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1 - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}

fun Color.withAlpha(alpha: Float): Color {
    return this.copy(alpha = alpha)
}

// Function to create shimmer effect
fun shimmerBrush(): androidx.compose.ui.graphics.Brush {
    return androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            HolographicColors.uiGlowPrimary,
            Color.Transparent
        ),
        start = androidx.compose.ui.geometry.Offset(-100f, -100f),
        end = androidx.compose.ui.geometry.Offset(100f, 100f)
    )
}
