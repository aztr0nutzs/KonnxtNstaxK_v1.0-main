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

    @Test
    fun `score increases on success`() {
        val game = MultiplierGame()
        game.applyMove(MultiplierGame.Action.Drop(0))
        if (game.lastEvent is MultiplierGame.Event.Success) {
            assertTrue(game.score > 0)
        }
    }

    @Test
    fun `multiplier increases with set multiplier action`() {
        val game = MultiplierGame()
        game.applyMove(MultiplierGame.Action.SetMultiplier(5))
        assertEquals(5, game.multiplier)
    }

    @Test
    fun `lives decrease on hazard`() {
        val game = MultiplierGame()
        game.start(GameDifficulty.HARD)
        game.applyMove(MultiplierGame.Action.SetMultiplier(10))
        val initialLives = game.lives

        while (game.lives == initialLives && !game.isGameOver) {
            game.applyMove(MultiplierGame.Action.Drop(0))
        }

        if (!game.isGameOver) {
            assertEquals(initialLives - 1, game.lives)
        }
    }

    @Test
    fun `game over when lives reach zero`() {
        val game = MultiplierGame(maxLives = 1)
        game.start(GameDifficulty.HARD)
        game.applyMove(MultiplierGame.Action.SetMultiplier(10))

        while (!game.isGameOver) {
            game.applyMove(MultiplierGame.Action.Drop(0))
        }

        assertTrue(game.isGameOver)
        assertEquals(0, game.lives)
    }
}
