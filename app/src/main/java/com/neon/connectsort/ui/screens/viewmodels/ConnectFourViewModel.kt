package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameResult
import com.neon.game.connectfour.ConnectFourAi
import com.neon.game.connectfour.ConnectFourGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConnectFourViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {
    private val game = ConnectFourGame()
    private val ai = ConnectFourAi()

    private val _gameState = MutableStateFlow(ConnectFourGameState())
    val gameState: StateFlow<ConnectFourGameState> = _gameState.asStateFlow()

    private var storedHighScore = 0
    private var playerScore = 0
    private var aiScore = 0
    private var lastWinner: Int? = null
    private var hasRecordedHighScore = false

    init {
        viewModelScope.launch {
            repository.getDifficultyFlow().collect { difficulty ->
                game.reset(difficulty)
                hasRecordedHighScore = false
                updateGameState()
            }
        }

        viewModelScope.launch {
            repository.prefsFlow.collect { prefs ->
                storedHighScore = prefs.highScoreConnectFour
                updateGameState()
            }
        }

        updateGameState()
    }

    fun dropChip(column: Int) {
        if (game.isGameOver || game.getCurrentPlayer() != 1) return

        if (game.dropChip(column)) {
            updateGameState()

            if (!game.isGameOver) {
                viewModelScope.launch {
                    delay(500)
                    val aiColumn = ai.getBestMove(game.getBoard(), 2)
                    if (aiColumn != -1) {
                        game.dropChip(aiColumn)
                        updateGameState()
                    }
                }
            }
        }
    }

    fun resetGame() {
        game.reset(game.getDifficulty())
        hasRecordedHighScore = false
        lastWinner = null
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
            difficulty = game.getDifficulty(),
            bestScore = storedHighScore
        )

        _gameState.value = newState

        if (game.isGameOver) {
            maybeSaveHighScore(newState)
        } else {
            hasRecordedHighScore = false
        }
    }

    private fun maybeSaveHighScore(state: ConnectFourGameState) {
        if (state.winner != 1) {
            hasRecordedHighScore = false
            return
        }

        if (hasRecordedHighScore) return
        if (state.playerScore <= storedHighScore) return

        hasRecordedHighScore = true
        viewModelScope.launch {
            repository.setHighScoreConnectFour(state.playerScore)
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
    val difficulty: GameDifficulty = GameDifficulty.MEDIUM,
    val bestScore: Int = 0
)
