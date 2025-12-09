package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.AudioSettings
import com.neon.game.common.GameDifficulty
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
    val gameDifficulty: GameDifficulty = GameDifficulty.MEDIUM
)

class SettingsViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsUiState())
    val settings: StateFlow<SettingsUiState> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAudioSettingsFlow(),
                repository.getDifficultyFlow(),
                repository.prefsFlow
            ) { audio, difficulty, prefs ->
                SettingsUiState(
                    audio = audio,
                    animationsEnabled = prefs.animationsEnabled,
                    glowEffectsEnabled = prefs.glowEffectsEnabled,
                    vibrationEnabled = prefs.vibrationEnabled,
                    showTutorials = prefs.showTutorials,
                    gameDifficulty = difficulty
                )
            }.collect {
                _settings.value = it
            }
        }
    }

    fun toggleSound() = updateAudio { it.copy(soundEnabled = !it.soundEnabled) }
    fun toggleMusic() = updateAudio { it.copy(musicEnabled = !it.musicEnabled) }
    fun toggleAnimations() = viewModelScope.launch { repository.setAnimations(!settings.value.animationsEnabled) }
    fun toggleGlowEffects() = viewModelScope.launch { repository.setGlow(!settings.value.glowEffectsEnabled) }
    fun toggleVibration() = viewModelScope.launch { repository.setVibration(!settings.value.vibrationEnabled) }
    fun toggleTutorials() = viewModelScope.launch { repository.setTutorials(!settings.value.showTutorials) }

    fun setVolume(value: Float) = updateAudio {
        it.copy(volume = value.coerceIn(0f, 1f))
    }

    fun setDifficulty(value: Int) = viewModelScope.launch {
        repository.setDifficulty(value)
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.setAudioSettings(AudioSettings())
            repository.setAnimations(true)
            repository.setGlow(true)
            repository.setVibration(true)
            repository.setTutorials(true)
            repository.setDifficulty(2)
        }
    }

    fun clearCache() {
        // Persistence handled by DataStore; nothing to clear here.
    }

    private fun updateAudio(transform: (AudioSettings) -> AudioSettings) {
        viewModelScope.launch {
            repository.setAudioSettings(transform(settings.value.audio))
        }
    }
}
