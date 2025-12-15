package com.neon.connectsort.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neon.connectsort.core.domain.CharacterChip
import com.neon.connectsort.core.domain.ChipAbility
import com.neon.connectsort.core.domain.ChipRarity
import com.neon.connectsort.ui.screens.viewmodels.CharacterChipsViewModel
import com.neon.connectsort.ui.theme.*
import com.neon.connectsort.ui.components.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CharacterChipsScreen(
    navController: NavController,
    viewModel: CharacterChipsViewModel
) {
    val characters by viewModel.characters.collectAsState()
    val selectedCharacter by viewModel.selectedCharacter.collectAsState()
    val unlockedCharacters by viewModel.unlockedCharacters.collectAsState()
    val playerCredits by viewModel.playerCredits.collectAsState()
    val equippedAbility by viewModel.equippedAbility.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonColors.depthVoid,
                        NeonColors.depthOcean,
                        NeonColors.depthMidnight
                    ),
                    center = Offset(0.3f, 0.3f),
                    radius = 1.5f
                )
            )
            .drawBehind {
                drawCharacterScreenBackground(size)
            }
    ) {
        HolographicParticleSystem(
            modifier = Modifier.fillMaxSize(),
            particleCount = 80,
            particleColors = ParticleColors.sparkleParticles
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CharacterChipsHeader(navController, playerCredits)

            SelectedCharacterShowcase(
                character = selectedCharacter ?: characters.firstOrNull(),
                onSelect = { viewModel.selectCharacter(it) }
            )

            CharacterRoster(
                characters = characters,
                selectedCharacter = selectedCharacter,
                unlockedCharacters = unlockedCharacters,
                onSelectCharacter = { viewModel.selectCharacter(it) },
                onPurchaseCharacter = { viewModel.purchaseCharacter(it) }
            )

            selectedCharacter?.let { character ->
                CharacterAbilitiesPanel(
                    character = character,
                    equippedAbility = equippedAbility,
                    onSelectAbility = { viewModel.equipAbility(it) }
                )
            }
        }
    }
}

@Composable
fun CharacterChipsHeader(navController: NavController, credits: Int) {
    var glitchOffset by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            glitchOffset = (Math.random() * 8 - 4).toFloat()
            delay(100)
            glitchOffset = 0f
            delay(2000)
        }
    }

    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = "NEURAL PROTOCOLS",
        subtitle = "CHARACTER CHIP INTEGRATION"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HolographicButton(
                text = "← PRIME",
                onClick = { navController.popBackStack() },
                glowColor = NeonColors.hologramPurple,
                secondaryColor = NeonColors.hologramPink,
                icon = "⌫",
                buttonType = ButtonType.SECONDARY,
                modifier = Modifier.width(120.dp)
            )

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = glitchOffset
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NEURAL NETWORK",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NeonColors.hologramCyan
                    )
                    Text(
                        text = "Protocol Version 2.5.1",
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonColors.hologramGreen
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonColors.depthDark.copy(alpha = 0.5f))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                NeonColors.hologramYellow,
                                NeonColors.hologramRed
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "NEURAL CREDITS",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonColors.hologramYellow
                )
                Text(
                    text = "$credits ⚡",
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonColors.hologramYellow
                )
            }
        }
    }
}

@Composable
fun SelectedCharacterShowcase(
    character: CharacterChip?,
    onSelect: (CharacterChip) -> Unit
) {
    character?.let { char ->
        HolographicCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            title = char.name.uppercase(),
            subtitle = char.title
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CharacterPortrait(
                    character = char,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "RARITY: ${char.rarity}",
                        style = MaterialTheme.typography.labelMedium,
                        color = when (char.rarity) {
                            ChipRarity.COMMON -> NeonColors.hologramBlue
                            ChipRarity.RARE -> NeonColors.hologramPurple
                            ChipRarity.EPIC -> NeonColors.hologramPink
                            ChipRarity.LEGENDARY -> NeonColors.hologramYellow
                        }
                    )

                    Text(
                        text = "AFFILIATION: ${char.faction}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonColors.hologramGreen
                    )

                    Text(
                        text = "NEURAL SYNC: ${char.neuralSync}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonColors.hologramCyan
                    )
                }

                HolographicButton(
                    text = if (char.isUnlocked) "ACTIVATE PROTOCOL" else "UNLOCK FOR ${char.price}⚡",
                    onClick = { onSelect(char) },
                    glowColor = if (char.isUnlocked) NeonColors.hologramGreen else NeonColors.hologramYellow,
                    secondaryColor = if (char.isUnlocked) NeonColors.hologramCyan else NeonColors.hologramRed,
                    icon = if (char.isUnlocked) "⚡" else "🔓",
                    buttonType = if (char.isUnlocked) ButtonType.SUCCESS else ButtonType.WARNING,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .width(200.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterPortrait(
    character: CharacterChip,
    modifier: Modifier = Modifier
) {
    var animationPhase by remember { mutableStateOf(0f) }

    LaunchedEffect(character.id) {
        while (true) {
            animationPhase = (animationPhase + 0.01f) % 1f
            delay(16)
        }
    }

    Canvas(modifier = modifier) {
        drawCharacterPortrait(size, character, animationPhase)
    }
}

private fun DrawScope.drawCharacterPortrait(
    size: Size,
    character: CharacterChip,
    phase: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val portraitSize = size.minDimension * 0.8f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                character.color.copy(alpha = 0.3f),
                character.color.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = center,
            radius = portraitSize * 0.8f
        ),
        center = center,
        radius = portraitSize * 0.8f,
        blendMode = BlendMode.Screen
    )

    drawCircle(
        color = character.color.copy(alpha = 0.2f),
        center = center,
        radius = portraitSize * 0.4f
    )

    for (i in 0..2) {
        val ringPhase = (phase + i * 0.1f) % 1f
        val ringRadius = portraitSize * (0.3f + ringPhase * 0.2f)
        val ringAlpha = 1f - ringPhase

        drawCircle(
            color = character.color.copy(alpha = ringAlpha * 0.3f),
            center = center,
            radius = ringRadius,
            style = Stroke(width = 2f)
        )
    }

    drawCircle(
        color = Color.White.copy(alpha = 0.6f),
        center = center,
        radius = portraitSize * 0.12f
    )

    for (i in 0..5) {
        val angle = i * 60f + phase * 360f
        val startRadius = portraitSize * 0.3f
        val endRadius = portraitSize * 0.6f

        val startX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * startRadius
        val startY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * startRadius
        val endX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * endRadius
        val endY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * endRadius

        drawLine(
            color = character.color.copy(alpha = 0.5f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 1f,
            cap = StrokeCap.Round
        )
    }

    when (character.faction) {
        "Neon Syndicate" -> drawNeonSyndicateEmblem(center, portraitSize * 0.2f, character.color)
        "Circuit Breakers" -> drawCircuitBreakersEmblem(center, portraitSize * 0.2f, character.color)
        "Data Wraiths" -> drawDataWraithsEmblem(center, portraitSize * 0.2f, character.color)
        "Neural Collective" -> drawNeuralCollectiveEmblem(center, portraitSize * 0.2f, character.color)
    }
}

@Composable
fun CharacterRoster(
    characters: List<CharacterChip>,
    selectedCharacter: CharacterChip?,
    unlockedCharacters: Set<String>,
    onSelectCharacter: (CharacterChip) -> Unit,
    onPurchaseCharacter: (CharacterChip) -> Unit
) {
    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = "NEURAL ARCHIVE",
        subtitle = "Available Protocols"
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(characters) { character ->
                CharacterChipCard(
                    character = character.copy(
                        isUnlocked = character.id in unlockedCharacters
                    ),
                    isSelected = selectedCharacter?.id == character.id,
                    onClick = {
                        if (character.isUnlocked) {
                            onSelectCharacter(character)
                        } else {
                            onPurchaseCharacter(character)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CharacterChipCard(
    character: CharacterChip,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var hoverScale by remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else hoverScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipCardScale"
    )

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp)
            .scale(animatedScale)
            .clickable(onClick = onClick)
            .onFocusChanged { hoverScale = if (it.isFocused) 1.05f else 1f }
            .graphicsLayer {
                shadowElevation = if (isSelected) 16.dp.toPx() else 8.dp.toPx()
                shape = RoundedCornerShape(16.dp)
                clip = true
            }
            .drawBehind {
                drawCharacterCardBackground(size, character, isSelected)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(character.color.copy(alpha = 0.3f))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = character.color,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = character.icon,
                    style = MaterialTheme.typography.titleLarge,
                    color = character.color
                )
            }

            Text(
                text = character.name,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) character.color else NeonColors.textHologram,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (character.isUnlocked) NeonColors.hologramGreen.copy(alpha = 0.2f)
                        else NeonColors.hologramYellow.copy(alpha = 0.2f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (character.isUnlocked) NeonColors.hologramGreen
                        else NeonColors.hologramYellow,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (character.isUnlocked) "UNLOCKED" else "${character.price}⚡",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (character.isUnlocked) NeonColors.hologramGreen
                    else NeonColors.hologramYellow
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawRarityIndicator(size, character.rarity)
            }
        }
    }
}

@Composable
fun CharacterAbilitiesPanel(
    character: CharacterChip,
    equippedAbility: ChipAbility?,
    onSelectAbility: (ChipAbility) -> Unit
) {
    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = "NEURAL PROTOCOLS",
        subtitle = "Specialized Abilities"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "NEURAL PROFILE",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonColors.hologramCyan
                )
                Text(
                    text = character.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonColors.textHologram.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                character.abilities.forEach { ability ->
                    item {
                        AbilityCard(
                            ability = ability,
                            isSelected = equippedAbility?.name == ability.name,
                            onSelect = { onSelectAbility(ability) }
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "STORY INTEGRATION",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonColors.hologramPurple
                )
                Text(
                    text = character.storyConnection,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonColors.textHologram.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun AbilityCard(
    ability: ChipAbility,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    HolographicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) NeonColors.hologramGreen else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        title = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isSelected) {
                Text(
                    text = "Equipped",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonColors.hologramGreen
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ability.color.copy(alpha = 0.2f))
                        .border(
                            width = 1.dp,
                            color = ability.color,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ability.icon,
                        style = MaterialTheme.typography.titleMedium,
                        color = ability.color
                    )
                }

                Column {
                    Text(
                        text = ability.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = ability.color
                    )
                    Text(
                        text = ability.type,
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonColors.textHologram.copy(alpha = 0.7f)
                    )
                }
            }

            Text(
                text = ability.description,
                style = MaterialTheme.typography.bodySmall,
                color = NeonColors.textHologram.copy(alpha = 0.9f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "⚡",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                    Text(
                        text = "Energy: ${ability.energyCost}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonColors.hologramYellow
                    )
                }

                Text(
                    text = "CD: ${ability.cooldown}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonColors.hologramBlue
                )
            }
        }
    }
}

private fun DrawScope.drawNeonSyndicateEmblem(center: Offset, size: Float, color: Color) {
    val points = listOf(
        Offset(center.x, center.y - size),
        Offset(center.x + size * 0.866f, center.y + size * 0.5f),
        Offset(center.x - size * 0.866f, center.y + size * 0.5f)
    )

    drawPath(
        path = Path().apply {
            moveTo(points[0].x, points[0].y)
            points.forEachIndexed { index, point ->
                if (index > 0) lineTo(point.x, point.y)
            }
            close()
        },
        color = color,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawCircuitBreakersEmblem(center: Offset, size: Float, color: Color) {
    drawCircle(
        color = color,
        center = center,
        radius = size,
        style = Stroke(width = 2f)
    )

    drawLine(
        color = color,
        start = Offset(center.x - size, center.y),
        end = Offset(center.x + size, center.y),
        strokeWidth = 2f
    )

    drawLine(
        color = color,
        start = Offset(center.x, center.y - size),
        end = Offset(center.x, center.y + size),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawDataWraithsEmblem(center: Offset, size: Float, color: Color) {
    drawPath(
        path = Path().apply {
            moveTo(center.x, center.y - size)
            cubicTo(
                center.x + size, center.y - size,
                center.x + size, center.y + size,
                center.x, center.y + size
            )
            cubicTo(
                center.x - size, center.y + size,
                center.x - size, center.y - size,
                center.x, center.y - size
            )
        },
        color = color,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawNeuralCollectiveEmblem(center: Offset, size: Float, color: Color) {
    val innerRadius = size * 0.6f
    val outerRadius = size

    for (i in 0..5) {
        val angle = i * 60f
        val innerX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * innerRadius
        val innerY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * innerRadius
        val outerX = center.x + cos(Math.toRadians(angle.toDouble())).toFloat() * outerRadius
        val outerY = center.y + sin(Math.toRadians(angle.toDouble())).toFloat() * outerRadius

        drawCircle(
            color = color,
            center = Offset(innerX, innerY),
            radius = 4f
        )

        drawLine(
            color = color,
            start = Offset(innerX, innerY),
            end = Offset(outerX, outerY),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawCharacterCardBackground(
    size: Size,
    character: CharacterChip,
    isSelected: Boolean
) {
    val cornerRadius = 16.dp.toPx()

    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                NeonColors.depthDark,
                NeonColors.depthMidnight,
                NeonColors.depthOcean
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        ),
        size = size,
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )

    if (isSelected) {
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    character.color.copy(alpha = 0.8f),
                    character.color.copy(alpha = 0.4f),
                    character.color.copy(alpha = 0.8f)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            topLeft = Offset(2f, 2f),
            size = Size(size.width - 4f, size.height - 4f),
            cornerRadius = CornerRadius(cornerRadius - 2, cornerRadius - 2),
            style = Stroke(width = 3f)
        )
    }

    val gridSpacing = 20f
    for (x in 0..(size.width / gridSpacing).toInt()) {
        drawLine(
            color = character.color.copy(alpha = 0.1f),
            start = Offset(x * gridSpacing, 0f),
            end = Offset(x * gridSpacing, size.height),
            strokeWidth = 0.5f
        )
    }

    for (y in 0..(size.height / gridSpacing).toInt()) {
        drawLine(
            color = character.color.copy(alpha = 0.1f),
            start = Offset(0f, y * gridSpacing),
            end = Offset(size.width, y * gridSpacing),
            strokeWidth = 0.5f
        )
    }
}

private fun DrawScope.drawRarityIndicator(size: Size, rarity: ChipRarity) {
    val color = when (rarity) {
        ChipRarity.COMMON -> NeonColors.hologramBlue
        ChipRarity.RARE -> NeonColors.hologramPurple
        ChipRarity.EPIC -> NeonColors.hologramPink
        ChipRarity.LEGENDARY -> NeonColors.hologramYellow
    }

    drawCircle(
        color = color,
        center = Offset(size.width / 2, size.height / 2),
        radius = size.minDimension / 2
    )

    if (rarity == ChipRarity.LEGENDARY) {
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            center = Offset(size.width / 2, size.height / 2),
            radius = size.minDimension / 3,
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawCharacterScreenBackground(size: Size) {
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF000022),
                Color(0xFF0A0A3A),
                Color(0xFF001144),
                Color(0xFF000022)
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        ),
        size = size
    )

    val nodeCount = 15
    val nodes = mutableListOf<Offset>()

    for (i in 0 until nodeCount) {
        val x = (i * 137) % size.width.toInt() + size.width * 0.1f
        val y = (i * 89) % size.height.toInt() + size.height * 0.1f
        nodes.add(Offset(x, y))

        drawCircle(
            color = NeonColors.hologramCyan.copy(alpha = 0.2f),
            center = Offset(x, y),
            radius = 4f
        )
    }

    for (i in nodes.indices) {
        for (j in i + 1 until nodes.size) {
            val distance = (nodes[i] - nodes[j]).getDistance()
            if (distance < 200f) {
                drawLine(
                    color = NeonColors.hologramCyan.copy(alpha = 0.1f),
                    start = nodes[i],
                    end = nodes[j],
                    strokeWidth = 1f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
