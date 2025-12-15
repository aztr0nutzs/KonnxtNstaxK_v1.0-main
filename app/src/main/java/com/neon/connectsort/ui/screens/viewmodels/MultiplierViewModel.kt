package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.core.data.GameTitle
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.connectsort.ui.audio.AudioManager
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameResult
import com.neon.game.multiplier.MultiplierGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MultiplierViewModel(
    private val preferencesRepository: AppPreferencesRepository,
    private val economy: EconomyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val audioManager: AudioManager
) : ViewModel() {
    private val game = MultiplierGame()

    private val _gameState = MutableStateFlow(mapToGameState())
    val gameState: StateFlow<MultiplierGameState> = _gameState.asStateFlow()

    private var hasRecordedHighScore = false
    private var storedHighScore = 0

    init {
        viewModelScope.launch {
            preferencesRepository.getDifficultyFlow().collect { difficulty ->
                game.start(difficulty)
                hasRecordedHighScore = false
                updateState()
            }
        }

        viewModelScope.launch {
            economy.highScoreFlow(GameTitle.MULTIPLIER).collect { highScore ->
                storedHighScore = highScore
            }
        }
    }

    fun onUserAction(action: MultiplierGame.Action) {
        game.applyMove(action)
        updateState()
    }

    fun onStartGame(difficulty: GameDifficulty) {
        viewModelScope.launch {
            preferencesRepository.setDifficulty(difficulty.level)
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
        storedHighScore = game.score
        viewModelScope.launch {
            economy.setHighScore(GameTitle.MULTIPLIER, game.score)
            analyticsTracker.logEvent(
                "multiplier_high_score",
                mapOf("score" to game.score, "streak" to game.bestStreak)
            )
            audioManager.playSample(AudioManager.Sample.VICTORY)
            val reward = 200 + game.score * 3
            economy.earnCoins(reward)
            analyticsTracker.logEvent("multiplier_reward", mapOf("reward" to reward))
            audioManager.playSample(AudioManager.Sample.COIN)
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
