package com.neon.connectsort.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val holographicDarkColorScheme = darkColorScheme(
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

object NeonGameTheme {
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
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalHolographicDimensions provides HolographicDimensions()
    ) {
        MaterialTheme(
            colorScheme = holographicDarkColorScheme,
            typography = HolographicTypography,
            content = content
        )
    }
}

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
