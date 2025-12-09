package com.neon.game.common

/**
 * Shared base class for all game states.
 * Provides common properties and reset functionality.
 */
open class BaseGameState(
    initialDifficulty: GameDifficulty = GameDifficulty.MEDIUM
) {
    var score: Int = 0
        protected set
    var moves: Int = 0
        protected set
    var turnCount: Int = 0
        protected set
    protected var difficultyLevel: GameDifficulty = initialDifficulty
        protected set
    var gameResult: GameResult = GameResult.IN_PROGRESS
        protected set
    var winner: Int? = null
        protected set

    val isGameOver: Boolean
        get() = gameResult.isTerminal()
    val isDraw: Boolean
        get() = gameResult == GameResult.DRAW
    val isWin: Boolean
        get() = gameResult == GameResult.WIN
    val isLoss: Boolean
        get() = gameResult == GameResult.LOSS

    /**
     * Reset the game to initial state.
     * Each concrete game implementation can extend or override this behavior.
     */
    open fun reset(newDifficulty: GameDifficulty = difficultyLevel): BaseGameState {
        score = 0
        moves = 0
        turnCount = 0
        winner = null
        difficultyLevel = newDifficulty
        gameResult = GameResult.IN_PROGRESS
        return this
    }

    /**
     * Return the current difficulty.
     */
    open fun getDifficulty(): GameDifficulty = difficultyLevel

    protected fun setResult(result: GameResult, winner: Int? = null) {
        gameResult = result
        this.winner = if (result == GameResult.WIN) winner else null
    }

    protected fun markInProgress() {
        setResult(GameResult.IN_PROGRESS)
    }

    protected fun markWin(winner: Int? = null) {
        setResult(GameResult.WIN, winner)
    }

    protected fun markLoss() {
        setResult(GameResult.LOSS)
    }

    protected fun markDraw() {
        setResult(GameResult.DRAW)
    }
}
