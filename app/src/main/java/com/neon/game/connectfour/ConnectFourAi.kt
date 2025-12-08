package com.neon.game.connectfour

class ConnectFourAi {
    private val difficulty = 4 // Depth of minimax search
    
    fun getBestMove(board: Array<Array<Int?>>, aiPlayer: Int): Int {
        // First check for immediate win
        for (col in 0 until ConnectFourGame.COLS) {
            val testBoard = copyBoard(board)
            if (isValidMove(testBoard, col)) {
                val row = getLowestEmptyRow(testBoard, col)
                testBoard[row][col] = aiPlayer
                if (checkWin(testBoard, aiPlayer)) {
                    return col
                }
            }
        }
        
        // Check for opponent's immediate win
        val opponent = if (aiPlayer == 1) 2 else 1
        for (col in 0 until ConnectFourGame.COLS) {
            val testBoard = copyBoard(board)
            if (isValidMove(testBoard, col)) {
                val row = getLowestEmptyRow(testBoard, col)
                testBoard[row][col] = opponent
                if (checkWin(testBoard, opponent)) {
                    return col
                }
            }
        }
        
        // Use minimax for other moves
        return minimax(board, difficulty, Int.MIN_VALUE, Int.MAX_VALUE, true, aiPlayer).first
    }
    
    private fun minimax(
        board: Array<Array<Int?>>,
        depth: Int,
        alpha: Int,
        beta: Int,
        maximizingPlayer: Boolean,
        aiPlayer: Int
    ): Pair<Int, Int> {
        val opponent = if (aiPlayer == 1) 2 else 1
        val currentPlayer = if (maximizingPlayer) aiPlayer else opponent
        
        // Terminal conditions
        if (checkWin(board, aiPlayer)) return Pair(-1, 1000)
        if (checkWin(board, opponent)) return Pair(-1, -1000)
        if (depth == 0 || isBoardFull(board)) return Pair(-1, evaluateBoard(board, aiPlayer))
        
        var bestScore = if (maximizingPlayer) Int.MIN_VALUE else Int.MAX_VALUE
        var bestMove = -1
        var currentAlpha = alpha
        var currentBeta = beta
        
        for (col in 0 until ConnectFourGame.COLS) {
            if (isValidMove(board, col)) {
                val row = getLowestEmptyRow(board, col)
                val newBoard = copyBoard(board)
                newBoard[row][col] = currentPlayer
                
                val score = minimax(newBoard, depth - 1, currentAlpha, currentBeta, !maximizingPlayer, aiPlayer).second
                
                if (maximizingPlayer) {
                    if (score > bestScore) {
                        bestScore = score
                        bestMove = col
                    }
                    currentAlpha = maxOf(currentAlpha, bestScore)
                } else {
                    if (score < bestScore) {
                        bestScore = score
                        bestMove = col
                    }
                    currentBeta = minOf(currentBeta, bestScore)
                }
                
                if (currentBeta <= currentAlpha) break
            }
        }
        
        return Pair(bestMove, bestScore)
    }
    
    private fun evaluateBoard(board: Array<Array<Int?>>, aiPlayer: Int): Int {
        val opponent = if (aiPlayer == 1) 2 else 1
        var score = 0
        
        // Evaluate positions
        for (row in 0 until ConnectFourGame.ROWS) {
            for (col in 0 until ConnectFourGame.COLS) {
                if (board[row][col] == aiPlayer) {
                    // Center column preference
                    if (col == 3) score += 3
                    else if (col == 2 || col == 4) score += 2
                    else if (col == 1 || col == 5) score += 1
                } else if (board[row][col] == opponent) {
                    if (col == 3) score -= 3
                    else if (col == 2 || col == 4) score -= 2
                    else if (col == 1 || col == 5) score -= 1
                }
            }
        }
        
        return score
    }
    
    private fun copyBoard(board: Array<Array<Int?>>): Array<Array<Int?>> {
        return board.map { it.copyOf() }.toTypedArray()
    }
    
    private fun isValidMove(board: Array<Array<Int?>>, column: Int): Boolean {
        return board[0][column] == null
    }
    
    private fun getLowestEmptyRow(board: Array<Array<Int?>>, column: Int): Int {
        for (row in ConnectFourGame.ROWS - 1 downTo 0) {
            if (board[row][column] == null) return row
        }
        return -1
    }
    
    private fun isBoardFull(board: Array<Array<Int?>>): Boolean {
        for (col in 0 until ConnectFourGame.COLS) {
            if (board[0][col] == null) return false
        }
        return true
    }
    
    private fun checkWin(board: Array<Array<Int?>>, player: Int): Boolean {
        // Check horizontal
        for (row in 0 until ConnectFourGame.ROWS) {
            for (col in 0..ConnectFourGame.COLS - 4) {
                if ((0..3).all { k -> board[row][col + k] == player }) {
                    return true
                }
            }
        }
        
        // Check vertical
        for (col in 0 until ConnectFourGame.COLS) {
            for (row in 0..ConnectFourGame.ROWS - 4) {
                if ((0..3).all { k -> board[row + k][col] == player }) {
                    return true
                }
            }
        }
        
        // Check diagonal (top-left to bottom-right)
        for (row in 0..ConnectFourGame.ROWS - 4) {
            for (col in 0..ConnectFourGame.COLS - 4) {
                if ((0..3).all { k -> board[row + k][col + k] == player }) {
                    return true
                }
            }
        }
        
        // Check diagonal (bottom-left to top-right)
        for (row in 3 until ConnectFourGame.ROWS) {
            for (col in 0..ConnectFourGame.COLS - 4) {
                if ((0..3).all { k -> board[row - k][col + k] == player }) {
                    return true
                }
            }
        }
        
        return false
    }
}
