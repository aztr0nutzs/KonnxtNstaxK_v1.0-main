package com.neon.connectsort.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object HolographicGradients {
    val cyanToPurple = listOf(
        NeonColors.hologramCyan,
        NeonColors.hologramBlue,
        NeonColors.hologramPurple,
        NeonColors.hologramPink
    )

    val fullSpectrum = listOf(
        NeonColors.neonRed,
        NeonColors.neonRed,
        NeonColors.neonYellow,
        NeonColors.neonGreen,
        NeonColors.hologramCyan,
        NeonColors.hologramBlue,
        NeonColors.hologramPurple,
        NeonColors.hologramPink
    )

    val depthGradient = listOf(
        NeonColors.depthVoid,
        NeonColors.depthVoid,
        NeonColors.depthMidnight,
        NeonColors.depthOcean
    )

    val neonGlow = listOf(
        Color.Transparent,
        NeonColors.hologramCyan.copy(alpha = 0.3f),
        NeonColors.hologramCyan.copy(alpha = 0.6f),
        NeonColors.hologramCyan.copy(alpha = 0.3f),
        Color.Transparent
    )

    val buttonGlow = listOf(
        NeonColors.hologramCyan,
        NeonColors.hologramPink,
        NeonColors.hologramCyan
    )

    val chipGlow = listOf(
        Color.White.copy(alpha = 0.8f),
        NeonColors.hologramCyan.copy(alpha = 0.6f),
        NeonColors.hologramPurple.copy(alpha = 0.6f),
        Color.White.copy(alpha = 0.8f)
    )

    val winGradient = listOf(
        NeonColors.neonYellow,
        NeonColors.hologramYellow,
        NeonColors.neonYellow,
        NeonColors.neonYellow
    )
}

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

object ParticleColors {
    val hologramParticles = listOf<Color>(
        NeonColors.hologramCyan,
        NeonColors.hologramBlue,
        NeonColors.hologramPurple,
        NeonColors.hologramGreen,
        NeonColors.hologramPink
    )

    val sparkleParticles = listOf<Color>(
        Color.White,
        NeonColors.hologramCyan,
        NeonColors.hologramYellow,
        Color(0x80FFFFFF),
        NeonColors.hologramPink
    )

    val energyParticles = listOf<Color>(
        NeonColors.hologramCyan,
        NeonColors.hologramBlue,
        Color(0x80FFFFFF),
        NeonColors.hologramPurple
    )

    val winParticles = listOf<Color>(
        NeonColors.neonYellow,
        NeonColors.hologramYellow,
        Color.White,
        NeonColors.neonRed
    )
}

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

fun shimmerBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            NeonColors.hologramCyan,
            Color.Transparent
        ),
        start = Offset(-100f, -100f),
        end = Offset(100f, 100f)
    )
}

val Modifier.holographicBorder: Modifier
    @Composable
    get() = this.border(
        width = NeonGameTheme.dimensions.borderWidth,
        brush = Brush.linearGradient(
            colors = HolographicGradients.cyanToPurple
        ),
        shape = RoundedCornerShape(NeonGameTheme.dimensions.cornerRadius)
    )

fun Modifier.holoButton(): Modifier = this
    .holographicBorder()
    .padding(8.dp)
    .clip(RoundedCornerShape(16.dp))

fun Color.asHolographicBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            this.lighten(0.3f),
            this,
            this.darken(0.3f)
        ),
        start = Offset(0f, 0f),
        end = Offset(100f, 100f)
    )
}
