package com.neon.game.ballsort

/**
 * Pure logic for Ball Sort puzzle (no Android/Compose types).
 * Tubes hold integer color ids; capacity defaults to 4 slots per tube.
 * 
 * Exception-safe implementation with proper validation and reset functionality.
 */
class BallSortGame(
    private val capacity: Int = 4
) {
    /**
     * Generate a shuffled level with N colors and two empty tubes.
     * The level is not guaranteed to be optimally minimal but avoids overfilling.
     * 
     * @param level The level number (1-based)
     * @return List of tubes, each containing color IDs
     * @throws IllegalArgumentException if level is invalid
     */
    fun generateLevel(level: Int): List<MutableList<Int>> {
        require(level > 0) { "Level must be positive, got: $level" }
        require(capacity > 0) { "Capacity must be positive, got: $capacity" }
        
        val colorCount = (level / 2 + 2).coerceIn(2, 6)
        val colors = (0 until colorCount).flatMap { List(capacity) { it } }.toMutableList()
        val tubeCount = colorCount + 2 // add two empty tubes for maneuvering

        val tubes = MutableList(tubeCount) { mutableListOf<Int>() }
        colors.shuffle()
        
        // Distribute colors evenly across tubes
        colors.forEachIndexed { index, color ->
            val tubeIndex = index % colorCount // Only fill non-empty tubes initially
            tubes[tubeIndex].add(color)
        }
        
        return tubes
    }

    /**
     * Check if a move from one tube to another is valid.
     * 
     * @param tubes Current game state
     * @param from Source tube index
     * @param to Destination tube index
     * @return true if the move is valid
     */
    fun isValidMove(tubes: List<List<Int>>, from: Int, to: Int): Boolean {
        try {
            if (from == to) return false
            if (from !in tubes.indices || to !in tubes.indices) return false
            if (tubes[from].isEmpty()) return false
            if (tubes[to].size >= capacity) return false

            val ball = tubes[from].last()
            val target = tubes[to]
            return target.isEmpty() || target.last() == ball
        } catch (e: Exception) {
            // Catch any unexpected exceptions and return false
            return false
        }
    }

    /**
     * Execute a move from one tube to another.
     * 
     * @param tubes Current game state
     * @param from Source tube index
     * @param to Destination tube index
     * @return New game state after the move
     * @throws IllegalStateException if the move is invalid
     */
    fun move(tubes: List<List<Int>>, from: Int, to: Int): List<MutableList<Int>> {
        require(isValidMove(tubes, from, to)) { 
            "Invalid move from tube $from to tube $to" 
        }
        
        val snapshot = tubes.map { it.toMutableList() }.toMutableList()
        val ball = snapshot[from].removeLast()
        snapshot[to].add(ball)
        return snapshot
    }

    /**
     * Check if the puzzle is solved.
     * A puzzle is solved when all tubes are either empty or contain only one color at full capacity.
     * 
     * @param tubes Current game state
     * @return true if the puzzle is solved
     */
    fun isSolved(tubes: List<List<Int>>): Boolean {
        return tubes.all { tube ->
            tube.isEmpty() || (tube.size == capacity && tube.distinct().size == 1)
        }
    }
    
    /**
     * Reset the game by generating a new level.
     * 
     * @param level The level to generate
     * @return New game state
     */
    fun reset(level: Int): List<MutableList<Int>> {
        return generateLevel(level)
    }
}
