package com.neon.game.multiplier

import com.neon.game.common.GameDifficulty
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MultiplierGameTest {
    private lateinit var game: MultiplierGame

    @Before
    fun setup() {
        game = MultiplierGame()
    }

    @Test
    fun `initial state has empty board`() {
        assertEquals(6, game.board.size)
        assertEquals(5, game.board[0].size)
        assertTrue(game.board.all { row -> row.all { it == null } })
    }

    @Test
    fun `initial state has correct defaults`() {
        assertEquals(0, game.score)
        assertEquals(1, game.multiplier)
        assertEquals(0, game.streak)
        assertEquals(3, game.lives)
        assertFalse(game.isGameOver)
    }

    @Test
    fun `setMultiplier updates multiplier`() {
        game.setMultiplier(5)
        assertEquals(5, game.multiplier)
    }

    @Test
    fun `setMultiplier enforces minimum of 1`() {
        game.setMultiplier(0)
        assertEquals(1, game.multiplier)
    }

    @Test
    fun `drop places chip on board`() {
        game.drop(2)
        // Check if any chip was placed (depends on hazard randomness)
        val hasChip = game.board.any { row -> row.any { it != null } }
        val hadHazard = game.lastEvent is MultiplierGame.Event.Hazard
        
        assertTrue(hasChip || hadHazard)
    }

    @Test
    fun `drop increases score on success`() {
        var attempts = 0
        var foundSuccess = false
        
        // Try multiple times to get a successful drop (not hazard)
        while (attempts < 20 && !foundSuccess) {
            game.reset()
            game.drop(0)
            if (game.lastEvent is MultiplierGame.Event.Success) {
                assertTrue(game.score > 0)
                foundSuccess = true
            }
            attempts++
        }
        
        assertTrue("Should find at least one successful drop", foundSuccess)
    }

    @Test
    fun `drop increases streak on success`() {
        var attempts = 0
        var foundSuccess = false
        
        while (attempts < 20 && !foundSuccess) {
            game.reset()
            game.drop(0)
            if (game.lastEvent is MultiplierGame.Event.Success) {
                assertEquals(1, game.streak)
                foundSuccess = true
            }
            attempts++
        }
        
        assertTrue("Should find at least one successful drop", foundSuccess)
    }

    @Test
    fun `hazard decreases lives`() {
        var attempts = 0
        var foundHazard = false
        
        while (attempts < 50 && !foundHazard) {
            game.reset()
            game.drop(0)
            if (game.lastEvent is MultiplierGame.Event.Hazard) {
                assertEquals(2, game.lives)
                foundHazard = true
            }
            attempts++
        }
        
        assertTrue("Should find at least one hazard", foundHazard)
    }

    @Test
    fun `hazard resets streak`() {
        var attempts = 0
        var foundHazard = false
        
        while (attempts < 50 && !foundHazard) {
            game.reset()
            game.drop(0) // First drop
            game.drop(1) // Second drop
            if (game.lastEvent is MultiplierGame.Event.Hazard) {
                assertEquals(0, game.streak)
                foundHazard = true
            }
            attempts++
        }
        
        assertTrue("Should find at least one hazard", foundHazard)
    }

    @Test
    fun `game over when lives reach zero`() {
        val testGame = MultiplierGame(maxLives = 1)
        testGame.setMultiplier(10) // High multiplier for guaranteed hazard
        
        testGame.drop(0)
        assertTrue(testGame.isGameOver)
    }

    @Test
    fun `cashOut ends game`() {
        game.cashOut()
        assertTrue(game.isGameOver)
        assertTrue(game.lastEvent is MultiplierGame.Event.CashOut)
    }

    @Test
    fun `reset clears board and state`() {
        game.drop(0)
        game.drop(1)
        game.setMultiplier(5)
        
        game.reset()
        
        assertTrue(game.board.all { row -> row.all { it == null } })
        assertEquals(0, game.score)
        assertEquals(0, game.streak)
        assertEquals(3, game.lives)
        assertEquals(1, game.multiplier)
        assertFalse(game.isGameOver)
    }

    @Test
    fun `drop rejects invalid column`() {
        val scoreBefore = game.score
        game.drop(-1)
        val scoreAfter = game.score
        
        assertEquals(scoreBefore, scoreAfter)
    }

    @Test
    fun `drop rejects after game over`() {
        game.cashOut()
        val scoreBefore = game.score
        game.drop(0)
        val scoreAfter = game.score
        
        assertEquals(scoreBefore, scoreAfter)
    }

    @Test
    fun `start updates difficulty`() {
        game.start(GameDifficulty.HARD)
        assertEquals(GameDifficulty.HARD, game.getDifficulty())
    }

    @Test
    fun `difficulty affects scoring`() {
        val easyGame = MultiplierGame()
        easyGame.start(GameDifficulty.EASY)
        val hardGame = MultiplierGame()
        hardGame.start(GameDifficulty.HARD)
        
        // Try to get successful drops for both
        var easyScore = 0
        var hardScore = 0
        
        for (i in 0 until 50) {
            easyGame.reset()
            easyGame.drop(0)
            if (easyGame.lastEvent is MultiplierGame.Event.Success) {
                easyScore = easyGame.score
                break
            }
        }
        
        for (i in 0 until 50) {
            hardGame.reset()
            hardGame.drop(0)
            if (hardGame.lastEvent is MultiplierGame.Event.Success) {
                hardScore = hardGame.score
                break
            }
        }
        
        // Easy mode should give higher scores
        if (easyScore > 0 && hardScore > 0) {
            assertTrue("Easy score should be higher than hard score", easyScore > hardScore)
        }
    }
}
