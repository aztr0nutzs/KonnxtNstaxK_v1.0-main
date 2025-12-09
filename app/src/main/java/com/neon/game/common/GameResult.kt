package com.neon.game.common

/**
 * Common outcome states shared by every game implementation.
 */
enum class GameResult {
    IN_PROGRESS,
    WIN,
    LOSS,
    DRAW;

    fun isTerminal(): Boolean = this != IN_PROGRESS
}
