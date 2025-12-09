package com.neon.game.ballsort

import com.neon.game.common.BaseGameState
import com.neon.game.common.GameResult

class BallSortGame(private val capacity: Int = 4) : BaseGameState() {

    private var _tubes: List<MutableList<Int>> = emptyList()
    val tubes: List<List<Int>> get() = _tubes.map { it.toList() } // Expose immutable view

    private var _level: Int = 1
    val level: Int get() = _level

    override var score: Int = 0
        private set
    override var moves: Int = 0
        private set
    override var turnCount: Int = 0 // Same as moves for this game
        private set
    override val isGameOver: Boolean
        get() = isSolved()
    override val result: GameResult
        get() = when {
            isSolved() -> GameResult.Win(1) // Player 1 wins, placeholder
            else -> GameResult.InProgress
        }
    
    init {
        startLevel(1)
    }

    fun startLevel(level: Int) {
        require(level > 0) { "Level must be positive, got: $level" }
        _level = level
        _tubes = generateInitialTubes(level)
        moves = 0
        turnCount = 0
        score = 0
    }

    private fun generateInitialTubes(level: Int): List<MutableList<Int>> {
        require(capacity > 0) { "Capacity must be positive, got: $capacity" })
        
        val colorCount = (level / 2 + 2).coerceIn(2, 6)
        val colors = (0 until colorCount).flatMap { List(capacity) { it } }.toMutableList()
        val tubeCount = colorCount + 2 // add two empty tubes for maneuvering

        val initialTubes = MutableList(tubeCount) { mutableListOf<Int>() }
        colors.shuffle()
        
        // Distribute colors evenly across tubes
        colors.forEachIndexed { index, color ->
            val tubeIndex = index % colorCount // Only fill non-empty tubes initially
            initialTubes[tubeIndex].add(color)
        }
        
        return initialTubes
    }

    /**
     * Rules for a valid move:
     * 1. The source tube cannot be empty.
     * 2. The destination tube cannot be full (less than `capacity`).
     * 3. The top ball of the source tube must match the top ball of the destination tube,
     *    or the destination tube must be empty.
     */
    fun isValidMove(from: Int, to: Int): Boolean {
        if (isGameOver) return false
        if (from == to) return false
        if (from !in _tubes.indices || to !in _tubes.indices) return false
        
        val sourceTube = _tubes[from]
        val destTube = _tubes[to]

        if (sourceTube.isEmpty()) return false
        if (destTube.size >= capacity) return false

        val ballToMove = sourceTube.last()
        return destTube.isEmpty() || destTube.last() == ballToMove
    }

    fun move(from: Int, to: Int) {
        require(isValidMove(from, to)) { 
            "Invalid move from tube $from to tube $to" 
        }
        
        val ball = _tubes[from].removeLast()
        _tubes[to].add(ball)
        moves++
        turnCount++
    }

    /**
     * A puzzle is solved when every tube is either:
     * 1. Empty.
     * 2. Full to `capacity` with balls of a single color.
     */
    fun isSolved(): Boolean {
        return _tubes.all { tube ->
            tube.isEmpty() || (tube.size == capacity && tube.distinct().size == 1)
        }
    }
    
    override fun reset(): BallSortGame {
        startLevel(_level)
        return this
    }
}
