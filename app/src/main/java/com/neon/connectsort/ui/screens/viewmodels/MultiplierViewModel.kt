package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.AppContextHolder
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import com.neon.game.multiplier.MultiplierGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MultiplierViewModel(
    private val repository: AppPreferencesRepository = AppPreferencesRepository(AppContextHolder.appContext)
) : ViewModel() {
    private val game = MultiplierGame()

    private val _gameState = MutableStateFlow(mapState(game.state()))
    val gameState: StateFlow<MultiplierGameState> = _gameState.asStateFlow()

    fun setMultiplier(multiplier: Int) {
        game.setMultiplier(multiplier)
        _gameState.value = mapState(game.state())
    }

    fun dropChip(column: Int) {
        _gameState.value = mapState(game.drop(column))
    }

    fun cashOut() {
        _gameState.value = mapState(game.cashOut())
    }

    fun resetGame() {
        _gameState.value = mapState(game.reset())
    }
    
    fun setDifficulty(difficulty: GameDifficulty) {
        game.setDifficulty(difficulty)
        _gameState.value = mapState(game.state())
    }

    private fun mapState(state: MultiplierGame.State): MultiplierGameState =
        MultiplierGameState(
            board = state.board,
            score = state.score,
            currentMultiplier = state.multiplier,
            currentStreak = state.streak,
            maxStreak = state.bestStreak,
            lives = state.lives,
            lastEvent = state.lastEvent,
            isGameOver = state.isGameOver,
            difficulty = state.difficulty
        )
}

data class MultiplierGameState(
    val board: Array<Array<Int?>> = Array(6) { arrayOfNulls<Int>(5) },
    val score: Int = 0,
    val currentMultiplier: Int = 1,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val lives: Int = 3,
    val lastEvent: MultiplierGame.Event = MultiplierGame.Event.None,
    val isGameOver: Boolean = false,
    val difficulty: GameDifficulty = GameDifficulty.MEDIUM
)
