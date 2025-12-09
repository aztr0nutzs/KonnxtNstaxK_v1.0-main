package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import com.neon.game.multiplier.MultiplierGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MultiplierViewModel(
    private val repository: AppPreferencesRepository? = null
) : ViewModel() {
    private val game = MultiplierGame()

    private val _gameState = MutableStateFlow(mapToGameState())
    val gameState: StateFlow<MultiplierGameState> = _gameState.asStateFlow()

    fun onUserAction(action: MultiplierGame.Action) {
        game.applyMove(action)
        updateState()
    }

    fun onStartGame(difficulty: GameDifficulty) {
        game.start(difficulty)
        updateState()
    }

    fun onRestart() {
        // Restart the game with the current difficulty
        game.start(game.getDifficulty())
        updateState()
    }

    private fun updateState() {
        _gameState.value = mapToGameState()
    }

    private fun mapToGameState(): MultiplierGameState {
        return MultiplierGameState(
            board = game.board.map { it.copyOf() }.toTypedArray(),
            score = game.score,
            currentMultiplier = game.multiplier,
            currentStreak = game.streak,
            maxStreak = game.bestStreak,
            lives = game.lives,
            lastEvent = game.lastEvent,
            isGameOver = game.isGameOver,
            difficulty = game.getDifficulty()
        )
    }
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
