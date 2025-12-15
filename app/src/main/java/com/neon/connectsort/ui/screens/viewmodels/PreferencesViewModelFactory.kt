package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.connectsort.ui.audio.AudioManager
import com.neon.connectsort.ui.screens.viewmodels.StoryHubViewModel

class PreferencesViewModelFactory(
    private val preferencesRepository: AppPreferencesRepository,
    private val economyRepository: EconomyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val audioManager: AudioManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LobbyViewModel::class.java) ->
                LobbyViewModel(preferencesRepository, economyRepository)
            modelClass.isAssignableFrom(ConnectFourViewModel::class.java) ->
                ConnectFourViewModel(preferencesRepository, economyRepository, analyticsTracker, audioManager)
            modelClass.isAssignableFrom(BallSortViewModel::class.java) ->
                BallSortViewModel(preferencesRepository, economyRepository, analyticsTracker, audioManager)
            modelClass.isAssignableFrom(MultiplierViewModel::class.java) ->
                MultiplierViewModel(preferencesRepository, economyRepository, analyticsTracker, audioManager)
            modelClass.isAssignableFrom(ShopViewModel::class.java) ->
                ShopViewModel(economyRepository, analyticsTracker, audioManager)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(preferencesRepository, economyRepository)
            modelClass.isAssignableFrom(CharacterChipsViewModel::class.java) ->
                CharacterChipsViewModel(economyRepository, analyticsTracker, audioManager)
            modelClass.isAssignableFrom(StoryHubViewModel::class.java) ->
                StoryHubViewModel(preferencesRepository, economyRepository, analyticsTracker, audioManager)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        } as T
    }
}
