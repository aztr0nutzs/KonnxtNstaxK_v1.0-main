package com.neon.game.ballsort

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BallSortGameTest {
    private lateinit var game: BallSortGame

    @Before
    fun setup() {
        game = BallSortGame(capacity = 4)
    }

    @Test
    fun `generateLevel creates correct number of tubes`() {
        val tubes = game.generateLevel(1)
        // Level 1: (1/2 + 2) = 2 colors, +2 empty = 4 tubes
        assertEquals(4, tubes.size)
    }

    @Test
    fun `generateLevel creates correct number of balls`() {
        val tubes = game.generateLevel(1)
        val totalBalls = tubes.sumOf { it.size }
        // 2 colors * 4 capacity = 8 balls
        assertEquals(8, totalBalls)
    }

    @Test
    fun `isValidMove rejects same tube`() {
        val tubes = game.generateLevel(1)
        assertFalse(game.isValidMove(tubes, 0, 0))
    }

    @Test
    fun `isValidMove rejects empty source tube`() {
        val tubes = listOf(
            mutableListOf<Int>(),
            mutableListOf(1, 2)
        )
        assertFalse(game.isValidMove(tubes, 0, 1))
    }

    @Test
    fun `isValidMove rejects full destination tube`() {
        val tubes = listOf(
            mutableListOf(1),
            mutableListOf(2, 2, 2, 2)
        )
        assertFalse(game.isValidMove(tubes, 0, 1))
    }

    @Test
    fun `isValidMove accepts move to empty tube`() {
        val tubes = listOf(
            mutableListOf(1, 2),
            mutableListOf<Int>()
        )
        assertTrue(game.isValidMove(tubes, 0, 1))
    }

    @Test
    fun `isValidMove accepts move to matching color`() {
        val tubes = listOf(
            mutableListOf(1, 2),
            mutableListOf(2)
        )
        assertTrue(game.isValidMove(tubes, 0, 1))
    }

    @Test
    fun `isValidMove rejects move to different color`() {
        val tubes = listOf(
            mutableListOf(1, 2),
            mutableListOf(3)
        )
        assertFalse(game.isValidMove(tubes, 0, 1))
    }

    @Test
    fun `move executes valid move correctly`() {
        val tubes = listOf(
            mutableListOf(1, 2),
            mutableListOf<Int>()
        )
        val result = game.move(tubes, 0, 1)
        
        assertEquals(1, result[0].size)
        assertEquals(1, result[1].size)
        assertEquals(2, result[1][0])
    }

    @Test(expected = IllegalStateException::class)
    fun `move throws on invalid move`() {
        val tubes = listOf(
            mutableListOf<Int>(),
            mutableListOf(1)
        )
        game.move(tubes, 0, 1)
    }

    @Test
    fun `isSolved returns true for solved puzzle`() {
        val tubes = listOf(
            mutableListOf(1, 1, 1, 1),
            mutableListOf(2, 2, 2, 2),
            mutableListOf<Int>(),
            mutableListOf<Int>()
        )
        assertTrue(game.isSolved(tubes))
    }

    @Test
    fun `isSolved returns false for unsolved puzzle`() {
        val tubes = listOf(
            mutableListOf(1, 2, 1, 1),
            mutableListOf(2, 2, 2),
            mutableListOf<Int>(),
            mutableListOf<Int>()
        )
        assertFalse(game.isSolved(tubes))
    }

    @Test
    fun `isSolved returns false for incomplete tubes`() {
        val tubes = listOf(
            mutableListOf(1, 1, 1),
            mutableListOf(2, 2, 2, 2),
            mutableListOf<Int>(),
            mutableListOf<Int>()
        )
        assertFalse(game.isSolved(tubes))
    }

    @Test
    fun `reset generates new level`() {
        val tubes1 = game.generateLevel(1)
        val tubes2 = game.reset(1)
        
        assertEquals(tubes1.size, tubes2.size)
        // Different shuffle should produce different arrangement
        // (statistically very unlikely to be identical)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `generateLevel throws on invalid level`() {
        game.generateLevel(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `generateLevel throws on negative level`() {
        game.generateLevel(-1)
    }
}
