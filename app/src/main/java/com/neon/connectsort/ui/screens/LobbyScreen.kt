package com.neon.connectsort.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.neon.connectsort.game.common.GameDifficulty
import com.neon.connectsort.navigation.toBallSort
import com.neon.connectsort.navigation.toCharacterChips
import com.neon.connectsort.navigation.toConnectFour
import com.neon.connectsort.navigation.toMultiplier
import com.neon.connectsort.navigation.toSettings
import com.neon.connectsort.navigation.toShop
import com.neon.connectsort.ui.screens.viewmodels.LobbyViewModel
import com.neon.connectsort.ui.theme.HolographicParticleSystem
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.holographicBorder

@Composable
fun LobbyScreen(
    navController: NavController,
    viewModel: LobbyViewModel
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        HolographicParticleSystem()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B0B1C),
                            Color(0x00050518)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "CONNECT-SORT",
                    style = MaterialTheme.typography.displayLarge,
                    color = NeonColors.hologramCyan
                )
                Text(
                    text = "Neon arcade hub",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Coins: ${state.totalCoins}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeonColors.hologramYellow
                    )
                    Text(
                        text = "Difficulty: ${GameDifficulty.fromLevel(state.gameDifficulty).displayName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeonColors.hologramGreen
                    )
                }
            }

            // Game mode grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(gameModes(state.highScoreBallSort, state.highScoreMultiplier)) { mode ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .holographicBorder(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = mode.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = mode.color
                            )
                            Text(
                                text = mode.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = { mode.onClick(navController) },
                                modifier = Modifier.fillMaxWidth().holographicBorder()
                            ) {
                                Text(text = "PLAY", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // Footer actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.toSettings() },
                    modifier = Modifier.weight(1f).holographicBorder()
                ) {
                    Text(text = "SETTINGS", style = MaterialTheme.typography.labelMedium)
                }
                Button(
                    onClick = { navController.toCharacterChips() },
                    modifier = Modifier.weight(1f).holographicBorder()
                ) {
                    Text(text = "CHIPS", style = MaterialTheme.typography.labelMedium)
                }
                Button(
                    onClick = { navController.toShop() },
                    modifier = Modifier.weight(1f).holographicBorder()
                ) {
                    Text(text = "SHOP", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

private data class LobbyMode(
    val name: String,
    val description: String,
    val color: Color,
    val onClick: (NavController) -> Unit
)

private fun gameModes(highScoreBallSort: Int, highScoreMultiplier: Int): List<LobbyMode> = listOf(
    LobbyMode(
        name = "Connect 4",
        description = "Classic drop duel vs AI",
        color = NeonColors.hologramCyan,
        onClick = { it.toConnectFour() }
    ),
    LobbyMode(
        name = "Ball Sort",
        description = "Best: ${if (highScoreBallSort == 0) "N/A" else "$highScoreBallSort moves"}",
        color = NeonColors.hologramGreen,
        onClick = { it.toBallSort() }
    ),
    LobbyMode(
        name = "Multiplier",
        description = "Best: $highScoreMultiplier",
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
