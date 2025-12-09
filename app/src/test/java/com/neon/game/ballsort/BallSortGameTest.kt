package com.neon.game.ballsort

import org.junit.Assert.*
import org.junit.Test

class BallSortGameTest {

    @Test
    fun `game initializes with a valid, non-solved state`() {
        val game = BallSortGame()
        assertFalse("Game should not be solved immediately after initialization", game.isSolved())
        assertTrue("Game should have tubes after initialization", game.tubes.isNotEmpty())
    }

    @Test
    fun `move ball to empty tube`() {
        val game = BallSortGame()
        val fromTubeIndex = game.tubes.indexOfFirst { it.isNotEmpty() }
        val toTubeIndex = game.tubes.indexOfFirst { it.isEmpty() }

        if (fromTubeIndex == -1 || toTubeIndex == -1) {
            return
        }

        val ball = game.tubes[fromTubeIndex].last()
        game.move(fromTubeIndex, toTubeIndex)

        assertEquals(ball, game.tubes[toTubeIndex].last())
    }

    @Test
    fun `move ball to tube with matching color`() {
        val game = BallSortGame()
        val fromTubeIndex = 0
        val toTubeIndex = 1
        game._tubes[fromTubeIndex].apply {
            clear()
            add(1)
            add(2)
        }
        game._tubes[toTubeIndex].apply {
            clear()
            add(2)
        }

        game.move(fromTubeIndex, toTubeIndex)

        assertEquals(2, game.tubes[toTubeIndex].last())
        assertEquals(2, game.tubes[toTubeIndex].size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `cannot move ball to tube with different color`() {
        val game = BallSortGame()
        val fromTubeIndex = 0
        val toTubeIndex = 1
        game._tubes[fromTubeIndex].apply {
            clear()
            add(1)
        }
        game._tubes[toTubeIndex].apply {
            clear()
            add(2)
        }

        game.move(fromTubeIndex, toTubeIndex)
    }

    @Test
    fun `game is solved when all tubes are sorted`() {
        val game = BallSortGame(capacity = 2)
        game._tubes[0].apply {
            clear()
            add(1)
            add(1)
        }
        game._tubes[1].apply {
            clear()
            add(2)
            add(2)
        }
        for (i in 2 until game.tubes.size) {
            game._tubes[i].clear()
        }

        assertTrue(game.isSolved())
    }
}
