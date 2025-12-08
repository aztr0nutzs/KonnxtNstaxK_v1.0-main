package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    
    private val _dropAnimation = MutableStateFlow(0f)
    val dropAnimation: StateFlow<Float> = _dropAnimation.asStateFlow()
    
    private val _columnHeights = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val columnHeights: StateFlow<Map<Int, Float>> = _columnHeights.asStateFlow()
    
    private var moveHistory = mutableListOf<ConnectFourGameState>()
    
    init {
        updateGameState()
    }
    
    fun dropChip(column: Int) {
        if (gameState.value.winner != 0) return
        
        val playerRow = game.dropChip(column, 1) ?: return
        animateDrop(column, playerRow)

        // AI move
        viewModelScope.launch {
            delay(500) // Small delay for visual feedback

            val aiColumn = ai.getBestMove(game.getBoard(), 2)
            if (aiColumn != -1) {
                val aiRow = game.dropChip(aiColumn, 2) ?: return@launch
                animateDrop(aiColumn, aiRow)
            }
        }
    }
    
    private fun animateDrop(column: Int, row: Int) {
        viewModelScope.launch {
            // Add to animation queue
            _animateDrops.value = _animateDrops.value + (column to row)
            
            // Animate drop
            for (progress in 0..100 step 5) {
                _dropAnimation.value = progress / 100f
                val heightMap = mutableMapOf<Int, Float>()
                heightMap[column] = progress / 100f
                _columnHeights.value = heightMap
                delay(16)
            }
            
            // Update game state after animation
            updateGameState()
            
            // Clear animation
            delay(100)
            _animateDrops.value = emptyList()
            _dropAnimation.value = 0f
            _columnHeights.value = emptyMap()
        }
    }
    
    fun undoMove() {
        if (moveHistory.size > 1) {
            moveHistory.removeLast()
            val previousState = moveHistory.last()
            // In a real implementation, you would restore the game state
            // This is simplified for this example
        }
    }
    
    fun resetGame() {
        game.reset()
        _animateDrops.value = emptyList()
        _dropAnimation.value = 0f
        _columnHeights.value = emptyMap()
        moveHistory.clear()
        updateGameState()
    }
    
    private fun updateGameState() {
        val board = game.getBoard()
        val currentPlayer = game.getCurrentPlayer()
        val winner = game.getWinner()
        val winningLine = game.getWinningLine()
        
        val newState = ConnectFourGameState(
            board = board,
            currentPlayer = currentPlayer,
            winner = winner,
            winningLine = winningLine,
            playerScore = if (winner == 1) gameState.value.playerScore + 1 else gameState.value.playerScore,
            aiScore = if (winner == 2) gameState.value.aiScore + 1 else gameState.value.aiScore
        )
        
        _gameState.value = newState
        moveHistory.add(newState.copy())
    }
}

data class ConnectFourGameState(
    val board: Array<Array<Int?>> = Array(6) { arrayOfNulls<Int>(7) },
    val currentPlayer: Int = 1,
    val winner: Int = 0,
    val winningLine: List<Pair<Int, Int>> = emptyList(),
    val playerScore: Int = 0,
    val aiScore: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as ConnectFourGameState
        
        if (!board.contentDeepEquals(other.board)) return false
        if (currentPlayer != other.currentPlayer) return false
        if (winner != other.winner) return false
        if (winningLine != other.winningLine) return false
        if (playerScore != other.playerScore) return false
        if (aiScore != other.aiScore) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = board.contentDeepHashCode()
        result = 31 * result + currentPlayer
        result = 31 * result + winner
        result = 31 * result + winningLine.hashCode()
        result = 31 * result + playerScore
        result = 31 * result + aiScore
        return result
    }
    
    fun copy(): ConnectFourGameState {
        val newBoard = Array(6) { row ->
            Array(7) { col ->
                board[row][col]
            }
        }
        return ConnectFourGameState(
            board = newBoard,
            currentPlayer = currentPlayer,
            winner = winner,
            winningLine = winningLine.toList(),
            playerScore = playerScore,
            aiScore = aiScore
        )
    }
}
