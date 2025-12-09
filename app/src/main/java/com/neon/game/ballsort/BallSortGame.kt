package com.neon.game.ballsort

import com.neon.game.common.BaseGameState
import com.neon.game.common.GameResult

/**
 * Manages the state and logic for the Ball Sort puzzle game.
 *
 * The objective of the game is to sort colored balls in tubes until each non-empty
 * tube contains balls of only a single color, and each of these tubes is full
 * to its capacity.
 *
 * @property capacity The maximum number of balls each tube can hold.
 *
 * Core Rules:
 * - A move is valid if:
 *   1. The source tube is not empty.
 *   2. The destination tube is not full.
 *   3. The ball being moved is the same color as the top ball in the destination tube,
 *      OR the destination tube is empty.
 *
 * - Win Condition:
 *   The game is won when every tube is either empty or is completely full with balls
 *   of a single, matching color.
 *
 * - State:
 *   The game state is represented by a list of tubes (`_tubes`), where each tube is a
 *   list of integers representing the colors of the balls.
 */
class BallSortGame(private val capacity: Int = 4) : BaseGameState() {

    internal var _tubes: List<MutableList<Int>> = emptyList()
    val tubes: List<List<Int>> get() = _tubes.map { it.toList() } // Expose immutable view

    var level: Int = 1
        private set

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
        this.level = level
        _tubes = generateInitialTubes(level)
        moves = 0
        turnCount = 0
        score = 0
    }

    private fun generateInitialTubes(level: Int): List<MutableList<Int>> {
        require(capacity > 0) { "Capacity must be positive, got: $capacity" }

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
        require(isValidMove(from, to)) { "Invalid move from tube $from to tube $to" }

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
        startLevel(level)
        return this
    }
}
