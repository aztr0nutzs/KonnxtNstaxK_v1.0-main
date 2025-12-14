package com.neon.connectsort.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neon.connectsort.ui.components.BallSortArcadeBackground
import com.neon.connectsort.ui.components.HolographicButton
import com.neon.connectsort.ui.components.NeonParticleField
import com.neon.connectsort.ui.components.NeonPulseRing
import com.neon.connectsort.ui.components.SlimeDripOverlay
import com.neon.connectsort.ui.screens.viewmodels.BallAnimationState
import com.neon.connectsort.ui.screens.viewmodels.BallSortGameState
import com.neon.connectsort.ui.screens.viewmodels.BallSortHint
import com.neon.connectsort.ui.screens.viewmodels.BallSortViewModel
import com.neon.connectsort.navigation.activeStoryChapterId
import com.neon.connectsort.navigation.publishStoryResult
import com.neon.connectsort.ui.theme.NeonButton
import com.neon.connectsort.ui.theme.NeonCard
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.NeonText
import com.neon.game.common.GameMode
import com.neon.game.common.PowerUp

@Composable
fun BallSortScreen(
    navController: NavController,
    viewModel: BallSortViewModel,
    initialLevel: Int = 1
) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedTube by viewModel.selectedTube.collectAsState()
    val animationState by viewModel.animationState.collectAsState()
    val hintMove by viewModel.hintMove.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    LaunchedEffect(initialLevel) {
        viewModel.loadLevel(initialLevel)
    }

    val storyChapterId = navController.activeStoryChapterId()
    var storyResultSent by remember { mutableStateOf(false) }

    LaunchedEffect(gameState.isLevelComplete, storyChapterId, storyResultSent) {
        if (storyChapterId != null && gameState.isLevelComplete && !storyResultSent) {
            navController.publishStoryResult(true)
            storyResultSent = true
        }
        if (!gameState.isLevelComplete && storyResultSent) {
            storyResultSent = false
        }
    }

    val actionHint = when {
        isPaused -> "Game paused. Tap resume to keep sorting."
        hintMove != null -> "Hint: move from ${hintMove!!.fromTube + 1} to ${hintMove!!.toTube + 1}."
        selectedTube != null -> "Selected tube ${selectedTube!! + 1}. Tap a tube to move into."
        else -> "Tap a tube to grab the top neon ball."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NeonColors.depthDark, NeonColors.depthVoid)
                )
            )
    ) {
        NeonParticleField(modifier = Modifier.matchParentSize(), intensity = 0.5f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BallSortHeader(
                navController = navController,
                gameState = gameState
            )

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                neonColor = if (gameState.isLevelComplete) NeonColors.neonYellow else NeonColors.hologramGreen
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    BallSortArcadeBackground()

                    SlimeDripOverlay(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .height(28.dp),
                        intensity = if (gameState.isLevelComplete) 1.4f else 0.9f,
                        color = NeonColors.hologramMagenta.copy(alpha = 0.4f)
                    )

                    NeonParticleField(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(16.dp),
                        intensity = if (gameState.level % 2 == 0) 0.7f else 0.4f
                    )

                    HolographicPulseFrame(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .align(Alignment.TopCenter),
                        intensity = if (gameState.isLevelComplete) 1.35f else 0.85f
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = actionHint,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )

                        TubeGrid(
                            tubes = gameState.tubes,
                            selectedTube = selectedTube,
                            hintMove = hintMove,
                            animationState = animationState,
                            isPaused = isPaused,
                            onTubeClick = { viewModel.selectTube(it) }
                        )
                    }

                    if (gameState.isLevelComplete) {
                        NeonPulseRing(
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.Center),
                            color = NeonColors.neonYellow
                        )
                    }
                }
            }

            Row {
                BallSortInfoRow(gameState = gameState, hint = hintMove)
            }

            if (gameState.gameMode == GameMode.COMPETITIVE) {
                PlayerStats(gameState)
            }

            PowerUps(gameState.enabledPowerUps) { viewModel.usePowerUp(it) }

            BallSortControlRow(
                isPaused = isPaused,
                onPauseToggle = { viewModel.togglePause() },
                onHint = { viewModel.requestHint() },
                onReset = { viewModel.resetLevel() },
                onNext = { viewModel.loadLevel(gameState.level + 1) },
                canAdvance = gameState.isLevelComplete
            )
        }

        if (isPaused) {
            PauseOverlay()
        }

        if (gameState.isLevelComplete) {
            VictoryScreen(gameState = gameState, onNextLevel = { viewModel.loadLevel(gameState.level + 1) })
        }
    }
}


@Composable
private fun BallSortHeader(
    navController: NavController,
    gameState: BallSortGameState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonButton(
            text = "← BACK",
            onClick = { navController.popBackStack() },
            neonColor = NeonColors.hologramBlue,
            modifier = Modifier.width(110.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NeonText(
                text = "BALL SORT",
                fontSize = 24,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.hologramGreen
            )
            NeonText(
                text = "LEVEL ${gameState.level}",
                fontSize = 16,
                fontWeight = FontWeight.SemiBold,
                neonColor = NeonColors.hologramYellow
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GameModeDisplay(gameState.gameMode)
            if (gameState.gameMode == GameMode.TIMED || gameState.gameMode == GameMode.COMPETITIVE) {
                Text(text = "Time: ${gameState.timer}", color = Color.White)
            }
        }
    }
}

@Composable
private fun TubeGrid(
    tubes: List<List<Color>>,
    selectedTube: Int?,
    hintMove: BallSortHint?,
    animationState: BallAnimationState?,
    isPaused: Boolean,
    onTubeClick: (Int) -> Unit
) {
    val columns = when (tubes.size) {
        in 1..4 -> 2
        in 5..8 -> 3
        else -> 4
    }
    val rows = (tubes.size + columns - 1) / columns

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < tubes.size) {
                        TubeView(
                            balls = tubes[index],
                            isSelected = selectedTube == index,
                            isAnimating = animationState?.fromTube == index || animationState?.toTube == index,
                            isHintSource = hintMove?.fromTube == index,
                            isHintTarget = hintMove?.toTube == index,
                            animationProgress = animationState?.progress ?: 0f,
                            isPaused = isPaused,
                            onClick = { onTubeClick(index) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TubeView(
    balls: List<Color>,
    isSelected: Boolean,
    isAnimating: Boolean,
    isHintSource: Boolean,
    isHintTarget: Boolean,
    animationProgress: Float,
    isPaused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isHintTarget -> NeonColors.neonYellow
        isHintSource -> NeonColors.hologramPink
        isSelected -> NeonColors.hologramCyan
        else -> NeonColors.neonBlue.copy(alpha = 0.5f)
    }
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.08f else 1f,
        animationSpec = tween(durationMillis = 200)
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .aspectRatio(0.6f)
            .clip(RoundedCornerShape(16.dp))
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonColors.depthMidnight,
                        NeonColors.depthVoid
                    )
                )
            )
            .clickable(enabled = !isPaused, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            balls.forEachIndexed { index, ballColor ->
                val ballScale = if (isHintSource && index == balls.lastIndex) {
                    1f + animationProgress * 0.6f
                } else 1f
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ballColor)
                        .border(
                            width = 1.dp,
                            color = ballColor.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .graphicsLayer(scaleX = ballScale, scaleY = ballScale)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            repeat(4 - balls.size) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = NeonColors.depthVoid.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(14.dp)
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        if (isHintTarget || isHintSource) {
            NeonPulseRing(
                modifier = Modifier
                    .matchParentSize()
                    .padding(6.dp),
                color = if (isHintTarget) NeonColors.neonYellow else NeonColors.hologramPink,
                pulseDuration = 900
            )
        }
    }
}

@Composable
private fun RowScope.BallSortInfoRow(gameState: BallSortGameState, hint: BallSortHint?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoBadge("MOVES", gameState.moves.toString(), NeonColors.neonCyan)
        InfoBadge("BEST", gameState.bestMoves?.toString() ?: "--", NeonColors.hologramGreen)
        InfoBadge("TUBES", gameState.tubes.size.toString(), NeonColors.neonYellow)
        InfoBadge(
            "HINT",
            text = hint?.let { "${it.fromTube + 1}->${it.toTube + 1}" } ?: "AVAILABLE",
            color = if (hint != null) NeonColors.neonYellow else NeonColors.hologramPink
        )
    }
}

@Composable
private fun RowScope.InfoBadge(label: String, text: String, color: Color) {
    NeonCard(
        modifier = Modifier
            .weight(1f)
            .height(80.dp),
        neonColor = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            NeonText(
                text = text,
                fontSize = 20,
                fontWeight = FontWeight.Bold,
                neonColor = color
            )
        }
    }
}

@Composable
private fun BallSortControlRow(
    isPaused: Boolean,
    onPauseToggle: () -> Unit,
    onHint: () -> Unit,
    onReset: () -> Unit,
    onNext: () -> Unit,
    canAdvance: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HolographicButton(
            text = if (isPaused) "RESUME" else "PAUSE",
            onClick = onPauseToggle,
            glowColor = NeonColors.hologramBlue,
            modifier = Modifier.weight(1f)
        )
        HolographicButton(
            text = "HINT",
            onClick = onHint,
            glowColor = NeonColors.hologramYellow,
            modifier = Modifier.weight(1f)
        )
        HolographicButton(
            text = "RESET",
            onClick = onReset,
            glowColor = NeonColors.hologramRed,
            modifier = Modifier.weight(1f)
        )
        HolographicButton(
            text = "NEXT",
            onClick = onNext,
            glowColor = NeonColors.hologramGreen,
            modifier = Modifier.weight(1f),
            enabled = canAdvance
        )
    }
}


@Composable
private fun GameModeDisplay(gameMode: GameMode) {
    val color = when (gameMode) {
        GameMode.CLASSIC -> NeonColors.hologramGreen
        GameMode.TIMED -> NeonColors.hologramYellow
        GameMode.COMPETITIVE -> NeonColors.hologramPink
        GameMode.PUZZLE -> NeonColors.hologramCyan
        else -> NeonColors.hologramBlue
    }
    NeonText(text = gameMode.name, fontSize = 16, fontWeight = FontWeight.Bold, neonColor = color)
}

@Composable
private fun PlayerStats(gameState: BallSortGameState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        PlayerStat(label = "Player 1", score = gameState.scores[0], isActive = gameState.currentPlayer == 0)
        PlayerStat(label = "Player 2", score = gameState.scores[1], isActive = gameState.currentPlayer == 1)
    }
}

@Composable
private fun PlayerStat(label: String, score: Int, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = if (isActive) NeonColors.neonYellow else Color.White)
        Text(text = "Score: $score", color = if (isActive) NeonColors.neonYellow else Color.White)
    }
}

@Composable
private fun PowerUps(enabledPowerUps: Set<PowerUp>, onPowerUpClick: (PowerUp) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (powerUp in enabledPowerUps) {
            HolographicButton(
                text = powerUp.name,
                onClick = { onPowerUpClick(powerUp) },
                glowColor = NeonColors.hologramPurple
            )
        }
    }
}

@Composable
private fun VictoryScreen(gameState: BallSortGameState, onNextLevel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { /* consume clicks */ },
        contentAlignment = Alignment.Center
    ) {
        NeonCard(
            neonColor = NeonColors.neonYellow,
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeonText(text = "VICTORY", fontSize = 32, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Level ${gameState.level} Complete!", color = Color.White)
                Text(text = "Moves: ${gameState.moves}", color = Color.White)
                if (gameState.gameMode == GameMode.TIMED || gameState.gameMode == GameMode.COMPETITIVE) {
                    Text(text = "Time: ${gameState.timer}", color = Color.White)
                }
                if (gameState.gameMode == GameMode.COMPETITIVE) {
                    Text(text = "Player 1: ${gameState.scores[0]}", color = Color.White)
                    Text(text = "Player 2: ${gameState.scores[1]}", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                HolographicButton(text = "NEXT LEVEL", onClick = onNextLevel)
            }
        }
    }
}

@Composable
private fun PauseOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        NeonCard(neonColor = NeonColors.hologramPink) {
            Text(
                text = "PAUSE ONLINE",
                color = Color.White,
                fontSize = 22.sp,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Composable
private fun HolographicPulseFrame(
    modifier: Modifier,
    intensity: Float
) {
    val transition = rememberInfiniteTransition()
    val glowProgress by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    NeonColors.hologramMagenta.copy(alpha = 0.35f * glowProgress * intensity),
                    NeonColors.depthVoid.copy(alpha = 0f)
                ),
                center = center,
                radius = size.maxDimension / 2
            )
        )
        drawLine(
            color = NeonColors.hologramCyan.copy(alpha = 0.45f * glowProgress * intensity),
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = 2f
        )
    }
}
