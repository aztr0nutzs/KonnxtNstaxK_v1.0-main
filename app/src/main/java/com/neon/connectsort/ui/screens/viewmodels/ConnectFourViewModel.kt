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
import com.neon.game.connectfour.ConnectFourAi
import com.neon.game.connectfour.ConnectFourGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

class ConnectFourViewModel(
    private val preferencesRepository: AppPreferencesRepository,
    private val economy: EconomyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val audioManager: AudioManager
) : ViewModel() {
    private val game = ConnectFourGame()
    private val ai = ConnectFourAi()
    private var pendingDrop: ConnectFourMove? = null
    private var isLocalMultiplayer = false

    private val _gameState = MutableStateFlow(ConnectFourGameState())
    val gameState: StateFlow<ConnectFourGameState> = _gameState.asStateFlow()

    private var storedHighScore = 0
    private var playerScore = 0
    private var aiScore = 0
    private var lastWinner: Int? = null
    private var hasRecordedHighScore = false
    private var resultLogged = false

    init {
        viewModelScope.launch {
            preferencesRepository.getDifficultyFlow().collect { difficulty ->
                game.reset(difficulty)
                hasRecordedHighScore = false
                updateGameState()
            }
        }

        viewModelScope.launch {
            economy.highScoreFlow(GameTitle.CONNECT_FOUR).collect { highScore ->
                storedHighScore = highScore
                updateGameState()
            }
        }

        updateGameState()
    }

    fun dropChip(column: Int) {
        if (game.isGameOver) return

        val currentPlayer = game.getCurrentPlayer()
        if (!isLocalMultiplayer && currentPlayer != 1) return
        val droppedRow = game.dropChip(column)
        if (droppedRow != null) {
            pendingDrop = ConnectFourMove(droppedRow, column, currentPlayer)
            updateGameState()

            if (!isLocalMultiplayer && !game.isGameOver) {
                viewModelScope.launch {
                    delay(500)
                    val aiColumn = ai.getBestMove(game.getBoard(), 2)
                    if (aiColumn != -1) {
                        val aiRow = game.dropChip(aiColumn)
                        if (aiRow != null) {
                            pendingDrop = ConnectFourMove(aiRow, aiColumn, 2)
                            updateGameState()
                        }
                    }
                }
            }
        }
    }

    fun resetGame() {
        game.reset(game.getDifficulty())
        hasRecordedHighScore = false
        resultLogged = false
        lastWinner = null
        updateGameState()
    }

    fun setLocalMultiplayer(enabled: Boolean) {
        if (isLocalMultiplayer == enabled) return
        isLocalMultiplayer = enabled
        playerScore = 0
        aiScore = 0
        lastWinner = null
        hasRecordedHighScore = false
        resultLogged = false
        game.reset(game.getDifficulty())
        updateGameState()
    }

    private fun updateGameState() {
        val board = game.getBoard()
        val currentPlayer = game.getCurrentPlayer()
        val winningLine = game.getWinningLine()
        val winnerId = game.winner ?: 0

        if (winnerId == 1 && lastWinner != 1) {
            playerScore++
        } else if (winnerId == 2 && lastWinner != 2) {
            aiScore++
        }

        when {
            winnerId == 1 -> lastWinner = 1
            winnerId == 2 -> lastWinner = 2
            !game.isGameOver -> lastWinner = null
            else -> lastWinner = null
        }

        val newState = ConnectFourGameState(
            board = board,
            currentPlayer = currentPlayer,
            winner = winnerId,
            winningLine = winningLine,
            playerScore = playerScore,
            aiScore = aiScore,
            isDraw = game.gameResult == GameResult.DRAW,
            isGameOver = game.isGameOver,
            difficulty = game.getDifficulty(),
            bestScore = storedHighScore,
            isLocalMultiplayer = isLocalMultiplayer,
            lastDrop = pendingDrop
        )

        _gameState.value = newState
        pendingDrop = null

        if (game.isGameOver) {
            if (!resultLogged) {
                analyticsTracker.logEvent(
                    "connect_four_result",
                    mapOf(
                        "winner" to winnerId,
                        "playerScore" to playerScore,
                        "aiScore" to aiScore
                    )
                )
                resultLogged = true
            }
            maybeSaveHighScore(newState)
        } else {
            hasRecordedHighScore = false
            resultLogged = false
        }
    }

    private fun maybeSaveHighScore(state: ConnectFourGameState) {
        if (!state.isGameOver) {
            hasRecordedHighScore = false
            return
        }
        if (hasRecordedHighScore) return
        val winnerScore = max(state.playerScore, state.aiScore)
        if (winnerScore <= storedHighScore) return
        hasRecordedHighScore = true
        storedHighScore = winnerScore
        viewModelScope.launch {
            economy.setHighScore(GameTitle.CONNECT_FOUR, winnerScore)
            analyticsTracker.logEvent(
                "connect_four_high_score",
                mapOf("winnerScore" to winnerScore, "winningPlayer" to state.winner)
            )
            audioManager.playSample(AudioManager.Sample.VICTORY)
            if (state.winner == 1) {
                val reward = 150 + state.playerScore * 10
                economy.adjustCoins(reward)
                analyticsTracker.logEvent(
                    "connect_four_reward",
                    mapOf("reward" to reward, "score" to state.playerScore)
                )
                audioManager.playSample(AudioManager.Sample.COIN)
            }
        }
    }
}

data class ConnectFourGameState(
    val board: Array<Array<Int?>> = Array(6) { arrayOfNulls<Int>(7) },
    val currentPlayer: Int = 1,
    val winner: Int = 0,
    val winningLine: List<Pair<Int, Int>> = emptyList(),
    val playerScore: Int = 0,
    val aiScore: Int = 0,
    val isDraw: Boolean = false,
    val isGameOver: Boolean = false,
    val difficulty: GameDifficulty = GameDifficulty.MEDIUM,
    val bestScore: Int = 0,
    val isLocalMultiplayer: Boolean = false,
    // last chip drop used for animated feedback
    val lastDrop: ConnectFourMove? = null
)

data class ConnectFourMove(
    val row: Int,
    val column: Int,
    val player: Int
)
