package com.neon.connectsort.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import com.neon.connectsort.ui.screens.viewmodels.ConnectFourViewModel
import com.neon.connectsort.ui.theme.HolographicColors
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.NeonGameTheme
import com.neon.connectsort.ui.theme.NeonButton
import com.neon.connectsort.ui.theme.NeonCard
import com.neon.connectsort.ui.theme.NeonText

@Composable
fun ConnectFourScreen(
    navController: NavController,
    viewModel: ConnectFourViewModel
) {
    val gameState by viewModel.gameState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B1C))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeonButton(
                text = "← LOBBY",
                onClick = { navController.popBackStack() },
                neonColor = NeonColors.neonBlue,
                modifier = Modifier.width(110.dp)
            )
            NeonText(
                text = "CONNECT 4",
                fontSize = 24,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.neonCyan
            )
            NeonButton(
                text = "RESET",
                onClick = { viewModel.resetGame() },
                neonColor = NeonColors.neonRed,
                modifier = Modifier.width(110.dp)
            )
        }

        // Scores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScorePill("PLAYER", gameState.playerScore, HolographicColors.playerOne)
            ScorePill("AI", gameState.aiScore, HolographicColors.playerTwo)
        }

        // Board
        NeonCard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(7f / 6f)
        ) {
            val rows = gameState.board.size
            val cols = gameState.board.first().size
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val cellW = size.width / cols
                val cellH = size.height / rows
                // grid holes
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val cx = c * cellW + cellW / 2
                        val cy = r * cellH + cellH / 2
                        drawCircle(
                            color = NeonColors.neonBlue.copy(alpha = 0.25f),
                            radius = cellW.coerceAtMost(cellH) * 0.4f,
                            center = Offset(cx, cy),
                            style = Stroke(width = 3f)
                        )
                    }
                }
                // chips
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val value = gameState.board[r][c]
                        value?.let { player ->
                            val cx = c * cellW + cellW / 2
                            val cy = r * cellH + cellH / 2
                            val color = if (player == 1) HolographicColors.playerOne else HolographicColors.playerTwo
                            drawCircle(
                                color = color,
                                radius = cellW.coerceAtMost(cellH) * 0.35f,
                                center = Offset(cx, cy)
                            )
                        }
                    }
                }
                // winning line highlight
                gameState.winningLine.forEach { (row, col) ->
                    val cx = col * cellW + cellW / 2
                    val cy = row * cellH + cellH / 2
                    drawCircle(
                        color = NeonColors.neonYellow.copy(alpha = 0.5f),
                        radius = cellW.coerceAtMost(cellH) * 0.42f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 6f)
                    )
                }
            }
            // touch overlay columns
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                repeat(gameState.board.first().size) { col ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(enabled = gameState.winner == 0) {
                                viewModel.dropChip(col)
                            }
                    )
                }
            }
        }

        // Status + actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when {
                    gameState.winner == 1 -> "You win! Tap reset to play again."
                    gameState.winner == 2 -> "AI wins! Tap reset to retry."
                    else -> "Current player: ${if (gameState.currentPlayer == 1) "You" else "AI"}"
                },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ScorePill(label: String, value: Int, color: Color) {
    NeonCard(
        modifier = Modifier.widthIn(min = 140.dp),
        neonColor = color
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
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
