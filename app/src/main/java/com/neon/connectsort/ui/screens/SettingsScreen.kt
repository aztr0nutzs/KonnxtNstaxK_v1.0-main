package com.neon.connectsort.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.neon.connectsort.ui.theme.*
import com.neon.connectsort.ui.components.*
import com.neon.connectsort.ui.screens.viewmodels.SettingsViewModel
import com.neon.game.common.GameMode
import com.neon.game.common.PowerUp

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val settings by viewModel.settings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.neonBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HolographicButton(
                text = "← BACK",
                onClick = { navController.popBackStack() },
                glowColor = NeonColors.hologramBlue,
                modifier = Modifier.width(100.dp)
            )

            NeonText(
                text = "SETTINGS",
                fontSize = 24,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.hologramCyan
            )

            Spacer(modifier = Modifier.width(100.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Settings cards
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SettingsCategory("Audio") }
            item {
                SettingToggle(
                    label = "Sound Effects",
                    isChecked = settings.audio.soundEnabled,
                    onCheckedChange = { viewModel.toggleSound() }
                )
            }
            item {
                SettingToggle(
                    label = "Background Music",
                    isChecked = settings.audio.musicEnabled,
                    onCheckedChange = { viewModel.toggleMusic() }
                )
            }
            item {
                SettingSlider(
                    label = "Volume",
                    value = settings.audio.volume,
                    onValueChange = { viewModel.setVolume(it) }
                )
            }

            item { SettingsCategory("Visual") }
            item {
                SettingToggle(
                    label = "Animations",
                    isChecked = settings.animationsEnabled,
                    onCheckedChange = { viewModel.toggleAnimations() }
                )
            }
            item {
                SettingToggle(
                    label = "Neon Glow Effects",
                    isChecked = settings.glowEffectsEnabled,
                    onCheckedChange = { viewModel.toggleGlowEffects() }
                )
            }

            item { SettingsCategory("Game") }
            item {
                DifficultySelector(
                    difficulty = settings.gameDifficulty.level,
                    onDifficultyChange = { viewModel.setDifficulty(it) }
                )
            }
            item {
                GameModeSelector(
                    gameMode = settings.gameMode,
                    onGameModeChange = { viewModel.setGameMode(it) }
                )
            }
            item {
                PowerUpSelector(
                    enabledPowerUps = settings.enabledPowerUps,
                    onPowerUpToggle = { viewModel.togglePowerUp(it) }
                )
            }
            item {
                SettingToggle(
                    label = "Vibration Feedback",
                    isChecked = settings.vibrationEnabled,
                    onCheckedChange = { viewModel.toggleVibration() }
                )
            }
            item {
                SettingToggle(
                    label = "Show Tutorials",
                    isChecked = settings.showTutorials,
                    onCheckedChange = { viewModel.toggleTutorials() }
                )
            }

            item { SettingsCategory("Data") }
            item {
                HolographicButton(
                    text = "RESET PROGRESS",
                    onClick = { viewModel.resetProgress() },
                    glowColor = NeonColors.hologramRed,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                HolographicButton(
                    text = "CLEAR CACHE",
                    onClick = { viewModel.clearCache() },
                    glowColor = NeonColors.hologramYellow,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = NeonColors.hologramPink,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingToggle(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = label
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    value = isChecked,
                    onValueChange = onCheckedChange,
                    role = Role.Switch
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = NeonColors.textPrimary,
                fontSize = 16.sp
            )

            Switch(
                checked = isChecked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonColors.hologramGreen,
                    checkedTrackColor = NeonColors.hologramGreen.copy(alpha = 0.5f),
                    uncheckedThumbColor = NeonColors.hologramBlue,
                    uncheckedTrackColor = NeonColors.hologramBlue.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun SettingSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = label
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = NeonColors.textPrimary,
                    fontSize = 16.sp
                )

                Text(
                    text = "${(value * 100).toInt()}%",
                    color = NeonColors.hologramCyan,
                    fontSize = 16.sp
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = NeonColors.hologramCyan,
                    activeTrackColor = NeonColors.hologramCyan,
                    inactiveTrackColor = NeonColors.hologramCyan.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun DifficultySelector(
    difficulty: Int,
    onDifficultyChange: (Int) -> Unit
) {
    val difficulties = listOf("Easy", "Medium", "Hard")
    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = "Difficulty"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            difficulties.forEachIndexed { index, level ->
                val levelValue = index + 1
                HolographicButton(
                    text = level.uppercase(),
                    onClick = { onDifficultyChange(levelValue) },
                    glowColor = if (difficulty == levelValue) NeonColors.hologramGreen else NeonColors.hologramBlue,
                    modifier = Modifier.weight(1f)
                )
                if (index < difficulties.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun GameModeSelector(
    gameMode: GameMode,
    onGameModeChange: (GameMode) -> Unit
) {
    val gameModes = GameMode.entries.take(4)

    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = "Game Mode"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gameModes.chunked(2).forEach { rowModes ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowModes.forEach { mode ->
                        HolographicButton(
                            text = mode.name.replace("_", " "),
                            onClick = { onGameModeChange(mode) },
                            glowColor = if (gameMode == mode) NeonColors.hologramGreen else NeonColors.hologramBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PowerUpSelector(
    enabledPowerUps: Set<PowerUp>,
    onPowerUpToggle: (PowerUp) -> Unit
) {
    val powerUps = PowerUp.entries

    HolographicCard(
        modifier = Modifier.fillMaxWidth(),
        title = "Enabled Power-ups"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            powerUps.chunked(2).forEach { rowPowerUps ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowPowerUps.forEach { powerUp ->
                        HolographicButton(
                            text = powerUp.name,
                            onClick = { onPowerUpToggle(powerUp) },
                            glowColor = if (powerUp in enabledPowerUps) NeonColors.hologramGreen else NeonColors.hologramRed,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

