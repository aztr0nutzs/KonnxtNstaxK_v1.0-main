package com.neon.game.common

/**
 * Difficulty tiers used across gameplay modules.
 */
enum class GameDifficulty(val level: Int, val displayName: String) {
    EASY(level = 1, displayName = "Easy"),
    MEDIUM(level = 2, displayName = "Medium"),
    HARD(level = 3, displayName = "Hard");

    companion object {
        fun fromLevel(level: Int): GameDifficulty = when (level) {
            1 -> EASY
            3 -> HARD
            else -> MEDIUM
        }

        fun fromName(name: String): GameDifficulty = when (name.trim().lowercase()) {
            "easy" -> EASY
            "hard" -> HARD
            else -> MEDIUM
        }
    }
}
