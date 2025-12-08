package com.neon.connectsort.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.neon.connectsort.ui.theme.NeonButton
import com.neon.connectsort.ui.theme.NeonCard
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.NeonGameTheme
import com.neon.connectsort.ui.theme.NeonText

@Composable
fun LobbyScreen(
    navController: NavController,
    viewModel: LobbyViewModel
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B0B1C),
                        Color(0xFF050518)
                    )
                )
            )
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
                NeonCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    neonColor = mode.color
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        NeonText(
                            text = mode.name,
                            fontSize = 18,
                            fontWeight = FontWeight.Bold,
                            neonColor = mode.color
                        )
                        Text(
                            text = mode.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        NeonButton(
                            text = "PLAY",
                            onClick = { mode.onClick(navController) },
                            neonColor = mode.color,
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
            NeonButton(
                text = "SETTINGS",
                onClick = { navController.toSettings() },
                neonColor = NeonColors.hologramBlue,
                modifier = Modifier.weight(1f)
            )
            NeonButton(
                text = "CHIPS",
                onClick = { navController.toCharacterChips() },
                neonColor = NeonColors.hologramPink,
                modifier = Modifier.weight(1f)
            )
            NeonButton(
                text = "SHOP",
                onClick = { navController.toShop() },
                neonColor = NeonColors.hologramGreen,
                modifier = Modifier.weight(1f)
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
