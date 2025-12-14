package com.neon.connectsort.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neon.connectsort.R
import com.neon.connectsort.navigation.activeStoryChapterId
import com.neon.connectsort.navigation.consumeConnectFourLocalMatchRequest
import com.neon.connectsort.navigation.publishStoryResult
import com.neon.connectsort.ui.components.HolographicButton
import com.neon.connectsort.ui.components.NeonParticleField
import com.neon.connectsort.ui.components.NeonPulseRing
import com.neon.connectsort.ui.components.SlimeDripOverlay
import com.neon.connectsort.ui.screens.viewmodels.ConnectFourViewModel
import com.neon.connectsort.ui.screens.viewmodels.ConnectFourGameState
import com.neon.connectsort.ui.theme.NeonButton
import com.neon.connectsort.ui.theme.NeonCard
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.NeonText
import com.neon.game.common.GameDifficulty

@Composable
fun ConnectFourScreen(
    navController: NavController,
    viewModel: ConnectFourViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val storyChapterId = navController.activeStoryChapterId()
    var storyResultSent by remember { mutableStateOf(false) }
    val dropAnimation = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        if (navController.consumeConnectFourLocalMatchRequest()) {
            viewModel.setLocalMultiplayer(true)
        }
    }

    LaunchedEffect(gameState.isGameOver, storyChapterId, storyResultSent) {
        if (storyChapterId != null && gameState.isGameOver && !storyResultSent) {
            val success = gameState.winner == 1
            navController.publishStoryResult(success)
            storyResultSent = true
        }
        if (!gameState.isGameOver && storyResultSent) {
            storyResultSent = false
        }
    }

    LaunchedEffect(gameState.lastDrop) {
        if (gameState.lastDrop != null) {
            dropAnimation.snapTo(0f)
            dropAnimation.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 420, easing = LinearOutSlowInEasing)
            )
        }
    }

    val auraColor = when {
        gameState.winner != 0 -> NeonColors.neonYellow
        gameState.currentPlayer == 1 -> NeonColors.neonCyan
        else -> NeonColors.hologramMagenta
    }

    val slimeIntensity = when {
        gameState.winner != 0 -> 1.7f
        gameState.isGameOver -> 1.3f
        else -> 1f
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ConnectFourHeader(
                difficulty = gameState.difficulty,
                onBack = { navController.popBackStack() },
                onReset = { viewModel.resetGame() }
            )
            ConnectFourModeSelector(
                isLocalMultiplayer = gameState.isLocalMultiplayer,
                onSelectAi = { viewModel.setLocalMultiplayer(false) },
                onSelectLocal = { viewModel.setLocalMultiplayer(true) }
            )

            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(7f / 6f),
                neonColor = auraColor
            ) {
                ConnectFourArena(
                    gameState = gameState,
                    dropProgress = dropAnimation.value,
                    slimeIntensity = slimeIntensity,
                    onColumnTap = { if (gameState.winner == 0) viewModel.dropChip(it) }
                )
            }

            ConnectFourStatusBar(state = gameState)
        }
    }
}

@Composable
private fun ConnectFourHeader(
    difficulty: GameDifficulty,
    onBack: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        NeonButton(
            text = "← LOBBY",
            onClick = onBack,
            neonColor = NeonColors.hologramBlue,
            modifier = Modifier.width(110.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NeonText(
                text = "NEON CONNECT 4",
                fontSize = 26,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.hologramCyan
            )
            Text(
                text = "Difficulty · ${difficulty.displayName}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        NeonButton(
            text = "RESET",
            onClick = onReset,
            neonColor = NeonColors.hologramRed,
            modifier = Modifier.width(110.dp)
        )
    }
}

@Composable
private fun ConnectFourModeSelector(
    isLocalMultiplayer: Boolean,
    onSelectAi: () -> Unit,
    onSelectLocal: () -> Unit
) {
    val aiColor = if (isLocalMultiplayer) NeonColors.hologramBlue else NeonColors.hologramCyan
    val localColor = if (isLocalMultiplayer) NeonColors.hologramPink else NeonColors.hologramBlue

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "MATCH TYPE",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HolographicButton(
                text = "VS AI",
                onClick = onSelectAi,
                glowColor = aiColor,
                modifier = Modifier.weight(1f)
            )
            HolographicButton(
                text = "LOCAL DUEL",
                onClick = onSelectLocal,
                glowColor = localColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
private fun ConnectFourArena(
    gameState: ConnectFourGameState,
    dropProgress: Float,
    slimeIntensity: Float,
    onColumnTap: (Int) -> Unit
) {
    val rows = gameState.board.size
    val cols = gameState.board.first().size
    val slimeMotion = rememberInfiniteTransition()
    val slimeWave by slimeMotion.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val slimeAlpha = (0.45f + (slimeIntensity - 1f) * 0.25f).coerceIn(0.35f, 0.95f)
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.connect4_board),
            contentDescription = "Connect Four arcade cabinet",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(R.drawable.connect4_slime),
            contentDescription = "Dripping slime cover",
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationY = slimeWave
                    alpha = slimeAlpha
                },
            contentScale = ContentScale.Crop
        )

        SlimeDripOverlay(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(32.dp),
            intensity = slimeIntensity
        )

        NeonParticleField(
            modifier = Modifier
                .matchParentSize()
                .padding(16.dp),
            intensity = if (gameState.winner != 0) 1.2f else 0.6f
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 30.dp)
        ) {
            val cellWidth = size.width / cols
            val cellHeight = size.height / rows

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(NeonColors.depthMidnight, NeonColors.depthDark),
                    startY = 0f,
                    endY = size.height
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(32f, 32f),
//                style = Stroke(width = 4f, color = NeonColors.hologramPurple.copy(alpha = 0.5f))
            )

            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val cx = col * cellWidth + cellWidth / 2
                    val cy = row * cellHeight + cellHeight / 2
                    drawCircle(
                        color = NeonColors.depthVoid.copy(alpha = 0.6f),
                        radius = cellWidth.coerceAtMost(cellHeight) * 0.42f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = NeonColors.hologramBlue.copy(alpha = 0.25f),
                        radius = cellWidth.coerceAtMost(cellHeight) * 0.35f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2f)
                    )
                }
            }

            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val chip = gameState.board[row][col]
                    chip?.let { player ->
                        val cx = col * cellWidth + cellWidth / 2
                        val cy = row * cellHeight + cellHeight / 2
                        val color = if (player == 1) NeonColors.neonCyan else NeonColors.neonMagenta
                        drawCircle(
                            color = color,
                            radius = cellWidth.coerceAtMost(cellHeight) * 0.36f,
                            center = Offset(cx, cy)
                        )
                    }
                }
            }

            gameState.winningLine.forEach { (row, col) ->
                val cx = col * cellWidth + cellWidth / 2
                val cy = row * cellHeight + cellHeight / 2
                drawCircle(
                    color = NeonColors.hologramYellow.copy(alpha = 0.6f),
                    radius = cellWidth.coerceAtMost(cellHeight) * 0.4f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 6f)
                )
            }

            gameState.lastDrop?.let { drop ->
                if (dropProgress < 1f) {
                    val targetY = drop.row * cellHeight + cellHeight / 2
                    val startY = -cellHeight
                    val cy = startY + (targetY - startY) * dropProgress
                    val cx = drop.column * cellWidth + cellWidth / 2
                    val color = if (drop.player == 1) NeonColors.neonCyan else NeonColors.neonMagenta
                    drawCircle(
                        color = color,
                        radius = cellWidth.coerceAtMost(cellHeight) * 0.35f,
                        center = Offset(cx, cy),
                        style = Fill
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(cols) { index ->
                ColumnDropIndicator(
                    index = index,
                    onTap = { onColumnTap(index) },
                    enabled = gameState.board[0][index] == null,
                    color = if (gameState.currentPlayer == 1) NeonColors.hologramCyan else NeonColors.hologramMagenta
                )
            }
        }

        NeonPulseRing(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center),
            color = if (gameState.isGameOver) NeonColors.neonYellow else NeonColors.hologramCyan
        )
    }
}

@Composable
private fun RowScope.ColumnDropIndicator(
    index: Int,
    onTap: () -> Unit,
    enabled: Boolean,
    color: Color
) {
    val alpha by animateFloatAsState(targetValue = if (enabled) 1f else 0.35f)
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .weight(1f)
            .height(44.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f * alpha))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.8f * alpha),
                shape = CircleShape
            )
            .clickable(enabled = enabled, onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${index + 1}",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ConnectFourStatusBar(state: ConnectFourGameState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val opponentLabel = if (state.isLocalMultiplayer) "PLAYER 2" else "AI"
        val statusText = when {
            state.winner == 1 -> "PLAYER 1 VICTORY!"
            state.winner == 2 -> if (state.isLocalMultiplayer) "PLAYER 2 VICTORY!" else "AI OUTSMARTED YOU"
            state.isDraw -> "DRAWN BATTLE"
            else -> "Current turn · ${if (state.currentPlayer == 1) "PLAYER 1" else opponentLabel}"
        }
        Text(
            text = statusText,
            color = NeonColors.textPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScorePill("PLAYER 1", state.playerScore, NeonColors.neonCyan)
            ScorePill(opponentLabel, state.aiScore, NeonColors.hologramYellow)
            ScorePill("BEST", state.bestScore, NeonColors.hologramYellow)
        }
    }
}

@Composable
private fun ScorePill(label: String, value: Int, color: Color) {
    NeonCard(
        modifier = Modifier
            .widthIn(min = 100.dp)
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
                text = value.toString(),
                fontSize = 22,
                fontWeight = FontWeight.Bold,
                neonColor = color
            )
        }
    }
}
