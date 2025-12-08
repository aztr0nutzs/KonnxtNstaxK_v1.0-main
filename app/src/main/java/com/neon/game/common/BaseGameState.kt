package com.neon.game.common

/**
 * Shared base class for all game states.
 * Provides common properties and reset functionality.
 */
abstract class BaseGameState {
    abstract val score: Int
    abstract val isGameOver: Boolean
    
    /**
     * Reset the game to initial state.
     * Each game implementation must define its own reset logic.
     */
    abstract fun reset(): BaseGameState
    
    /**
     * Get difficulty level (1 = easy, 2 = medium, 3 = hard).
     * Default implementation returns medium difficulty.
     */
    open fun getDifficulty(): Int = 2
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
