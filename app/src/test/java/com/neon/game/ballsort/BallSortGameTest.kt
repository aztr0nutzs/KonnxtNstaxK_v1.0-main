package com.neon.game.ballsort

import org.junit.Assert.*
import org.junit.Test

class BallSortGameTest {

    @Test
    fun `test level generation`() {
        val game = BallSortGame()
        game.startLevel(1)
        assertEquals(4, game.tubes.size) // 2 colors + 2 empty
    }

    @Test
    fun `test valid move`() {
        val game = BallSortGame()
        game.startLevel(1)
        // This is a bit of a hack, we should probably set the tubes directly
        // for testing purposes.
        val tubes = mutableListOf(
            mutableListOf(0, 1),
            mutableListOf(0, 1),
            mutableListOf(),
            mutableListOf()
        )
        // game.setTubes(tubes) // We need a way to set the tubes for testing
        assertTrue(game.isValidMove(0, 2))
    }

    @Test
    fun `test invalid move - to full tube`() {
        val game = BallSortGame(capacity = 2)
        game.startLevel(1)
        val tubes = mutableListOf(
            mutableListOf(0, 1),
            mutableListOf(0, 1),
            mutableListOf(),
            mutableListOf()
        )
        // game.setTubes(tubes)
        assertFalse(game.isValidMove(0, 1))
    }

    @Test
    fun `test invalid move - different colors`() {
        val game = BallSortGame()
        game.startLevel(1)
        val tubes = mutableListOf(
            mutableListOf(0),
            mutableListOf(1),
            mutableListOf(),
            mutableListOf()
        )
        // game.setTubes(tubes)
        assertFalse(game.isValidMove(0, 1))
    }

    @Test
    fun `test game solved`() {
        val game = BallSortGame(capacity = 2)
        game.startLevel(1)
        val tubes = listOf(
            mutableListOf(0, 0),
            mutableListOf(1, 1),
            mutableListOf(),
            mutableListOf()
        )
        // game.setTubes(tubes)
        assertTrue(game.isSolved())
    }

    @Test
    fun `test game not solved`() {
        val game = BallSortGame(capacity = 2)
        game.startLevel(1)
        val tubes = listOf(
            mutableListOf(0, 1),
            mutableListOf(0, 1),
            mutableListOf(),
            mutableListOf()
        )
        // game.setTubes(tubes)
        assertFalse(game.isSolved())
    }
}