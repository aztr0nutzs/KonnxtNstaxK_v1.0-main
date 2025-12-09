package com.neon.game.multiplier

import com.neon.game.common.BaseGameState
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameResult

/**
 * Manages the state and logic for the Multiplier risk-based game.
 *
 * The objective is to drop chips to score points, with the score being multiplied
 * by the current multiplier level. Higher multipliers increase both the potential reward
 * and the risk of hitting a hazard, which costs a life.
 */
class MultiplierGame(
    private val rows: Int = 6,
    private val cols: Int = 5,
    private val baseScore: Int = 10,
    private val maxLives: Int = 3
) : BaseGameState() {

    // Game State
    var board: Array<Array<Int?>> = emptyBoard()
        private set
    override var score: Int = 0
        private set
    var multiplier: Int = 1
        private set
    var streak: Int = 0
        private set
    var bestStreak: Int = 0
        private set
    var lives: Int = maxLives
        private set
    override var isGameOver: Boolean = false
        private set
    internal var lastEvent: Event = Event.None
        private set

    // BaseGameState Overrides
    override var moves: Int = 0
        private set
    override var turnCount: Int = 0
        private set
    override val result: GameResult
        get() = when {
            !isGameOver -> GameResult.InProgress
            lives > 0 -> GameResult.Win(1) // Player wins by cashing out
            else -> GameResult.Loss
        }

    // Game-specific events and actions
    sealed class Event {
        data class Success(val points: Int) : Event()
        object Hazard : Event()
        object CashOut : Event()
        object None : Event()
    }

    sealed class Action {
        data class Drop(val column: Int) : Action()
        data class SetMultiplier(val value: Int) : Action()
        object CashOut : Action()
    }

    internal var _difficulty: GameDifficulty = GameDifficulty.MEDIUM

    fun start(newDifficulty: GameDifficulty) {
        _difficulty = newDifficulty
        reset()
    }

    override fun getDifficulty(): GameDifficulty = _difficulty

    fun applyMove(action: Action) {
        if (isGameOver) return

        when (action) {
            is Action.Drop -> drop(action.column)
            is Action.SetMultiplier -> setMultiplier(action.value)
            is Action.CashOut -> cashOut()
        }
    }

    private fun setMultiplier(value: Int) {
        multiplier = value.coerceAtLeast(1)
    }

    private fun cashOut() {
        isGameOver = true
        lastEvent = Event.CashOut
    }

    private fun drop(column: Int) {
        if (column !in 0 until cols) return
        val row = findRow(column) ?: return // Column is full

        moves++
        turnCount++

        val difficultyModifier = when (_difficulty) {
            GameDifficulty.EASY -> 0.7
            GameDifficulty.MEDIUM -> 1.0
            GameDifficulty.HARD -> 1.4
        }

        val baseHazardChance = when (multiplier) {
            1 -> 0.05
            2 -> 0.12
            3 -> 0.2
            5 -> 0.3
            else -> 0.4
        }

        val hazardChance = (baseHazardChance * difficultyModifier).coerceIn(0.0, 0.9)
        val isHazard = Math.random() < hazardChance

        if (isHazard) {
            lives -= 1
            streak = 0
            lastEvent = Event.Hazard
        } else {
            board[row][column] = 1 // Mark as filled
            streak += 1
            bestStreak = maxOf(bestStreak, streak)

            val difficultyScoreModifier = when (_difficulty) {
                GameDifficulty.EASY -> 1.5
                GameDifficulty.MEDIUM -> 1.0
                GameDifficulty.HARD -> 0.75
            }

            val streakBonus = 1 + (streak / 3)
            val gained = (baseScore * multiplier * streakBonus * difficultyScoreModifier).toInt()
            score += gained
            lastEvent = Event.Success(gained)
        }

        if (lives <= 0 || isBoardFull()) {
            isGameOver = true
        }
    }

    override fun reset(): BaseGameState {
        board = emptyBoard()
        score = 0
        streak = 0
        bestStreak = 0
        lives = maxLives
        isGameOver = false
        lastEvent = Event.None
        multiplier = 1
        moves = 0
        turnCount = 0
        return this
    }

    private fun emptyBoard(): Array<Array<Int?>> = Array(rows) { arrayOfNulls<Int>(cols) }

    private fun findRow(column: Int): Int? {
        return (rows - 1 downTo 0).firstOrNull { board[it][column] == null }
    }

    private fun isBoardFull(): Boolean = board.all { row -> row.all { it != null } }
}
