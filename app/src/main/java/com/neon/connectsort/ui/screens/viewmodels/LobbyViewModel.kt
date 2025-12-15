package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.core.data.GameTitle
import com.neon.game.common.GameDifficulty
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

class LobbyViewModel(
    private val preferencesRepository: AppPreferencesRepository,
    private val economy: EconomyRepository
) : ViewModel() {
    val state: StateFlow<LobbyState> = combine(
        economy.coinBalance,
        economy.unlockedChips,
        economy.selectedChipId,
        economy.highScoreFlow(GameTitle.BALL_SORT),
        economy.highScoreFlow(GameTitle.MULTIPLIER),
        economy.highScoreFlow(GameTitle.CONNECT_FOUR)
    ) { values ->
        val coins = values[0] as Int
        val unlocked = values[1] as Set<String>
        val selected = values[2] as String?
        val ballSort = values[3] as Int
        val multiplier = values[4] as Int
        val connectFour = values[5] as Int
        LobbyState(
            totalCoins = coins,
            unlockedCharacterIds = unlocked,
            selectedCharacterId = selected,
            highScoreBallSort = ballSort,
            highScoreMultiplier = multiplier,
            highScoreConnectFour = connectFour
        )
    }.combine(preferencesRepository.getDifficultyFlow()) { state, difficulty ->
        state.copy(gameDifficulty = difficulty)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LobbyState()
    )
}
