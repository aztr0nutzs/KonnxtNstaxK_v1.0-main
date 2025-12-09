package com.neon.game.multiplier

import com.neon.game.common.GameDifficulty
import org.junit.Assert.*
import org.junit.Test

class MultiplierGameTest {

    @Test
    fun `game initializes correctly with default state`() {
        val game = MultiplierGame()
        assertEquals(0, game.score)
        assertEquals(1, game.multiplier)
        assertEquals(3, game.lives)
        assertFalse(game.isGameOver)
    }

    @Test
    fun `start new game sets difficulty and resets state`() {
        val game = MultiplierGame()
        game.start(GameDifficulty.HARD)

        assertEquals(GameDifficulty.HARD, game.getDifficulty())
        assertEquals(0, game.score)
    }
}
