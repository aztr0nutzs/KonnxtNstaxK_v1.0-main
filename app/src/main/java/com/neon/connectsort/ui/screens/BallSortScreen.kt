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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.Text
import com.neon.connectsort.ui.theme.*
import com.neon.connectsort.ui.screens.viewmodels.BallAnimationState
import com.neon.connectsort.ui.screens.viewmodels.BallSortGameState
import com.neon.connectsort.ui.screens.viewmodels.BallSortViewModel
import kotlinx.coroutines.delay

@Composable
fun BallSortScreen(
    navController: NavController,
    viewModel: BallSortViewModel,
    initialLevel: Int = 1
) {
    val gameState by viewModel.gameState.collectAsState()
    val selectedTube by viewModel.selectedTube.collectAsState()
    val animationState by viewModel.animationState.collectAsState()
    
    LaunchedEffect(initialLevel) {
        viewModel.loadLevel(initialLevel)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            BallSortHeader(navController, viewModel, gameState)
            
            // Game tubes
            TubeGrid(
                tubes = gameState.tubes,
                selectedTube = selectedTube,
                animationState = animationState,
                onTubeClick = { index -> viewModel.selectTube(index) }
            )
            
            // Game info
            BallSortInfo(gameState)
            
            // Controls
            BallSortControls(viewModel, navController)
        }
        
        // Level complete overlay
        if (gameState.isLevelComplete) {
            LevelCompleteOverlay(
                level = gameState.level,
                moves = gameState.moves,
                onNextLevel = { viewModel.loadLevel(gameState.level + 1) },
                onBackToLobby = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun BallSortHeader(
    navController: NavController,
    viewModel: BallSortViewModel,
    gameState: BallSortGameState
) {
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
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NeonText(
                text = "BALL SORT",
                fontSize = 24,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.neonGreen
            )
            
            NeonText(
                text = "LEVEL ${gameState.level}",
                fontSize = 16,
                fontWeight = FontWeight.SemiBold,
                neonColor = NeonColors.neonYellow
            )
        }
        
        NeonButton(
            text = "RESET",
            onClick = { viewModel.resetLevel() },
            neonColor = NeonColors.neonRed,
            modifier = Modifier.width(100.dp)
        )
    }
}

@Composable
fun TubeGrid(
    tubes: List<List<Color>>,
    selectedTube: Int?,
    animationState: BallAnimationState?,
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    if (index < tubes.size) {
                        TubeView(
                            balls = tubes[index],
                            isSelected = selectedTube == index,
                            isAnimating = animationState?.fromTube == index || 
                                        animationState?.toTube == index,
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
fun TubeView(
    balls: List<Color>,
    isSelected: Boolean,
    isAnimating: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tubeColor = if (isSelected) NeonColors.neonCyan else NeonColors.neonBlue
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.05f else 1f,
        label = "tubeScale"
    )
    
    Box(
        modifier = modifier
            .aspectRatio(0.6f)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = tubeColor.copy(alpha = if (isSelected) 0.8f else 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tube neck
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(tubeColor.copy(alpha = 0.5f))
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Balls
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                balls.forEach { ballColor ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ballColor)
                            .border(
                                width = 1.dp,
                                color = ballColor.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }
                
                // Empty slots
                repeat(4 - balls.size) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Transparent)
                    )
                }
            }
        }
    }
}

@Composable
fun BallSortInfo(gameState: BallSortGameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        InfoItem(
            label = "MOVES",
            value = gameState.moves.toString(),
            color = NeonColors.neonCyan
        )
        
        InfoItem(
            label = "BEST",
            value = gameState.bestMoves?.toString() ?: "--",
            color = NeonColors.neonGreen
        )
        
        InfoItem(
            label = "TUBES",
            value = gameState.tubes.size.toString(),
            color = NeonColors.neonYellow
        )
    }
}

@Composable
fun InfoItem(
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
            fontSize = 20,
            fontWeight = FontWeight.Bold,
            neonColor = color
        )
    }
}

@Composable
fun BallSortControls(
    viewModel: BallSortViewModel,
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonButton(
            text = "UNDO",
            onClick = { viewModel.undoMove() },
            neonColor = NeonColors.neonBlue,
            modifier = Modifier.weight(1f)
        )
        
        NeonButton(
            text = "RESET LEVEL",
            onClick = { viewModel.resetLevel() },
            neonColor = NeonColors.neonRed,
            modifier = Modifier.weight(1f)
        )
        
        NeonButton(
            text = "NEXT LEVEL",
            onClick = { viewModel.loadLevel(viewModel.gameState.value.level + 1) },
            neonColor = NeonColors.neonGreen,
            modifier = Modifier.weight(1f),
            enabled = viewModel.gameState.value.level < 10 // Example max level
        )
    }
}

@Composable
fun LevelCompleteOverlay(
    level: Int,
    moves: Int,
    onNextLevel: () -> Unit,
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
            neonColor = NeonColors.neonGreen
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                NeonText(
                    text = "LEVEL COMPLETE!",
                    fontSize = 32,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.neonGreen
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Level $level",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 18.sp
                    )
                    
                    NeonText(
                        text = "$moves moves",
                        fontSize = 24,
                        fontWeight = FontWeight.Bold,
                        neonColor = NeonColors.neonYellow
                    )
                }
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NeonButton(
                        text = "NEXT LEVEL",
                        onClick = onNextLevel,
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
