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
}
