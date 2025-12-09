package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.neon.connectsort.core.data.AppPreferencesRepository

class PreferencesViewModelFactory(
    private val repository: AppPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LobbyViewModel::class.java) -> LobbyViewModel(repository)
            modelClass.isAssignableFrom(ConnectFourViewModel::class.java) -> ConnectFourViewModel(repository)
            modelClass.isAssignableFrom(BallSortViewModel::class.java) -> BallSortViewModel(repository)
            modelClass.isAssignableFrom(MultiplierViewModel::class.java) -> MultiplierViewModel(repository)
            modelClass.isAssignableFrom(ShopViewModel::class.java) -> ShopViewModel(repository)
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(repository)
            modelClass.isAssignableFrom(CharacterChipsViewModel::class.java) -> CharacterChipsViewModel(repository)
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        } as T
    }
}
