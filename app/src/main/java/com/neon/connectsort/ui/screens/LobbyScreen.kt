package com.neon.connectsort.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neon.connectsort.navigation.toBallSort
import com.neon.connectsort.navigation.toCharacterChips
import com.neon.connectsort.navigation.toConnectFour
import com.neon.connectsort.navigation.toMultiplier
import com.neon.connectsort.navigation.toSettings
import com.neon.connectsort.navigation.toShop
import com.neon.connectsort.ui.screens.viewmodels.LobbyViewModel
import com.neon.connectsort.ui.theme.HolographicButton
import com.neon.connectsort.ui.theme.HolographicCard
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.NeonGameTheme
import com.neon.connectsort.ui.theme.NeonText

@Composable
fun LobbyScreen(
    navController: NavController,
    viewModel: LobbyViewModel
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        NeonGridBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                NeonText(
                    text = "CONNECT-SORT",
                    fontSize = 28,
                    fontWeight = FontWeight.Bold,
                    neonColor = NeonColors.hologramCyan
                )
                Text(
                    text = "Neon arcade hub",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
                NeonText(
                    text = "Coins: ${state.totalCoins}",
                    fontSize = 16,
                    fontWeight = FontWeight.SemiBold,
                    neonColor = NeonColors.hologramYellow
                )
            }

            // Game mode grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(gameModes()) { mode ->
                    HolographicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        title = mode.name
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mode.description,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                            HolographicButton(
                                text = "PLAY",
                                onClick = { mode.onClick(navController) },
                                glowColor = mode.color,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Footer actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HolographicButton(
                    text = "SETTINGS",
                    onClick = { navController.toSettings() },
                    glowColor = NeonColors.hologramBlue,
                    modifier = Modifier.weight(1f)
                )
                HolographicButton(
                    text = "CHIPS",
                    onClick = { navController.toCharacterChips() },
                    glowColor = NeonColors.hologramPink,
                    modifier = Modifier.weight(1f)
                )
                HolographicButton(
                    text = "SHOP",
                    onClick = { navController.toShop() },
                    glowColor = NeonColors.hologramGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NeonGridBackground() {
    val infiniteTransition = rememberInfiniteTransition()
    val linePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 100.dp.toPx()
        val lineCount = (size.width / gridSize).toInt() + 2
        val offset = (linePosition * gridSize * 2) % (gridSize * 2)

        drawRect(color = Color(0xFF050518))

        for (i in -1..lineCount) {
            val x = i * gridSize - offset
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, NeonColors.hologramBlue.copy(alpha = 0.3f), Color.Transparent)
                ),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 2f
            )
        }
    }
}

private data class LobbyMode(
    val name: String,
    val description: String,
    val color: Color,
    val onClick: (NavController) -> Unit
)

private fun gameModes(): List<LobbyMode> = listOf(
    LobbyMode(
        name = "Connect 4",
        description = "Classic drop duel vs AI",
        color = NeonColors.hologramCyan,
        onClick = { it.toConnectFour() }
    ),
    LobbyMode(
        name = "Ball Sort",
        description = "Sort the neon vials",
        color = NeonColors.hologramGreen,
        onClick = { it.toBallSort() }
    ),
    LobbyMode(
        name = "Multiplier",
        description = "Risk/reward streaks",
        color = NeonColors.hologramYellow,
        onClick = { it.toMultiplier() }
    ),
    LobbyMode(
        name = "Shop",
        description = "Buy neon upgrades",
        color = NeonColors.hologramPink,
        onClick = { it.toShop() }
    )
)
