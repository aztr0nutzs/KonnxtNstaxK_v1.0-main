package com.neon.connectsort.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import com.neon.connectsort.ui.theme.*
import com.neon.game.multiplier.MultiplierGame
import com.neon.connectsort.ui.screens.viewmodels.MultiplierGameState
import com.neon.connectsort.ui.screens.viewmodels.MultiplierViewModel

@Composable
fun MultiplierScreen(
    navController: NavController,
    viewModel: MultiplierViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B1C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            MultiplierHeader(navController)
            
            // Score and multiplier display
            ScoreMultiplierDisplay(gameState)
            
            // Multiplier selector
            MultiplierSelector(
                currentMultiplier = gameState.currentMultiplier,
                onMultiplierSelected = { multiplier -> viewModel.setMultiplier(multiplier) }
            )
            
            // Game board
            MultiplierBoard(
                state = gameState,
                onColumnClick = { col -> if (!gameState.isGameOver) viewModel.dropChip(col) }
            )
            
            // Game controls
            MultiplierControls(viewModel, gameState)
            
            // Streak display
            StreakDisplay(gameState)
        }
        
        // Game over overlay
        if (gameState.isGameOver) {
            MultiplierGameOverOverlay(
                finalScore = gameState.score,
                onPlayAgain = { viewModel.resetGame() },
                onBackToLobby = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MultiplierHeader(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonButton(
            text = "← BACK",
            onClick = { navController.popBackStack() },
            neonColor = NeonColors.neonBlue,
            modifier = Modifier.width(100.dp)
        )
        
        NeonText(
            text = "MULTIPLIER MODE",
            fontSize = 24,
            fontWeight = FontWeight.Bold,
            neonColor = NeonColors.neonYellow
        )
        
        NeonButton(
            text = "HELP",
            onClick = { /* Show help dialog */ },
            neonColor = NeonColors.neonMagenta,
            modifier = Modifier.width(100.dp)
        )
    }
}

@Composable
fun ScoreMultiplierDisplay(gameState: MultiplierGameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SCORE",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            
            NeonText(
                text = gameState.score.toString(),
                fontSize = 36,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.neonCyan
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MULTIPLIER",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonColors.neonYellow.copy(alpha = 0.2f))
                    .border(
                        width = 2.dp,
                        color = NeonColors.neonYellow,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                NeonText(
                    text = "×${gameState.currentMultiplier}",
                    fontSize = 32,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.neonYellow
                )
            }
        }
        
    }
}

@Composable
fun MultiplierSelector(
    currentMultiplier: Int,
    onMultiplierSelected: (Int) -> Unit
) {
    val multipliers = listOf(1, 2, 3, 5, 10)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "SELECT MULTIPLIER",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            multipliers.forEach { multiplier ->
                val isSelected = multiplier == currentMultiplier
                val color = when (multiplier) {
                    1 -> NeonColors.neonBlue
                    2 -> NeonColors.neonGreen
                    3 -> NeonColors.neonYellow
                    5 -> NeonColors.neonOrange
                    10 -> NeonColors.neonRed
                    else -> NeonColors.neonCyan
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onMultiplierSelected(multiplier) }
                        .background(
                            if (isSelected) color.copy(alpha = 0.3f) 
                            else Color.Transparent
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = color.copy(alpha = if (isSelected) 0.8f else 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        NeonText(
                            text = "×$multiplier",
                            fontSize = 20,
                            fontWeight = FontWeight.Bold,
                            neonColor = color
                        )
                        
                    Text(
                        text = if (multiplier == 1) "SAFE" else "RISKY",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    }
                }
            }
        }
    }
}

@Composable
fun MultiplierBoard(gameState: MultiplierGameState) {
    MultiplierBoard(state = gameState, onColumnClick = {})
}

@Composable
fun MultiplierBoard(
    state: MultiplierGameState,
    onColumnClick: (Int) -> Unit
) {
    ArcadePanel(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .padding(16.dp),
        title = "RISK BOARD"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(state.board.size) { r ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(state.board[0].size) { c ->
                        val cell = state.board[r][c]
            val color = if (cell != null) NeonColors.neonCyan else Color.Transparent
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonColors.surfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = NeonColors.neonBlue.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onColumnClick(c) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (cell != null) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(color)
                                        .border(
                                            2.dp,
                                            NeonColors.neonYellow.copy(alpha = 0.7f),
                                            RoundedCornerShape(14.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Lives: ${state.lives} • Streak: ${state.currentStreak}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MultiplierControls(
    viewModel: MultiplierViewModel,
    gameState: MultiplierGameState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NeonButton(
            text = "PLACE CHIP",
            onClick = { viewModel.dropChip(0) },
            neonColor = NeonColors.neonGreen,
            modifier = Modifier.weight(1f)
        )
        
        NeonButton(
            text = "CASH OUT",
            onClick = { viewModel.cashOut() },
            neonColor = NeonColors.neonYellow,
            modifier = Modifier.weight(1f),
            enabled = !gameState.isGameOver && gameState.score > 0
        )
        
        NeonButton(
            text = "NEW GAME",
            onClick = { viewModel.resetGame() },
            neonColor = NeonColors.neonRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StreakDisplay(gameState: MultiplierGameState) {
    ArcadePanel(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        title = "STREAK BONUS"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StreakItem(
                label = "Current",
                value = gameState.currentStreak.toString(),
                color = NeonColors.neonCyan
            )
            
            StreakItem(
                label = "Max Streak",
                value = gameState.maxStreak.toString(),
                color = NeonColors.neonMagenta
            )
            
            StreakItem(
                label = "Lives",
                value = gameState.lives.toString(),
                color = NeonColors.neonYellow
            )
        }
    }
}

@Composable
fun StreakItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )

        NeonText(
            text = value,
            fontSize = 18,
            fontWeight = FontWeight.Bold,
            neonColor = color
        )
    }
}

@Composable
fun MultiplierGameOverOverlay(
    finalScore: Int,
    onPlayAgain: () -> Unit,
    onBackToLobby: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        NeonCard(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(24.dp),
            neonColor = NeonColors.neonYellow
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                NeonText(
                    text = "GAME OVER",
                    fontSize = 28,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.neonYellow
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ScoreDisplayItem(
                        label = "FINAL SCORE",
                        value = finalScore.toString(),
                        color = NeonColors.neonCyan
                    )
                }
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeonButton(
                        text = "PLAY AGAIN",
                        onClick = onPlayAgain,
                        neonColor = NeonColors.neonGreen,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    NeonButton(
                        text = "BACK TO LOBBY",
                        onClick = onBackToLobby,
                        neonColor = NeonColors.neonBlue,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreDisplayItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        
        NeonText(
            text = value,
            fontSize = 32,
            fontWeight = FontWeight.Bold,
            neonColor = color
        )
    }
}
