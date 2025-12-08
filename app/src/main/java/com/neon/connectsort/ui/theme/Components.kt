package com.neon.connectsort.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    neonColor: Color = NeonColors.hologramCyan
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "buttonScale"
    )
    
    Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = neonColor.copy(alpha = 0.5f)
            )
            .border(
                width = 2.dp,
                color = neonColor.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonColors.cardBackground.copy(alpha = 0.8f),
            contentColor = neonColor,
            disabledContainerColor = Color.DarkGray,
            disabledContentColor = Color.Gray
        ),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled && !isLoading,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        if (isLoading) {
            NeonLoadingIndicator()
        } else {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    neonColor: Color = NeonColors.hologramCyan,
    content: @Composable () -> Unit
) {
    var glowPhase by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            glowPhase = (glowPhase + 0.02f) % 1f
            delay(16) // ~60fps
        }
    }
    
    val glowAlpha = (0.3f + 0.2f * kotlin.math.sin(glowPhase.toDouble() * 2 * kotlin.math.PI).toFloat()).coerceIn(0f, 1f)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = neonColor.copy(alpha = glowAlpha)
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        neonColor.copy(alpha = 0.7f),
                        neonColor.copy(alpha = 0.3f),
                        neonColor.copy(alpha = 0.7f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(100f, 100f)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = NeonColors.cardBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
fun NeonText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight.Normal,
    neonColor: Color = NeonColors.hologramCyan,
    withGlow: Boolean = true
) {
    Text(
        text = text,
        modifier = if (withGlow) {
            modifier.drawBehind {
                drawCircle(
                    color = neonColor.copy(alpha = 0.2f),
                    radius = size.width / 2,
                    center = center
                )
            }
        } else modifier,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        color = neonColor,
        style = NeonGameTheme.typography.bodyMedium
    )
}

@Composable
fun ArcadePanel(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    NeonCard(
        modifier = modifier,
        neonColor = NeonColors.hologramBlue
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            title?.let {
                NeonText(
                    text = it,
                    fontSize = 20,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.hologramYellow,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            content()
        }
    }
}

@Composable
fun NeonLoadingIndicator() {
    var rotation by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            rotation = (rotation + 5f) % 360f
            delay(16)
        }
    }
    
    Box(
        modifier = Modifier
            .size(24.dp)
            .drawBehind {
                drawCircle(
                    color = NeonColors.hologramCyan,
                    center = center,
                    radius = size.minDimension / 2,
                    style = Stroke(width = 3f)
                )
                drawArc(
                    color = NeonColors.hologramPink,
                    startAngle = rotation,
                    sweepAngle = 120f,
                    useCenter = false,
                    size = size,
                    style = Stroke(width = 3f)
                )
            }
    )
}

fun createNeonBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            NeonColors.hologramCyan,
            NeonColors.hologramPink,
            NeonColors.hologramBlue
        ),
        start = Offset(0f, 0f),
        end = Offset(100f, 100f)
    )
}
