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
import com.neon.connectsort.ui.screens.viewmodels.SettingsViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items

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
            NeonButton(
                text = "← BACK",
                onClick = { navController.popBackStack() },
                neonColor = NeonColors.neonBlue,
                modifier = Modifier.width(100.dp)
            )
            
            NeonText(
                text = "SETTINGS",
                fontSize = 24,
                fontWeight = FontWeight.Bold,
                neonColor = NeonColors.neonCyan
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
                    isChecked = settings.soundEnabled,
                    onCheckedChange = { viewModel.toggleSound() }
                )
            }
            item {
                SettingToggle(
                    label = "Background Music",
                    isChecked = settings.musicEnabled,
                    onCheckedChange = { viewModel.toggleMusic() }
                )
            }
            item {
                SettingSlider(
                    label = "Volume",
                    value = settings.volume,
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
                NeonButton(
                    text = "RESET PROGRESS",
                    onClick = { viewModel.resetProgress() },
                    neonColor = NeonColors.neonRed,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                NeonButton(
                    text = "CLEAR CACHE",
                    onClick = { viewModel.clearCache() },
                    neonColor = NeonColors.neonYellow,
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
        color = NeonColors.neonMagenta,
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
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        neonColor = if (isChecked) NeonColors.neonGreen else NeonColors.neonBlue
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
                    checkedThumbColor = NeonColors.neonGreen,
                    checkedTrackColor = NeonColors.neonGreen.copy(alpha = 0.5f),
                    uncheckedThumbColor = NeonColors.neonBlue,
                    uncheckedTrackColor = NeonColors.neonBlue.copy(alpha = 0.5f)
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
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        neonColor = NeonColors.neonCyan
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
                    color = NeonColors.neonCyan,
                    fontSize = 16.sp
                )
            }
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = NeonColors.neonCyan,
                    activeTrackColor = NeonColors.neonCyan,
                    inactiveTrackColor = NeonColors.neonCyan.copy(alpha = 0.3f)
                )
            )
        }
    }
}
