package com.neon.connectsort.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val HologramFont = FontFamily.SansSerif
private val DigitalFont = FontFamily.Monospace

val HolographicTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = HologramFont,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        letterSpacing = 1.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HologramFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = HologramFont,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = HologramFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HologramFont,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = HologramFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = DigitalFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = DigitalFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = Color.White
    ),
    bodySmall = TextStyle(
        fontFamily = DigitalFont,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontFamily = DigitalFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = DigitalFont,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp
    )
)
