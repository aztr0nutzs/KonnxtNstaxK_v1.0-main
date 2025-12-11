package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameResult
import com.neon.game.multiplier.MultiplierGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MultiplierViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {
    private val game = MultiplierGame()

    private val _gameState = MutableStateFlow(mapToGameState())
    val gameState: StateFlow<MultiplierGameState> = _gameState.asStateFlow()

    private var hasRecordedHighScore = false
    private var storedHighScore = 0

    init {
        viewModelScope.launch {
            repository.getDifficultyFlow().collect { difficulty ->
                game.start(difficulty)
                hasRecordedHighScore = false
                updateState()
            }
        }

        viewModelScope.launch {
            repository.prefsFlow.collect { prefs ->
                storedHighScore = prefs.highScoreMultiplier
            }
        }
    }

    fun onUserAction(action: MultiplierGame.Action) {
        game.applyMove(action)
        updateState()
    }

    fun onStartGame(difficulty: GameDifficulty) {
        viewModelScope.launch {
            repository.setDifficulty(difficulty.level)
        }
    }

    fun onRestart() {
        game.start(game.getDifficulty())
        hasRecordedHighScore = false
        updateState()
    }

    private fun updateState() {
        _gameState.value = mapToGameState()
        if (game.isGameOver) {
            maybeSaveHighScore()
        } else {
            hasRecordedHighScore = false
        }
    }

    private fun maybeSaveHighScore() {
        if (hasRecordedHighScore || game.score <= storedHighScore) return
        hasRecordedHighScore = true
        viewModelScope.launch {
            repository.setHighScoreMultiplier(game.score)
        }
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
            gameResult = game.gameResult,
            difficulty = game.getDifficulty(),
            bestScore = storedHighScore
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
    val gameResult: GameResult = GameResult.IN_PROGRESS,
    val difficulty: GameDifficulty = GameDifficulty.MEDIUM,
    val bestScore: Int = 0
)
