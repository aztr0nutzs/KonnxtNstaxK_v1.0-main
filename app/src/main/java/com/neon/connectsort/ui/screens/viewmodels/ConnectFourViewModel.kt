package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.game.common.GameResult
import com.neon.game.connectfour.ConnectFourGame
import com.neon.game.connectfour.ConnectFourAi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ConnectFourViewModel : ViewModel() {
    private val game = ConnectFourGame()
    private val ai = ConnectFourAi()

    private val _gameState = MutableStateFlow(ConnectFourGameState())
    val gameState: StateFlow<ConnectFourGameState> = _gameState.asStateFlow()

    private val _animateDrops = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val animateDrops: StateFlow<List<Pair<Int, Int>>> = _animateDrops.asStateFlow()

    private var moveHistory = mutableListOf<ConnectFourGameState>()

    init {
        updateGameState()
    }

    fun dropChip(column: Int) {
        if (game.isGameOver || game.getCurrentPlayer() != 1) return

        if (game.dropChip(column)) {
            updateGameState()

            // AI move
            if (!game.isGameOver) {
                viewModelScope.launch {
                    delay(500) // Small delay for visual feedback
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
        game.reset()
        moveHistory.clear()
        updateGameState()
    }

    private fun updateGameState() {
        val board = game.getBoard()
        val currentPlayer = game.getCurrentPlayer()
        val winningLine = game.getWinningLine()

        val winner = when (val gameResult = game.result) {
            is GameResult.Win -> gameResult.winner
            else -> 0
        }

        val newState = ConnectFourGameState(
            board = board,
            currentPlayer = currentPlayer,
            winner = winner,
            winningLine = winningLine,
            playerScore = if (winner == 1) gameState.value.playerScore + 1 else gameState.value.playerScore,
            aiScore = if (winner == 2) gameState.value.aiScore + 1 else gameState.value.aiScore,
            isDraw = game.result is GameResult.Draw
        )

        _gameState.value = newState
        moveHistory.add(newState)
    }
}

data class ConnectFourGameState(
    val board: Array<Array<Int?>> = Array(6) { arrayOfNulls<Int>(7) },
    val currentPlayer: Int = 1,
    val winner: Int = 0,
    val winningLine: List<Pair<Int, Int>> = emptyList(),
    val playerScore: Int = 0,
    val aiScore: Int = 0,
    val isDraw: Boolean = false
)
