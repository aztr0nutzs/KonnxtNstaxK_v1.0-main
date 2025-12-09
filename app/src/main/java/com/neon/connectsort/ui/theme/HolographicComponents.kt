package com.neon.connectsort.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

// 3D Holographic Button with multiple animation effects
@Composable
fun HolographicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    glowColor: Color = NeonColors.hologramCyan,
    secondaryColor: Color = NeonColors.hologramPurple,
    icon: String? = null,
    buttonType: ButtonType = ButtonType.PRIMARY
) {
    var isPressed by remember { mutableStateOf(false) }
    var isHovered by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    
    // Multiple animation states
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    
    val glowIntensity by animateFloatAsState(
        targetValue = if (isHovered) 1.2f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "glowIntensity"
    )
    
    val pulsePhase = remember { Animatable(0f) }
    
    // Pulsing animation
    LaunchedEffect(Unit) {
        while (true) {
            pulsePhase.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }
    
    // Button style based on type
    val buttonColors = when (buttonType) {
        ButtonType.PRIMARY -> Pair(glowColor, secondaryColor)
        ButtonType.SECONDARY -> Pair(NeonColors.hologramPurple, NeonColors.hologramPink)
        ButtonType.SUCCESS -> Pair(NeonColors.hologramGreen, NeonColors.hologramCyan)
        ButtonType.WARNING -> Pair(NeonColors.hologramYellow, NeonColors.hologramRed)
        ButtonType.DANGER -> Pair(NeonColors.hologramRed, NeonColors.hologramYellow)
    }
    
    Canvas(
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth()
            .scale(pressScale)
            .clickable(
                enabled = enabled && !isLoading,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { /* Handled by clickable */ }
                )
            }
            .onFocusChanged { isHovered = it.isFocused }
    ) {
        // 3D Button base with depth
        draw3DButton(
            width = size.width,
            height = size.height,
            isPressed = isPressed,
            glowIntensity = glowIntensity,
            primaryColor = buttonColors.first,
            secondaryColor = buttonColors.second,
            pulsePhase = pulsePhase.value
        )
        
        // Draw text
        drawIntoCanvas { canvas ->
            val paint = android.graphics.Paint().apply {
                this.color = Color.White.toArgb()
                textSize = 18f
                isAntiAlias = true
            }
            canvas.nativeCanvas.drawText(if (isLoading) "..." else text, size.width / 2 - if (isLoading) 20f else size.width * 0.4f, size.height / 2 - 15f, paint)
        }
        
        // Draw icon if present
        icon?.let {
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    this.color = Color.White.toArgb()
                    textSize = 18f
                    isAntiAlias = true
                }
                canvas.nativeCanvas.drawText(it, size.width * 0.1f, size.height / 2 - 15f, paint)
            }
        }
        
        // Loading animation
        if (isLoading) {
            drawCircle(
                color = buttonColors.first,
                center = Offset(size.width * 0.8f, size.height / 2),
                radius = 8f,
                alpha = (sin(pulsePhase.value * 2 * PI) * 0.5f + 0.5f).toFloat()
            )
        }
    }
}

private fun DrawScope.draw3DButton(
    width: Float,
    height: Float,
    isPressed: Boolean,
    glowIntensity: Float,
    primaryColor: Color,
    secondaryColor: Color,
    pulsePhase: Float
) {
    val cornerRadius = 16f
    val buttonDepth = if (isPressed) 4f else 8f
    
    // Outer glow effect
    val glowRadius = 20f * glowIntensity
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.3f),
                primaryColor.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(width / 2, height / 2),
            radius = glowRadius
        ),
        center = Offset(width / 2, height / 2),
        radius = glowRadius
    )
    
    // Button base (3D depth effect)
    val buttonTop = if (isPressed) buttonDepth else 0f
    val buttonHeight = height - buttonDepth
    
    // Top surface with gradient
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                primaryColor,
                secondaryColor,
                primaryColor
            ),
            start = Offset(0f, 0f),
            end = Offset(width, height)
        ),
        topLeft = Offset(0f, buttonTop),
        size = Size(width, buttonHeight),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
    
    // Inner glow border
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.8f),
                secondaryColor.copy(alpha = 0.8f)
            ),
            start = Offset(0f, 0f),
            end = Offset(width, height)
        ),
        topLeft = Offset(2f, buttonTop + 2f),
        size = Size(width - 4f, buttonHeight - 4f),
        cornerRadius = CornerRadius(cornerRadius - 2, cornerRadius - 2),
        style = Stroke(width = 2f)
    )
    
    // Pulsing scan line
    val scanY = buttonHeight * pulsePhase
    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(4f, scanY),
        end = Offset(width - 4f, scanY),
        strokeWidth = 1f,
        cap = StrokeCap.Round
    )
    
    // 3D edge highlights
    drawRoundRect(
        color = Color.White.copy(alpha = 0.2f),
        topLeft = Offset(4f, buttonTop + 4f),
        size = Size(width - 8f, 2f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    
    // Bottom shadow for depth
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.3f),
        topLeft = Offset(0f, height - buttonDepth),
        size = Size(width, buttonDepth),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
}

// 3D Holographic Card with floating animation
@Composable
fun HolographicCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    elevation: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    var hoverOffset by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "cardFloat")
    val floatPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3000
                0.0f at 0 with LinearEasing
                0.5f at 1500 with LinearEasing
                1.0f at 3000 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "floatPhase"
    )
    
    val floatOffset = sin(floatPhase * 2 * PI).toFloat() * 4f
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = floatOffset + hoverOffset
                shadowElevation = elevation.toPx()
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
            .onFocusChanged { hoverOffset = if (it.isFocused) -4f else 0f }
            .drawBehind {
                drawHolographicCardBackground(size, floatPhase)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawGlitchEffect()
                )
            }
            
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonColors.hologramGreen
                )
            }
            
            content()
        }
        
        // Corner accents
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCornerAccents(size)
        }
    }
}

private fun DrawScope.drawHolographicCardBackground(size: Size, floatPhase: Float) {
    // Base with gradient
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = HolographicGradients.depthGradient,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        ),
        size = size,
        cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
    )
    
    // Grid pattern
    drawGridPattern(size, floatPhase)
    
    // Inner glow border
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                NeonColors.hologramCyan.copy(alpha = 0.3f),
                NeonColors.hologramPurple.copy(alpha = 0.3f)
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        ),
        topLeft = Offset(2f, 2f),
        size = Size(size.width - 4f, size.height - 4f),
        cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
        style = Stroke(width = 2f)
    )
    
    // Scanning light effect
    val scanY = size.height * floatPhase
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                NeonColors.hologramCyan.copy(alpha = 0.1f),
                Color.Transparent
            ),
            startY = scanY - 50f,
            endY = scanY + 50f
        ),
        topLeft = Offset(0f, scanY - 50f),
        size = Size(size.width, 100f)
    )
}

private fun DrawScope.drawGridPattern(size: Size, phase: Float) {
    val gridSpacing = 40f
    val lineWidth = 1f
    
    // Vertical lines
    for (x in 0..(size.width / gridSpacing).toInt()) {
        val xPos = x * gridSpacing + (phase * gridSpacing) % gridSpacing
        drawLine(
            color = NeonColors.hologramCyan.copy(alpha = 0.3f),
            start = Offset(xPos, 0f),
            end = Offset(xPos, size.height),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )
    }
    
    // Horizontal lines
    for (y in 0..(size.height / gridSpacing).toInt()) {
        val yPos = y * gridSpacing + (phase * gridSpacing) % gridSpacing
        drawLine(
            color = NeonColors.hologramPurple.copy(alpha = 0.2f),
            start = Offset(0f, yPos),
            end = Offset(size.width, yPos),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )
    }
    
    // Diagonal lines
    val diagonalCount = 10
    for (i in -diagonalCount..diagonalCount) {
        val offset = size.height * (i.toFloat() / diagonalCount) + phase * 100f
        drawLine(
            color = NeonColors.hologramPurple.copy(alpha = 0.1f),
            start = Offset(0f, offset),
            end = Offset(size.width, offset + size.width * 0.5f),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawCornerAccents(size: Size) {
    val cornerSize = 24f
    val cornerOffset = 12f
    
    // Top-left corner
    drawLine(
        color = NeonColors.hologramCyan,
        start = Offset(cornerOffset, cornerOffset),
        end = Offset(cornerOffset + cornerSize, cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = NeonColors.hologramCyan,
        start = Offset(cornerOffset, cornerOffset),
        end = Offset(cornerOffset, cornerOffset + cornerSize),
        strokeWidth = 2f
    )
    
    // Top-right corner
    drawLine(
        color = NeonColors.hologramPurple,
        start = Offset(size.width - cornerOffset - cornerSize, cornerOffset),
        end = Offset(size.width - cornerOffset, cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = NeonColors.hologramPurple,
        start = Offset(size.width - cornerOffset, cornerOffset),
        end = Offset(size.width - cornerOffset, cornerOffset + cornerSize),
        strokeWidth = 2f
    )
    
    // Bottom-left corner
    drawLine(
        color = NeonColors.hologramGreen,
        start = Offset(cornerOffset, size.height - cornerOffset - cornerSize),
        end = Offset(cornerOffset, size.height - cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = NeonColors.hologramGreen,
        start = Offset(cornerOffset, size.height - cornerOffset),
        end = Offset(cornerOffset + cornerSize, size.height - cornerOffset),
        strokeWidth = 2f
    )
    
    // Bottom-right corner
    drawLine(
        color = NeonColors.hologramPink,
        start = Offset(size.width - cornerOffset, size.height - cornerOffset - cornerSize),
        end = Offset(size.width - cornerOffset, size.height - cornerOffset),
        strokeWidth = 2f
    )
    drawLine(
        color = NeonColors.hologramPink,
        start = Offset(size.width - cornerOffset - cornerSize, size.height - cornerOffset),
        end = Offset(size.width - cornerOffset, size.height - cornerOffset),
        strokeWidth = 2f
    )
}

// 3D Holographic Chip with depth and shine
@Composable
fun HolographicChip(
    value: Int = 500,
    color: Color = NeonColors.neonCyan,
    isActive: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    var rotation by remember { mutableStateOf(0f) }
    var hoverScale by remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = hoverScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipScale"
    )
    
    // Continuous rotation for active chips
    LaunchedEffect(isActive) {
        if (isActive) {
            while (true) {
                rotation = (rotation + 0.5f) % 360f
                delay(16)
            }
        }
    }
    
    Canvas(
        modifier = modifier
            .size(100.dp)
            .scale(animatedScale)
            .graphicsLayer {
                rotationZ = rotation
                clip = true
            }
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .onFocusChanged { hoverScale = if (it.isFocused) 1.1f else 1f }
    ) {
        draw3DChip(size, value, color, isActive)
    }
}

private fun DrawScope.draw3DChip(size: Size, value: Int, color: Color, isActive: Boolean) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = size.minDimension / 2 - 8f
    
    // Outer glow for active chips
    if (isActive) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.5f),
                    color.copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 1.2f
            ),
            center = center,
            radius = radius * 1.2f
        )
    }
    
    // 3D Chip body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color,
                color.darken(0.3f),
                color.darken(0.6f)
            ),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius
    )
    
    // Metallic edge
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.8f),
                Color.LightGray.copy(alpha = 0.6f),
                Color.DarkGray.copy(alpha = 0.8f)
            ),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius,
        style = Stroke(width = 4f)
    )
    
    // Inner highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
        radius = radius * 0.3f,
        blendMode = BlendMode.Plus
    )
    
    // Value text
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            this.color = Color.White.toArgb()
            textSize = 20f
            isAntiAlias = true
        }
        canvas.nativeCanvas.drawText(value.toString(), center.x - radius * 0.4f, center.y - radius * 0.3f, paint)
    }
    
    // Shine effect
    drawArc(
        color = Color.White.copy(alpha = 0.5f),
        startAngle = -45f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(center.x - radius * 0.8f, center.y - radius * 0.8f),
        size = Size(radius * 1.6f, radius * 1.6f),
        style = Stroke(width = 2f)
    )
}

// Glitch effect modifier
fun Modifier.drawGlitchEffect(): Modifier = this.then(
    Modifier.drawWithCache {
        onDrawWithContent {
            drawContent()
            
            // Glitch lines
            val glitchOffsetX = (Math.random() * 4 - 2).toFloat()
            val glitchOffsetY = (Math.random() * 4 - 2).toFloat()
            
            drawRect(
                color = NeonColors.hologramCyan.copy(alpha = 0.3f),
                topLeft = Offset(glitchOffsetX, glitchOffsetY),
                size = size,
                blendMode = BlendMode.Screen
            )
            
            drawRect(
                color = NeonColors.hologramPurple.copy(alpha = 0.2f),
                topLeft = Offset(-glitchOffsetX, -glitchOffsetY),
                size = size,
                blendMode = BlendMode.Screen
            )
        }
    }
)

// Particle system for backgrounds
@Composable
fun HolographicParticleSystem(
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    particleColors: List<Color> = ParticleColors.hologramParticles
) {
    val particles = remember { mutableStateListOf<ParticleData>() }
    
    LaunchedEffect(Unit) {
        // Initialize particles
        repeat(particleCount) {
            particles.add(createParticle(particleColors))
        }
        
        // Animation loop
        while (true) {
            particles.forEachIndexed { index, particle ->
                particles[index] = updateParticle(particle)
            }
            delay(16)
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawParticle(particle)
        }
    }
}

private data class ParticleData(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val size: Float,
    val color: Color,
    val life: Float,
    val maxLife: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

private fun createParticle(colors: List<Color>): ParticleData {
    val color = colors.random()
    return ParticleData(
        x = Random.nextFloat() * 1000f,
        y = Random.nextFloat() * 1000f,
        velocityX = (Random.nextFloat() - 0.5f) * 2f,
        velocityY = (Random.nextFloat() - 0.5f) * 2f,
        size = Random.nextFloat() * 8f + 2f,
        color = color,
        life = Random.nextFloat() * 100f,
        maxLife = 100f,
        rotation = Random.nextFloat() * 360f,
        rotationSpeed = (Random.nextFloat() - 0.5f) * 5f
    )
}

private fun updateParticle(particle: ParticleData): ParticleData {
    return particle.copy(
        x = particle.x + particle.velocityX,
        y = particle.y + particle.velocityY,
        rotation = particle.rotation + particle.rotationSpeed,
        life = particle.life - 0.5f
    ).let { updated ->
        if (updated.life <= 0) createParticle(ParticleColors.hologramParticles)
        else updated
    }
}

private fun DrawScope.drawParticle(particle: ParticleData) {
    drawCircle(
        color = particle.color.copy(alpha = particle.life / particle.maxLife),
        center = Offset(particle.x, particle.y),
        radius = particle.size,
        blendMode = BlendMode.Screen
    )
}

// Enum for button types
enum class ButtonType {
    PRIMARY,
    SECONDARY,
    SUCCESS,
    WARNING,
    DANGER
}
