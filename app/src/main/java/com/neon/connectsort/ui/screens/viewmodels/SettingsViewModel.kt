package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.UserPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsState(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val volume: Float = 0.8f,
    val animationsEnabled: Boolean = true,
    val glowEffectsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showTutorials: Boolean = true,
    val gameDifficulty: Int = 2
)

class SettingsViewModel(
    private val repository: AppPreferencesRepository? = null
) : ViewModel() {

    private val _settings = MutableStateFlow(SettingsState())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            repository?.prefsFlow?.collect { prefs ->
                _settings.value = prefs.toState()
            }
        }
    }

    fun toggleSound() = persist { repository?.setSound(!settings.value.soundEnabled) }
    fun toggleMusic() = persist { repository?.setMusic(!settings.value.musicEnabled) }
    fun toggleAnimations() = persist { repository?.setAnimations(!settings.value.animationsEnabled) }
    fun toggleGlowEffects() = persist { repository?.setGlow(!settings.value.glowEffectsEnabled) }
    fun toggleVibration() = persist { repository?.setVibration(!settings.value.vibrationEnabled) }
    fun toggleTutorials() = persist { repository?.setTutorials(!settings.value.showTutorials) }

    fun setVolume(value: Float) = persist { repository?.setVolume(value.coerceIn(0f, 1f)) }
    fun setDifficulty(value: Int) = persist { repository?.setDifficulty(value) }

    fun resetProgress() {
        viewModelScope.launch {
            repository?.setSound(true)
            repository?.setMusic(true)
            repository?.setAnimations(true)
            repository?.setGlow(true)
            repository?.setVibration(true)
            repository?.setTutorials(true)
            repository?.setVolume(0.8f)
            repository?.setDifficulty(2)
        }
    }

    fun clearCache() {
        // No-op placeholder; persistence handled via DataStore already.
    }

    private fun UserPrefs.toState() = SettingsState(
        soundEnabled = soundEnabled,
        musicEnabled = musicEnabled,
        volume = volume,
        animationsEnabled = animationsEnabled,
        glowEffectsEnabled = glowEffectsEnabled,
        vibrationEnabled = vibrationEnabled,
        showTutorials = showTutorials,
        gameDifficulty = gameDifficulty
    )

    private fun persist(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
