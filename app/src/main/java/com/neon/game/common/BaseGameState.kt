package com.neon.game.common

/**
 * Shared base class for all game states.
 * Provides common properties and reset functionality.
 */
abstract class BaseGameState {
    abstract val score: Int
    abstract val isGameOver: Boolean
    abstract val moves: Int
    abstract val turnCount: Int
    abstract val result: GameResult

    /**
     * Reset the game to initial state.
     * Each game implementation must define its own reset logic.
     */
    abstract fun reset(): BaseGameState

    /**
     * Get difficulty level.
     * Default implementation returns medium difficulty.
     */
    open fun getDifficulty(): GameDifficulty = GameDifficulty.MEDIUM
}

/**
 * Represents the outcome of a game.
 */
sealed class GameResult {
    object InProgress : GameResult()
    data class Win(val winner: Int) : GameResult()
    object Loss : GameResult()
    object Draw : GameResult()
}

/**
 * Difficulty levels for games.
 */
enum class GameDifficulty(val level: Int, val displayName: String) {
    EASY(1, "Easy"),
    MEDIUM(2, "Medium"),
    HARD(3, "Hard");
    
    companion object {
        fun fromLevel(level: Int): GameDifficulty = when (level) {
            1 -> EASY
            3 -> HARD
            else -> MEDIUM
        }
    }
}
