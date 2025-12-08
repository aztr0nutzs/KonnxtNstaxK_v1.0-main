package com.neon.connectsort.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class HolographicColorPalette(
    val hologramBlue: Color,
    val hologramCyan: Color,
    val hologramPurple: Color,
    val hologramPink: Color,
    val hologramGreen: Color,
    val hologramYellow: Color,
    val hologramRed: Color,
    val hologramOrange: Color,
    val hologramViolet: Color,
    val hologramAqua: Color,
    
    val depthBlack: Color,
    val depthVoid: Color,
    val depthMidnight: Color,
    val depthOcean: Color,
    val depthAbyss: Color,
    
    val gridPrimary: Color,
    val gridSecondary: Color,
    val gridTertiary: Color,
    
    val uiGlowPrimary: Color,
    val uiGlowSecondary: Color,
    val uiGlowTertiary: Color,
    val uiHighlight: Color,
    val uiShadow: Color,
    
    val textHologram: Color,
    val textGlow: Color,
    val textShadow: Color,
    val textNeon: Color,
    val textWarning: Color,
    
    val interactiveActive: Color,
    val interactiveHover: Color,
    val interactivePressed: Color,
    val interactiveDisabled: Color,
    val interactiveSuccess: Color,
    val interactiveError: Color,
    
    val playerOne: Color,
    val playerTwo: Color,
    val playerThree: Color,
    val playerFour: Color,
    val winGlow: Color,
    val scoreActive: Color,
    val scoreBackground: Color,
    val scoreGlow: Color,
    
    val particleGlow: Color,
    val scanLine: Color,
    val lensFlare: Color,
    val chromaticAberration: Color,
    
    val buttonPrimary: Color,
    val buttonSecondary: Color,
    val buttonSuccess: Color,
    val buttonWarning: Color,
    val buttonDanger: Color,
    val buttonInfo: Color
)

private val holographicDarkColors = HolographicColorPalette(
    hologramBlue = HolographicColors.hologramBlue,
    hologramCyan = HolographicColors.hologramCyan,
    hologramPurple = HolographicColors.hologramPurple,
    hologramPink = HolographicColors.hologramPink,
    hologramGreen = HolographicColors.hologramGreen,
    hologramYellow = HolographicColors.hologramYellow,
    hologramRed = HolographicColors.hologramRed,
    hologramOrange = HolographicColors.hologramOrange,
    hologramViolet = HolographicColors.hologramViolet,
    hologramAqua = HolographicColors.hologramAqua,
    
    depthBlack = HolographicColors.depthBlack,
    depthVoid = HolographicColors.depthVoid,
    depthMidnight = HolographicColors.depthMidnight,
    depthOcean = HolographicColors.depthOcean,
    depthAbyss = HolographicColors.depthAbyss,
    
    gridPrimary = HolographicColors.gridPrimary,
    gridSecondary = HolographicColors.gridSecondary,
    gridTertiary = HolographicColors.gridTertiary,
    
    uiGlowPrimary = HolographicColors.uiGlowPrimary,
    uiGlowSecondary = HolographicColors.uiGlowSecondary,
    uiGlowTertiary = HolographicColors.uiGlowTertiary,
    uiHighlight = HolographicColors.uiHighlight,
    uiShadow = HolographicColors.uiShadow,
    
    textHologram = HolographicColors.textHologram,
    textGlow = HolographicColors.textGlow,
    textShadow = HolographicColors.textShadow,
    textNeon = HolographicColors.textNeon,
    textWarning = HolographicColors.textWarning,
    
    interactiveActive = HolographicColors.interactiveActive,
    interactiveHover = HolographicColors.interactiveHover,
    interactivePressed = HolographicColors.interactivePressed,
    interactiveDisabled = HolographicColors.interactiveDisabled,
    interactiveSuccess = HolographicColors.interactiveSuccess,
    interactiveError = HolographicColors.interactiveError,
    
    playerOne = HolographicColors.playerOne,
    playerTwo = HolographicColors.playerTwo,
    playerThree = HolographicColors.playerThree,
    playerFour = HolographicColors.playerFour,
    winGlow = HolographicColors.winGlow,
    scoreActive = HolographicColors.scoreActive,
    scoreBackground = HolographicColors.scoreBackground,
    scoreGlow = HolographicColors.scoreGlow,
    
    particleGlow = HolographicColors.particleGlow,
    scanLine = HolographicColors.scanLine,
    lensFlare = HolographicColors.lensFlare,
    chromaticAberration = HolographicColors.chromaticAberration,
    
    buttonPrimary = HolographicColors.buttonPrimary,
    buttonSecondary = HolographicColors.buttonSecondary,
    buttonSuccess = HolographicColors.buttonSuccess,
    buttonWarning = HolographicColors.buttonWarning,
    buttonDanger = HolographicColors.buttonDanger,
    buttonInfo = HolographicColors.buttonInfo
)

private val holographicLightColors = HolographicColorPalette(
    hologramBlue = Color(0xFF0066FF),
    hologramCyan = Color(0xFF00AAFF),
    hologramPurple = Color(0xFF6600FF),
    hologramPink = Color(0xFFCC00FF),
    hologramGreen = Color(0xFF00CC88),
    hologramYellow = Color(0xFFFFCC00),
    hologramRed = Color(0xFFFF3366),
    hologramOrange = Color(0xFFFF6600),
    hologramViolet = Color(0xFF9900FF),
    hologramAqua = Color(0xFF00CCCC),
    
    depthBlack = Color(0xFFCCDDFF),
    depthVoid = Color(0xFFE6F0FF),
    depthMidnight = Color(0xFFAABBEE),
    depthOcean = Color(0xFF88AAFF),
    depthAbyss = Color(0xFF6688DD),
    
    gridPrimary = Color(0x400066FF),
    gridSecondary = Color(0x206600FF),
    gridTertiary = Color(0x10FFFFFF),
    
    uiGlowPrimary = Color(0x6000AAFF),
    uiGlowSecondary = Color(0x40CC00FF),
    uiGlowTertiary = Color(0x20FFFFFF),
    uiHighlight = Color(0x80FFFFFF),
    uiShadow = Color(0x40000088),
    
    textHologram = Color(0xFF001144),
    textGlow = Color(0xFF0066FF),
    textShadow = Color(0xFF0000AA),
    textNeon = Color(0xFFCC00FF),
    textWarning = Color(0xFFFF6600),
    
    interactiveActive = Color(0xFF00AA88),
    interactiveHover = Color(0xFF0099FF),
    interactivePressed = Color(0xFFCC00FF),
    interactiveDisabled = Color(0xFF6666FF),
    interactiveSuccess = Color(0xFF00CC88),
    interactiveError = Color(0xFFFF3366),
    
    playerOne = Color(0xFF00AAFF),
    playerTwo = Color(0xFFCC00FF),
    playerThree = Color(0xFFFFCC00),
    playerFour = Color(0xFF00CC88),
    winGlow = Color(0xFFFFCC00),
    scoreActive = Color(0xFF00CC88),
    scoreBackground = Color(0x2000CC88),
    scoreGlow = Color(0x40CC00FF),
    
    particleGlow = Color(0x60FFFFFF),
    scanLine = Color(0x80FFFFFF),
    lensFlare = Color(0x30FFFFFF),
    chromaticAberration = Color(0x40FF0000),
    
    buttonPrimary = Color(0xFF00AAFF),
    buttonSecondary = Color(0xFFCC00FF),
    buttonSuccess = Color(0xFF00CC88),
    buttonWarning = Color(0xFFFFCC00),
    buttonDanger = Color(0xFFFF3366),
    buttonInfo = Color(0xFF6600FF)
)

object NeonGameTheme {
    val colors: HolographicColorPalette
        @Composable
        get() = LocalHolographicColors.current
    
    val typography: androidx.compose.material3.Typography
        @Composable
        get() = MaterialTheme.typography
    
    val shapes: androidx.compose.material3.Shapes
        @Composable
        get() = MaterialTheme.shapes
    
    val dimensions: HolographicDimensions
        @Composable
        get() = LocalHolographicDimensions.current
}

@Composable
fun NeonGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val holographicColors = if (darkTheme) {
        holographicDarkColors
    } else {
        holographicLightColors
    }
    
    val colorScheme = if (darkTheme) holographicDarkColorScheme else holographicLightColorScheme
    
    CompositionLocalProvider(
        LocalHolographicColors provides holographicColors,
        LocalHolographicDimensions provides HolographicDimensions()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = HolographicTypography,
            content = content
        )
    }
}

private val LocalHolographicColors = staticCompositionLocalOf { holographicDarkColors }

data class HolographicDimensions(
    val buttonHeight: Dp = 60.dp,
    val cardPadding: Dp = 24.dp,
    val chipSize: Dp = 100.dp,
    val gridSpacing: Dp = 60.dp,
    val cornerRadius: Dp = 16.dp,
    val elevation: Dp = 8.dp,
    val borderWidth: Dp = 2.dp
)

private val LocalHolographicDimensions = staticCompositionLocalOf { HolographicDimensions() }

// Theme extension functions
val androidx.compose.ui.Modifier.holographicBorder: androidx.compose.ui.Modifier
    @Composable
    get() = this.border(
        width = NeonGameTheme.dimensions.borderWidth,
        brush = androidx.compose.ui.graphics.Brush.linearGradient(
            colors = HolographicGradients.cyanToPurple
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(NeonGameTheme.dimensions.cornerRadius)
    )

fun androidx.compose.ui.graphics.Color.asHolographicBrush(): androidx.compose.ui.graphics.Brush {
    return androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(
            this.lighten(0.3f),
            this,
            this.darken(0.3f)
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(100f, 100f)
    )
}
