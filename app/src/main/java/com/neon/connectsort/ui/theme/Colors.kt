package com.neon.connectsort.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Semantic tokens derived from the low-level [NeonColors] palette.
 * These values are intended to be the single source of truth for
 * primary, secondary, accent, glow, and background surfaces.
 */
object NeonPalette {
    val primary = NeonColors.hologramCyan
    val secondary = NeonColors.hologramPurple
    val accent = NeonColors.hologramPink
    val glow = NeonColors.hologramGreen
    val background = NeonColors.depthVoid
    val surface = NeonColors.depthMidnight
    val surfaceVariant = NeonColors.depthOcean
    val highlight = NeonColors.hologramYellow
    val error = NeonColors.hologramRed

    val onPrimary = Color.Black
    val onSecondary = Color.Black
    val onBackground = NeonColors.textHologram
    val onSurface = NeonColors.textHologram.copy(alpha = 0.9f)
    val onSurfaceVariant = NeonColors.textHologram.copy(alpha = 0.7f)
    val onError = Color.Black

    val glowAccent = NeonColors.hologramCyan
    val depthGradient = listOf(
        NeonColors.depthVoid,
        NeonColors.depthMidnight,
        NeonColors.depthOcean
    )
}
