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
import com.neon.connectsort.navigation.activeStoryChapterId
import com.neon.connectsort.navigation.publishStoryResult
import com.neon.connectsort.navigation.toLobby
import com.neon.game.common.GameResult
import com.neon.game.multiplier.MultiplierGame
import com.neon.connectsort.ui.screens.viewmodels.MultiplierGameState
import com.neon.connectsort.ui.screens.viewmodels.MultiplierViewModel

@Composable
fun MultiplierScreen(
    navController: NavController,
    viewModel: MultiplierViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val storyChapterId = navController.activeStoryChapterId()
    var storyResultSent by remember { mutableStateOf(false) }

    LaunchedEffect(storyChapterId, gameState.gameResult, storyResultSent) {
        if (storyChapterId != null && gameState.gameResult == GameResult.WIN && !storyResultSent) {
            navController.publishStoryResult(true)
            storyResultSent = true
        }
        if (storyResultSent && gameState.gameResult != GameResult.WIN) {
            storyResultSent = false
        }
    }
    
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
            
            // Game board
            MultiplierBoard(
                state = gameState,
                onColumnClick = { col -> 
                    if (!gameState.isGameOver) {
                        viewModel.onUserAction(MultiplierGame.Action.Drop(col))
                    }
                }
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
            onPlayAgain = { viewModel.onRestart() },
            onBackToLobby = { navController.toLobby() }
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
            neonColor = NeonColors.hologramBlue,
            modifier = Modifier.width(100.dp)
        )
        
        NeonText(
            text = "MULTIPLIER MODE",
            fontSize = 24,
            fontWeight = FontWeight.Bold,
            neonColor = NeonColors.hologramYellow
        )
        
        NeonButton(
            text = "HELP",
            onClick = { /* Show help dialog */ },
            neonColor = NeonColors.hologramPink,
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
                neonColor = NeonColors.hologramCyan
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
                    .background(NeonColors.hologramYellow.copy(alpha = 0.2f))
                    .border(
                        width = 2.dp,
                        color = NeonColors.hologramYellow,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                NeonText(
                    text = "×${gameState.currentMultiplier}",
                    fontSize = 32,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.hologramYellow
                )
            }
        }
        
    }
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
            val color = if (cell != null) NeonColors.hologramCyan else Color.Transparent
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonColors.surfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = NeonColors.hologramBlue.copy(alpha = 0.4f),
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
                                            NeonColors.hologramYellow.copy(alpha = 0.7f),
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
            text = "CASH OUT",
            onClick = { viewModel.onUserAction(MultiplierGame.Action.CashOut) },
            neonColor = NeonColors.hologramYellow,
            modifier = Modifier.weight(1f),
            enabled = !gameState.isGameOver && gameState.score > 0
        )
        
        NeonButton(
            text = "NEW GAME",
            onClick = { viewModel.onRestart() },
            neonColor = NeonColors.hologramRed,
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
                color = NeonColors.hologramCyan
            )
            
            StreakItem(
                label = "Max Streak",
                value = gameState.maxStreak.toString(),
                color = NeonColors.hologramPink
            )
            
            StreakItem(
                label = "Lives",
                value = gameState.lives.toString(),
                color = NeonColors.hologramYellow
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
            neonColor = NeonColors.hologramYellow
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
                    neonColor = NeonColors.hologramYellow
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ScoreDisplayItem(
                        label = "FINAL SCORE",
                        value = finalScore.toString(),
                        color = NeonColors.hologramCyan
                    )
                }
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeonButton(
                        text = "PLAY AGAIN",
                        onClick = onPlayAgain,
                        neonColor = NeonColors.hologramGreen,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    NeonButton(
                        text = "BACK TO LOBBY",
                        onClick = onBackToLobby,
                        neonColor = NeonColors.hologramBlue,
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
