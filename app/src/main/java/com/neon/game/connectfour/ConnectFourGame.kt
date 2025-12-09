package com.neon.game.connectfour

import com.neon.game.common.BaseGameState
import com.neon.game.common.GameDifficulty

class ConnectFourGame : BaseGameState() {

    companion object {
        const val ROWS = 6
        const val COLS = 7
        const val WIN_LENGTH = 4
    }

    private var board = Array(ROWS) { arrayOfNulls<Int>(COLS) }
    private var currentPlayer = 1
    private var winningLine = emptyList<Pair<Int, Int>>()

    // BaseGameState Overrides
    fun getBoard(): Array<Array<Int?>> = board.map { it.copyOf() }.toTypedArray()

    fun getCurrentPlayer(): Int = currentPlayer

    fun getWinningLine(): List<Pair<Int, Int>> = winningLine

    fun dropChip(column: Int): Boolean {
        if (column !in 0 until COLS || isGameOver) return false

        val row = findNextAvailableRow(column) ?: return false

        board[row][column] = currentPlayer
        moves++
        turnCount++

        if (checkForWin(row, column)) {
            markWin(currentPlayer)
        } else if (isBoardFull()) {
            markDraw()
        } else {
            currentPlayer = if (currentPlayer == 1) 2 else 1
            markInProgress()
        }

        return true
    }

    private fun findNextAvailableRow(column: Int): Int? {
        return (ROWS - 1 downTo 0).firstOrNull { board[it][column] == null }
    }

    internal fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it != null } }
    }

    private fun checkForWin(row: Int, col: Int): Boolean {
        val player = board[row][col] ?: return false

        // Check in all four directions (horizontally, vertically, and both diagonals)
        val directions = listOf(0 to 1, 1 to 0, 1 to 1, 1 to -1)

        for ((dr, dc) in directions) {
            val line = mutableListOf<Pair<Int, Int>>().apply { add(row to col) }
            var count = 1

            // Check in the positive direction
            for (i in 1 until WIN_LENGTH) {
                val r = row + i * dr
                val c = col + i * dc
                if (r in 0 until ROWS && c in 0 until COLS && board[r][c] == player) {
                    count++
                    line.add(r to c)
                } else {
                    break
                }
            }

            // Check in the negative direction
            for (i in 1 until WIN_LENGTH) {
                val r = row - i * dr
                val c = col - i * dc
                if (r in 0 until ROWS && c in 0 until COLS && board[r][c] == player) {
                    count++
                    line.add(r to c)
                } else {
                    break
                }
            }

            if (count >= WIN_LENGTH) {
                winningLine = line.sortedWith(compareBy({ it.first }, { it.second }))
                return true
            }
        }
        return false
    }

    override fun reset(newDifficulty: GameDifficulty): ConnectFourGame {
        board = Array(ROWS) { arrayOfNulls(COLS) }
        currentPlayer = 1
        winningLine = emptyList()
        super.reset(newDifficulty)
        return this
    }
}
