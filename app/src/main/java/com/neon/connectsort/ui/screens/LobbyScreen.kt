package com.neon.connectsort.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neon.connectsort.R
import com.neon.connectsort.navigation.toBallSort
import com.neon.connectsort.navigation.toCharacterChips
import com.neon.connectsort.navigation.toConnectFour
import com.neon.connectsort.navigation.toMultiplier
import com.neon.connectsort.navigation.toSettings
import com.neon.connectsort.navigation.toShop
import com.neon.connectsort.navigation.toStoryHub
import com.neon.connectsort.ui.components.HolographicButton
import com.neon.connectsort.ui.components.NeonParticleField
import com.neon.connectsort.ui.components.SlimeDripOverlay
import com.neon.connectsort.ui.theme.NeonCard
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.connectsort.ui.theme.NeonText
import com.neon.connectsort.ui.screens.viewmodels.LobbyState
import com.neon.connectsort.ui.screens.viewmodels.LobbyViewModel
import com.neon.connectsort.ui.components.ConnectFourArcadeBackground
import com.neon.connectsort.ui.components.BallSortArcadeBackground

@Composable
fun LobbyScreen(
    navController: NavController,
    viewModel: LobbyViewModel
) {
    val state by viewModel.state.collectAsState()
    var selectedMode by remember { mutableStateOf(gameModes().first()) }
    var showLeaderboard by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // CyberpunkBackdrop()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LobbyHeader(state = state)
            Spacer(modifier = Modifier.height(12.dp))
            GameModePreview(selectedMode = selectedMode)
            Spacer(modifier = Modifier.height(12.dp))
            ModeSelectorRow(
                selectedMode = selectedMode,
                onModeSelected = { selectedMode = it },
                onPlay = { selectedMode.onClick(navController) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MultiplayerPanel(
                matches = multiplayerMatches,
                onQuickMatch = {},
                onCreateRoom = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            FooterActions(
                navController = navController,
                onLeaderboard = { showLeaderboard = true }
            )
        }

        if (showLeaderboard) {
            LeaderboardOverlay(onClose = { showLeaderboard = false })
        }
    }
}

@Composable
private fun LobbyHeader(state: LobbyState) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        NeonText(
            text = "NEON CONNECT-SORT",
            fontSize = 28,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            neonColor = NeonColors.hologramCyan
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Arcade hub · Cybernetic lounge",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatsBadge("Coins", state.totalCoins.toString(), NeonColors.hologramYellow)
            StatsBadge("Connect 4", displayScore(state.highScoreConnectFour), NeonColors.hologramCyan)
            StatsBadge("Ball Sort", displayScore(state.highScoreBallSort), NeonColors.hologramGreen)
            StatsBadge("Difficulty", state.gameDifficulty.displayName, NeonColors.hologramPink)
        }
    }
}

@Composable
private fun ModeSelectorRow(
    selectedMode: LobbyMode,
    onModeSelected: (LobbyMode) -> Unit,
    onPlay: () -> Unit
) {
    val modes = gameModes()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        modes.forEach { mode ->
            ArcadeModeCard(
                mode = mode,
                isSelected = mode.name == selectedMode.name,
                onSelect = { onModeSelected(mode) },
                onPlay = onPlay
            )
        }
    }
}

@Composable
private fun GameModePreview(selectedMode: LobbyMode) {
    NeonCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        neonColor = selectedMode.color
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LobbyPreviewHologram(
                modifier = Modifier
                    .matchParentSize()
                    .padding(12.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MODES · ${selectedMode.name}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                NeonText(
                    text = selectedMode.description,
                    fontSize = 20,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    neonColor = NeonColors.textPrimary
                )
                NeonText(
                    text = "Tap PLAY to drop into the neon arena.",
                    fontSize = 14,
                    neonColor = NeonColors.hologramPurple
                )
            }
        }
    }
}

@Composable
private fun RowScope.ArcadeModeCard(
    mode: LobbyMode,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPlay: () -> Unit
) {
    NeonCard(
        modifier = Modifier
            .weight(1f)
            .height(200.dp)
            .clickable { onSelect() },
        neonColor = if (isSelected) mode.color else NeonColors.depthMidnight
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(mode.color, RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = mode.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
            Text(
                text = mode.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            HolographicButton(
                text = "PLAY",
                onClick = onPlay,
                glowColor = mode.color,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MultiplayerPanel(
    matches: List<MultiplayerMatch>,
    onQuickMatch: () -> Unit,
    onCreateRoom: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NeonColors.hologramPurple, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NeonText(
                text = "MULTIPLAYER LOBBY",
                fontSize = 18,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                neonColor = NeonColors.hologramCyan
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HolographicButton(
                    text = "QUICK MATCH",
                    onClick = onQuickMatch,
                    glowColor = NeonColors.hologramGreen
                )
                HolographicButton(
                    text = "CREATE ROOM",
                    onClick = onCreateRoom,
                    glowColor = NeonColors.hologramPink
                )
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(matches) { match ->
                MultiplayerMatchCard(match)
            }
        }
    }
}

@Composable
private fun MultiplayerMatchCard(match: MultiplayerMatch) {
    NeonCard(
        modifier = Modifier
            .width(200.dp)
            .height(140.dp),
        neonColor = match.color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            NeonText(
                text = match.name,
                fontSize = 16,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                neonColor = NeonColors.textPrimary
            )
            Text(
                text = "Players: ${match.players} · ${match.mode}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            HolographicButton(
                text = "JOIN",
                onClick = {},
                glowColor = NeonColors.hologramYellow,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FooterActions(navController: NavController, onLeaderboard: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
        HolographicButton(
            text = "LEADERBOARD",
            onClick = onLeaderboard,
            glowColor = NeonColors.hologramYellow,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LeaderboardOverlay(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        NeonCard(modifier = Modifier.width(320.dp), neonColor = NeonColors.hologramPurple) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NeonText(
                    text = "LEADERBOARD",
                    fontSize = 24,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    neonColor = NeonColors.neonYellow
                )
                LeaderboardRow("NEXUS", "CONNECT 4", "237K")
                LeaderboardRow("SPECTRE", "BALL SORT", "182K")
                LeaderboardRow("VALKYRIE", "MULTIPLIER", "160K")
                HolographicButton(
                    text = "CLOSE",
                    onClick = onClose,
                    glowColor = NeonColors.hologramPink,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun LeaderboardRow(name: String, mode: String, score: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = name, color = Color.White, fontSize = 16.sp)
            Text(text = mode, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Text(text = score, color = NeonColors.hologramYellow, fontSize = 18.sp)
    }
}

@Composable
private fun CyberpunkBackdrop() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val gridSize = 90.dp.toPx()
            val lines = (size.width / gridSize).toInt()
            drawRect(color = NeonColors.depthVoid)
            for (i in -1..lines) {
                val x = i * gridSize
                drawLine(
                    color = NeonColors.hologramBlue.copy(alpha = 0.2f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                drawLine(
                    color = NeonColors.hologramBlue.copy(alpha = 0.15f),
                    start = Offset(0f, x),
                    end = Offset(size.width, x),
                    strokeWidth = 1f
                )
            }
        }
        SlimeDripOverlay(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.TopCenter)
        )
        // FloatingGlyphs()
        // NeonParticleField(modifier = Modifier.matchParentSize(), intensity = 0.35f)
    }
}

@Composable
private fun FloatingGlyphs() {
    val transition = rememberInfiniteTransition()
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing))
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val glyphs = listOf(0.2f, 0.5f, 0.8f)
        glyphs.forEachIndexed { index, position ->
            val y = (position + offset * 0.2f * index.toFloat()) % 1f
            val x = (position + offset * 0.3f * (index.toFloat() + 1f)) % 1f
            drawCircle(
                color = NeonColors.hologramPink.copy(alpha = 0.4f),
                radius = 18f,
                center = Offset(x * size.width, y * size.height),
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
private fun LobbyPreviewHologram(modifier: Modifier) {
    val shimmer = rememberInfiniteTransition()
    val shimmerProgress by shimmer.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        Box(modifier = Modifier.graphicsLayer {
            alpha = 0.25f + shimmerProgress * 0.15f
            translationX = shimmerProgress * 6f
        }) {
            ConnectFourArcadeBackground()
        }
        Box(modifier = Modifier.graphicsLayer {
            alpha = 0.15f + (1f - shimmerProgress) * 0.2f
            translationX = -shimmerProgress * 8f
        }) {
            BallSortArcadeBackground()
        }
        Canvas(modifier = Modifier.matchParentSize()) {
            val lineAlpha = 0.35f * shimmerProgress
            drawLine(
                color = NeonColors.hologramPink.copy(alpha = lineAlpha),
                start = Offset(0f, size.height * 0.35f),
                end = Offset(size.width, size.height * 0.35f),
                strokeWidth = 2f
            )
            drawLine(
                color = NeonColors.hologramCyan.copy(alpha = lineAlpha * 0.6f),
                start = Offset(0f, size.height * 0.55f),
                end = Offset(size.width, size.height * 0.55f),
                strokeWidth = 1.5f
            )
        }
    }
}

private fun displayScore(score: Int): String = if (score > 0) score.toString() else "—"

private fun gameModes(): List<LobbyMode> = listOf(
    LobbyMode("Connect 4", "Neon drop duel", NeonColors.hologramCyan) { it.toConnectFour() },
    LobbyMode("Ball Sort", "Vial shuffle puzzle", NeonColors.hologramGreen) { it.toBallSort() },
    LobbyMode("Multiplier", "Risk streaks", NeonColors.hologramYellow) { it.toMultiplier() },
    LobbyMode("Shop", "Neon mods", NeonColors.hologramPink) { it.toShop() }
)

private val multiplayerMatches = listOf(
    MultiplayerMatch("Nexus Prime", 2, "PRIVATE", NeonColors.hologramBlue),
    MultiplayerMatch("Spectral Rift", 4, "RANKED", NeonColors.hologramPurple),
    MultiplayerMatch("Valkyrie Run", 3, "QUICK", NeonColors.hologramRed)
)

data class LobbyMode(
    val name: String,
    val description: String,
    val color: Color,
    val onClick: (NavController) -> Unit
)

data class MultiplayerMatch(
    val name: String,
    val players: Int,
    val mode: String,
    val color: Color
)

@Composable
private fun StatsBadge(label: String, value: String, color: Color) {
    NeonCard(
        modifier = Modifier
            .width(90.dp)
            .height(70.dp),
        neonColor = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            NeonText(text = value, fontSize = 18, neonColor = color)
        }
    }
}
