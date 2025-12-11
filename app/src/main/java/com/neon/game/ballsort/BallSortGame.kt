package com.neon.game.ballsort

import com.neon.game.common.BaseGameState
import com.neon.game.common.GameDifficulty

/**
 * Manages the state and logic for the Ball Sort puzzle game.
 *
 * The objective of the game is to sort colored balls in tubes until each non-empty
 * tube contains balls of only a single color, and each of these tubes is full
 * to its capacity.
 *
 * @property capacity The maximum number of balls each tube can hold.
 */
class BallSortGame(private val capacity: Int = 4) : BaseGameState() {

    internal var _tubes: List<MutableList<Int>> = emptyList()
    val tubes: List<List<Int>> get() = _tubes.map { it.toList() } // Immutable view

    var level: Int = 1
        private set

    init {
        startLevel(1)
    }

    /**
     * Prepare a fresh level configuration.
     */
    fun startLevel(level: Int) {
        require(level > 0) { "Level must be positive, got: $level" }
        this.level = level
        _tubes = generateInitialTubes(level)
        moves = 0
        turnCount = 0
        score = 0
        markInProgress()
    }

    private fun generateInitialTubes(level: Int): List<MutableList<Int>> {
        require(capacity > 0) { "Capacity must be positive, got: $capacity" }

        val baseColorCount = (level / 2 + 2).coerceIn(2, 6)
        val difficultyBonus = when (getDifficulty()) {
            GameDifficulty.EASY -> 0
            GameDifficulty.MEDIUM -> 1
            GameDifficulty.HARD -> 2
        }
        val colorCount = (baseColorCount + difficultyBonus).coerceIn(2, 8)
        val colors = (0 until colorCount).flatMap { List(capacity) { it } }.toMutableList()
        val tubeCount = colorCount + 2 // add empty tubes for maneuvering

        val initialTubes = MutableList(tubeCount) { mutableListOf<Int>() }
        colors.shuffle()

        colors.forEachIndexed { index, color ->
            val tubeIndex = index % colorCount
            initialTubes[tubeIndex].add(color)
        }

        return initialTubes
    }

    /**
     * A move is valid when the source is not empty, destination is not full,
     * and either the destination is empty or matches the ball color.
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

        val ball = _tubes[from].removeAt(_tubes[from].lastIndex)
        _tubes[to].add(ball)
        moves++
        turnCount++

        if (isSolved()) {
            markWin()
        } else {
            markInProgress()
        }
    }

    fun findHint(): Pair<Int, Int>? {
        for (from in _tubes.indices) {
            for (to in _tubes.indices) {
                if (isValidMove(from, to)) {
                    return from to to
                }
            }
        }
        return null
    }

    /**
     * A puzzle is solved when each tube is empty or uniformly filled.
     */
    fun isSolved(): Boolean {
        return _tubes.all { tube ->
            tube.isEmpty() || (tube.size == capacity && tube.distinct().size == 1)
        }
    }

    override fun reset(newDifficulty: GameDifficulty): BallSortGame {
        super.reset(newDifficulty)
        startLevel(level)
        return this
    }
}
