package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LobbyState(
    val totalCoins: Int = 0,
    val unlockedModes: Set<String> = setOf("connect4"),
    val activeChallenges: List<String> = emptyList(),
    val highScoreBallSort: Int = 0,
    val highScoreMultiplier: Int = 0,
    val gameDifficulty: Int = 2
)

class LobbyViewModel(repository: AppPreferencesRepository) : ViewModel() {
    val state: StateFlow<LobbyState> = repository.prefsFlow.map {
        LobbyState(
            totalCoins = it.coins,
            unlockedModes = it.unlockedCharacterIds,
            highScoreBallSort = it.highScoreBallSort,
            highScoreMultiplier = it.highScoreMultiplier,
            gameDifficulty = it.gameDifficulty
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LobbyState()
    )
}
