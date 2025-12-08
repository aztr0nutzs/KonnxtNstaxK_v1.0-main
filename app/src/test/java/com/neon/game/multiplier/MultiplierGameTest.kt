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
        val state = game.state()
        assertEquals(6, state.board.size)
        assertEquals(5, state.board[0].size)
        assertTrue(state.board.all { row -> row.all { it == null } })
    }

    @Test
    fun `initial state has correct defaults`() {
        val state = game.state()
        assertEquals(0, state.score)
        assertEquals(1, state.multiplier)
        assertEquals(0, state.streak)
        assertEquals(3, state.lives)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `setMultiplier updates multiplier`() {
        game.setMultiplier(5)
        val state = game.state()
        assertEquals(5, state.multiplier)
    }

    @Test
    fun `setMultiplier enforces minimum of 1`() {
        game.setMultiplier(0)
        val state = game.state()
        assertEquals(1, state.multiplier)
    }

    @Test
    fun `drop places chip on board`() {
        val state = game.drop(2)
        // Check if any chip was placed (depends on hazard randomness)
        val hasChip = state.board.any { row -> row.any { it != null } }
        val hadHazard = state.lastEvent is MultiplierGame.Event.Hazard
        
        assertTrue(hasChip || hadHazard)
    }

    @Test
    fun `drop increases score on success`() {
        var attempts = 0
        var foundSuccess = false
        
        // Try multiple times to get a successful drop (not hazard)
        while (attempts < 20 && !foundSuccess) {
            game.reset()
            val state = game.drop(0)
            if (state.lastEvent is MultiplierGame.Event.Success) {
                assertTrue(state.score > 0)
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
            val state = game.drop(0)
            if (state.lastEvent is MultiplierGame.Event.Success) {
                assertEquals(1, state.streak)
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
            val state = game.drop(0)
            if (state.lastEvent is MultiplierGame.Event.Hazard) {
                assertEquals(2, state.lives)
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
            val state = game.drop(1) // Second drop
            if (state.lastEvent is MultiplierGame.Event.Hazard) {
                assertEquals(0, state.streak)
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
        
        val state = testGame.drop(0)
        assertTrue(state.isGameOver)
    }

    @Test
    fun `cashOut ends game`() {
        val state = game.cashOut()
        assertTrue(state.isGameOver)
        assertTrue(state.lastEvent is MultiplierGame.Event.CashOut)
    }

    @Test
    fun `reset clears board and state`() {
        game.drop(0)
        game.drop(1)
        game.setMultiplier(5)
        
        val state = game.reset()
        
        assertTrue(state.board.all { row -> row.all { it == null } })
        assertEquals(0, state.score)
        assertEquals(0, state.streak)
        assertEquals(3, state.lives)
        assertEquals(1, state.multiplier)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `drop rejects invalid column`() {
        val stateBefore = game.state()
        val stateAfter = game.drop(-1)
        
        assertEquals(stateBefore.score, stateAfter.score)
        assertEquals(stateBefore.streak, stateAfter.streak)
    }

    @Test
    fun `drop rejects after game over`() {
        game.cashOut()
        val stateBefore = game.state()
        val stateAfter = game.drop(0)
        
        assertEquals(stateBefore.score, stateAfter.score)
    }

    @Test
    fun `setDifficulty updates difficulty`() {
        game.setDifficulty(GameDifficulty.HARD)
        val state = game.state()
        assertEquals(GameDifficulty.HARD, state.difficulty)
    }

    @Test
    fun `difficulty affects scoring`() {
        val easyGame = MultiplierGame(difficulty = GameDifficulty.EASY)
        val hardGame = MultiplierGame(difficulty = GameDifficulty.HARD)
        
        // Try to get successful drops for both
        var easyScore = 0
        var hardScore = 0
        
        for (i in 0 until 50) {
            easyGame.reset()
            val easyState = easyGame.drop(0)
            if (easyState.lastEvent is MultiplierGame.Event.Success) {
                easyScore = easyState.score
                break
            }
        }
        
        for (i in 0 until 50) {
            hardGame.reset()
            val hardState = hardGame.drop(0)
            if (hardState.lastEvent is MultiplierGame.Event.Success) {
                hardScore = hardState.score
                break
            }
        }
        
        // Easy mode should give higher scores
        if (easyScore > 0 && hardScore > 0) {
            assertTrue("Easy score should be higher than hard score", easyScore > hardScore)
        }
    }
}
