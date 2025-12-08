package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.AppContextHolder
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
    val showTutorials: Boolean = true
)

class SettingsViewModel : ViewModel() {
    private val repo = AppPreferencesRepository(AppContextHolder.appContext)

    private val _settings = MutableStateFlow(SettingsState())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            repo.prefsFlow.collect { prefs ->
                _settings.value = prefs.toState()
            }
        }
    }

    fun toggleSound() = persist { repo.setSound(!settings.value.soundEnabled) }
    fun toggleMusic() = persist { repo.setMusic(!settings.value.musicEnabled) }
    fun toggleAnimations() = persist { repo.setAnimations(!settings.value.animationsEnabled) }
    fun toggleGlowEffects() = persist { repo.setGlow(!settings.value.glowEffectsEnabled) }
    fun toggleVibration() = persist { repo.setVibration(!settings.value.vibrationEnabled) }
    fun toggleTutorials() = persist { repo.setTutorials(!settings.value.showTutorials) }

    fun setVolume(value: Float) = persist { repo.setVolume(value.coerceIn(0f, 1f)) }

    fun resetProgress() {
        viewModelScope.launch {
            repo.setSound(true)
            repo.setMusic(true)
            repo.setAnimations(true)
            repo.setGlow(true)
            repo.setVibration(true)
            repo.setTutorials(true)
            repo.setVolume(0.8f)
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
        showTutorials = showTutorials
    )

    private fun persist(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
