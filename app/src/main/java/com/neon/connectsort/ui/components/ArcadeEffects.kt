package com.neon.connectsort.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.ParticleColors
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SlimeDripOverlay(
    modifier: Modifier = Modifier,
    color: Color = NeonColors.neonGreen.copy(alpha = 0.35f),
    dripHeight: Dp = 24.dp,
    intensity: Float = 1f
) {
    val transition = rememberInfiniteTransition()
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1600 / intensity).coerceAtLeast(400f).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val height = (dripHeight.toPx() * intensity).coerceAtLeast(12f)
        val width = size.width
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, height)
            var x = 0f
            val segments = 8
            while (x < width) {
                val controlX = x + (width / segments) / 2f
                val controlY = (height + sin((x / width + offset).toDouble() * PI * 2) * height * 0.4f).toFloat()
                val nextX = x + width / segments
                quadraticBezierTo(controlX, controlY, nextX, height)
                x = nextX
            }
            lineTo(width, 0f)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.7f),
                    color.copy(alpha = 0.3f)
                )
            ),
            style = Fill
        )
        drawPath(
            path = path,
            color = color.copy(alpha = 0.4f),
            style = Stroke(width = 2f)
        )
    }
}

@Composable
fun NeonParticleField(
    modifier: Modifier = Modifier,
    palette: List<Color> = ParticleColors.hologramParticles,
    particleCount: Int = 20,
    intensity: Float = 1f
) {
    val particles = remember {
        val random = Random(42)
        List(particleCount) {
            NeonParticle(
                x = random.nextFloat(),
                y = random.nextFloat(),
                radius = random.nextFloat() * 3f + 1f,
                speed = random.nextFloat() * 0.2f + 0.05f,
                color = palette[random.nextInt(palette.size)]
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        particles.forEach { particle ->
            val x = (particle.x + drift * particle.speed * intensity) % 1f
            val y = (particle.y + drift * particle.speed * intensity * 0.8f) % 1f
            drawCircle(
                color = particle.color,
                radius = particle.radius * intensity,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

@Composable
fun NeonPulseRing(
    modifier: Modifier = Modifier,
    color: Color,
    radius: Dp = 40.dp,
    pulseDuration: Int = 1200
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                center = center,
                radius = radius.toPx() * scale
            ),
            radius = radius.toPx() * scale
        )
    }
}

private data class NeonParticle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val color: Color
)
