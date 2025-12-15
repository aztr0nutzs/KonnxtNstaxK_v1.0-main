package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.core.data.AudioSettings
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameMode
import com.neon.game.common.PowerUp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class SettingsUiState(
    val audio: AudioSettings = AudioSettings(),
    val animationsEnabled: Boolean = true,
    val glowEffectsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showTutorials: Boolean = true,
    val gameDifficulty: GameDifficulty = GameDifficulty.MEDIUM,
    val gameMode: GameMode = GameMode.CLASSIC,
    val enabledPowerUps: Set<PowerUp> = setOf(PowerUp.BOMB, PowerUp.SHIELD, PowerUp.SWAP),
    val analyticsEnabled: Boolean = true
)

class SettingsViewModel(
    private val repository: AppPreferencesRepository,
    private val economy: EconomyRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsUiState())
    val settings: StateFlow<SettingsUiState> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAudioSettingsFlow(),
                repository.getDifficultyFlow(),
                repository.getGameModeFlow(),
                repository.getPowerUpsFlow(),
                repository.prefsFlow
            ) { audio, difficulty, gameMode, powerUps, prefs ->
                SettingsUiState(
                    audio = audio,
                    animationsEnabled = prefs.animationsEnabled,
                    glowEffectsEnabled = prefs.glowEffectsEnabled,
                    vibrationEnabled = prefs.vibrationEnabled,
                    showTutorials = prefs.showTutorials,
                    gameDifficulty = difficulty,
                    gameMode = gameMode,
                    enabledPowerUps = powerUps,
                    analyticsEnabled = prefs.analyticsEnabled
                )
            }.collect {
                _settings.value = it
            }
        }
    }

    fun toggleSound() = updateAudio { it.copy(soundEnabled = !it.copy().soundEnabled) }
    fun toggleMusic() = updateAudio { it.copy(musicEnabled = !it.copy().musicEnabled) }
    fun toggleAnimations() =
        viewModelScope.launch { repository.setAnimations(!settings.value.animationsEnabled) }

    fun toggleGlowEffects() =
        viewModelScope.launch { repository.setGlow(!settings.value.glowEffectsEnabled) }

    fun toggleVibration() =
        viewModelScope.launch { repository.setVibration(!settings.value.vibrationEnabled) }

    fun toggleTutorials() =
        viewModelScope.launch { repository.setTutorials(!settings.value.showTutorials) }

    fun setVolume(value: Float) = updateAudio {
        it.copy(volume = value.coerceIn(0f, 1f))
    }

    fun setDifficulty(value: Int) = viewModelScope.launch {
        repository.setDifficulty(value)
    }

    fun setGameMode(gameMode: GameMode) = viewModelScope.launch {
        repository.setGameMode(gameMode)
    }

    fun togglePowerUp(powerUp: PowerUp) {
        viewModelScope.launch {
            val currentPowerUps = settings.value.enabledPowerUps.toMutableSet()
            if (powerUp in currentPowerUps) {
                currentPowerUps.remove(powerUp)
            } else {
                currentPowerUps.add(powerUp)
            }
            repository.setPowerUps(currentPowerUps)
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.setAudioSettings(AudioSettings())
            repository.setAnimations(true)
            repository.setGlow(true)
            repository.setVibration(true)
            repository.setTutorials(true)
            repository.setDifficulty(2)
            repository.setGameMode(GameMode.CLASSIC)
            repository.setPowerUps(setOf(PowerUp.BOMB, PowerUp.SHIELD, PowerUp.SWAP))
            repository.setAnalytics(true)
            economy.resetProgress()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            economy.clearCache()
        }
    }

    fun toggleAnalytics() =
        viewModelScope.launch { repository.setAnalytics(!settings.value.analyticsEnabled) }

    private fun updateAudio(transform: (AudioSettings) -> AudioSettings) {
        viewModelScope.launch {
            repository.setAudioSettings(transform(settings.value.audio))
        }
    }
}
