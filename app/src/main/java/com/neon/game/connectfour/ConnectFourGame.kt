package com.neon.game.connectfour

import com.neon.game.common.BaseGameState
import com.neon.game.common.GameResult

class ConnectFourGame : BaseGameState() {
    companion object {
        const val ROWS = 6
        const val COLS = 7
        const val WIN_LENGTH = 4
    }
    
    private val board = Array(ROWS) { arrayOfNulls<Int>(COLS) }
    private var currentPlayer = 1
    private var winningLine = mutableListOf<Pair<Int, Int>>()
    
    override var score: Int = 0
    override var isGameOver: Boolean = false
    override var moves: Int = 0
    override var turnCount: Int = 0
    override var result: GameResult = GameResult.InProgress
        private set

    fun dropChip(column: Int, player: Int): Int? {
        if (column !in 0 until COLS) return null
        if (isGameOver) return null
        
        for (row in ROWS - 1 downTo 0) {
            if (board[row][column] == null) {
                board[row][column] = player
                currentPlayer = if (player == 1) 2 else 1
                moves++
                turnCount++
                
                // Check for win
                if (checkWin(player)) {
                    isGameOver = true
                    result = GameResult.Win(player)
                } else if (isBoardFull()) {
                    isGameOver = true
                    result = GameResult.Draw
                }
                return row
            }
        }
        return null
    }
    
    fun checkWin(player: Int): Boolean {
        // Check horizontal
        for (row in 0 until ROWS) {
            for (col in 0..COLS - WIN_LENGTH) {
                var count = 0
                val line = mutableListOf<Pair<Int, Int>>()
                for (k in 0 until WIN_LENGTH) {
                    if (board[row][col + k] == player) {
                        count++
                        line.add(row to (col + k))
                    }
                }
                if (count == WIN_LENGTH) {
                    winningLine = line
                    return true
                }
            }
        }
        
        // Check vertical
        for (col in 0 until COLS) {
            for (row in 0..ROWS - WIN_LENGTH) {
                var count = 0
                val line = mutableListOf<Pair<Int, Int>>()
                for (k in 0 until WIN_LENGTH) {
                    if (board[row + k][col] == player) {
                        count++
                        line.add((row + k) to col)
                    }
                }
                if (count == WIN_LENGTH) {
                    winningLine = line
                    return true
                }
            }
        }
        
        // Check diagonal (top-left to bottom-right)
        for (row in 0..ROWS - WIN_LENGTH) {
            for (col in 0..COLS - WIN_LENGTH) {
                var count = 0
                val line = mutableListOf<Pair<Int, Int>>()
                for (k in 0 until WIN_LENGTH) {
                    if (board[row + k][col + k] == player) {
                        count++
                        line.add((row + k) to (col + k))
                    }
                }
                if (count == WIN_LENGTH) {
                    winningLine = line
                    return true
                }
            }
        }
        
        // Check diagonal (bottom-left to top-right)
        for (row in WIN_LENGTH - 1 until ROWS) {
            for (col in 0..COLS - WIN_LENGTH) {
                var count = 0
                val line = mutableListOf<Pair<Int, Int>>()
                for (k in 0 until WIN_LENGTH) {
                    if (board[row - k][col + k] == player) {
                        count++
                        line.add((row - k) to (col + k))
                    }
                }
                if (count == WIN_LENGTH) {
                    winningLine = line
                    return true
                }
            }
        }
        
        return false
    }
    
    fun getBoard(): Array<Array<Int?>> {
        return board.map { it.copyOf() }.toTypedArray()
    }
    
    fun getCurrentPlayer(): Int = currentPlayer
    
    fun getWinningLine(): List<Pair<Int, Int>> = winningLine
    
    override fun reset(): BaseGameState {
        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                board[row][col] = null
            }
        }
        currentPlayer = 1
        winningLine.clear()
        score = 0
        isGameOver = false
        moves = 0
        turnCount = 0
        result = GameResult.InProgress
        return this
    }
    
    fun isBoardFull(): Boolean {
        for (col in 0 until COLS) {
            if (board[0][col] == null) return false
        }
        return true
    }
}
