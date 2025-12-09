package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameResult
import com.neon.game.multiplier.MultiplierGame
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MultiplierViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {
    private val game = MultiplierGame()

    private val _gameState = MutableStateFlow(mapToGameState())
    val gameState: StateFlow<MultiplierUiState> = _gameState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.difficultyFlow.collect { difficulty ->
                game.start(GameDifficulty.fromLevel(difficulty))
                updateState()
            }
        }
    }

    fun onStartGame(difficulty: GameDifficulty) {
        game.start(difficulty)
        updateState()
    }

    fun onDrop(column: Int) {
        if (column !in 0 until 5) return // 5 is the number of columns
        game.drop(column)
        updateState()
    }

    fun onCashOut() {
        game.cashOut()
        updateState()
    }

    fun onRestart() {
        game.reset()
        updateState()
    }

    fun setMultiplier(multiplier: Int) {
        game.setMultiplier(multiplier)
        updateState()
    }

    private fun updateState() {
        viewModelScope.launch {
            if (game.isGameOver) {
                repository.setHighScoreMultiplier(game.score)
            }
            val bestScore = repository.prefsFlow.map { it.highScoreMultiplier }.first()
            _gameState.value = mapToGameState(bestScore)
        }
    }

    private fun mapToGameState(bestScore: Int? = null): MultiplierUiState {
        return MultiplierUiState(
            board = game.board.map { it.copyOf() }.toTypedArray(),
            score = game.score,
            bestScore = bestScore,
            currentMultiplier = game.multiplier,
            currentStreak = game.streak,
            maxStreak = game.bestStreak,
            lives = game.lives,
            lastEvent = game.lastEvent,
            isGameOver = game.isGameOver,
            result = game.result,
            difficulty = game.getDifficulty()
        )
    }
}

data class MultiplierUiState(
    val board: Array<Array<Int?>> = Array(6) { arrayOfNulls<Int>(5) },
    val score: Int = 0,
    val bestScore: Int? = null,
    val currentMultiplier: Int = 1,
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val lives: Int = 3,
    val lastEvent: MultiplierGame.Event = MultiplierGame.Event.None,
    val isGameOver: Boolean = false,
    val result: GameResult = GameResult.InProgress,
    val difficulty: GameDifficulty = GameDifficulty.MEDIUM
)
