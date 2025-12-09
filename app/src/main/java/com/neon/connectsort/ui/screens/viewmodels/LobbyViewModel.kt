package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class LobbyState(
    val totalCoins: Int = 0,
    val unlockedCharacterIds: Set<String> = setOf("nexus_prime"),
    val selectedCharacterId: String? = "nexus_prime",
    val activeChallenges: List<String> = emptyList(),
    val highScoreBallSort: Int = 0,
    val highScoreMultiplier: Int = 0,
    val highScoreConnectFour: Int = 0,
    val gameDifficulty: GameDifficulty = GameDifficulty.MEDIUM
)

class LobbyViewModel(repository: AppPreferencesRepository) : ViewModel() {
    val state: StateFlow<LobbyState> = repository.prefsFlow.map {
        LobbyState(
            totalCoins = it.coins,
            unlockedCharacterIds = it.unlockedCharacterIds,
            selectedCharacterId = it.selectedCharacterId,
            highScoreBallSort = it.highScoreBallSort,
            highScoreMultiplier = it.highScoreMultiplier,
            highScoreConnectFour = it.highScoreConnectFour,
            gameDifficulty = GameDifficulty.fromLevel(it.gameDifficulty)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LobbyState()
    )
}
