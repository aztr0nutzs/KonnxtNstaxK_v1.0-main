package com.neon.game.connectfour

class ConnectFourGame {
    companion object {
        const val ROWS = 6
        const val COLS = 7
        const val WIN_LENGTH = 4
    }
    
    private val board = Array(ROWS) { arrayOfNulls<Int>(COLS) }
    private var currentPlayer = 1
    private var winner = 0
    private var winningLine = mutableListOf<Pair<Int, Int>>()
    
    fun dropChip(column: Int, player: Int): Int? {
        if (column !in 0 until COLS) return null
        if (winner != 0) return null
        
        for (row in ROWS - 1 downTo 0) {
            if (board[row][column] == null) {
                board[row][column] = player
                currentPlayer = if (player == 1) 2 else 1
                
                // Check for win
                if (checkWin(player)) {
                    winner = player
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
    
    fun getWinner(): Int = winner
    
    fun getWinningLine(): List<Pair<Int, Int>> = winningLine
    
    fun reset() {
        for (row in 0 until ROWS) {
            for (col in 0 until COLS) {
                board[row][col] = null
            }
        }
        currentPlayer = 1
        winner = 0
        winningLine.clear()
    }
    
    fun isBoardFull(): Boolean {
        for (col in 0 until COLS) {
            if (board[0][col] == null) return false
        }
        return true
    }
}
