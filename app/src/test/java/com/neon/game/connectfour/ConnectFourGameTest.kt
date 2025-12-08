package com.neon.game.connectfour

import com.neon.game.common.GameResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConnectFourGameTest {
    private lateinit var game: ConnectFourGame

    @Before
    fun setup() {
        game = ConnectFourGame()
    }

    @Test
    fun `initial state has empty board`() {
        assertEquals(6, game.getBoard().size)
        assertEquals(7, game.getBoard()[0].size)
        assertTrue(game.getBoard().all { row -> row.all { it == null } })
    }

    @Test
    fun `initial state is player 1 turn`() {
        assertEquals(1, game.getCurrentPlayer())
        assertFalse(game.isGameOver)
    }

    @Test
    fun `drop places chip in correct column`() {
        game.dropChip(3, 1)
        assertEquals(1, game.getBoard()[5][3])
    }

    @Test
    fun `drop alternates players`() {
        game.dropChip(0, 1)
        assertEquals(2, game.getCurrentPlayer())
        
        game.dropChip(1, 2)
        assertEquals(1, game.getCurrentPlayer())
    }

    @Test
    fun `drop stacks chips correctly`() {
        game.dropChip(0, 1)
        game.dropChip(0, 2)
        game.dropChip(0, 1)
        
        assertEquals(1, game.getBoard()[5][0])
        assertEquals(2, game.getBoard()[4][0])
        assertEquals(1, game.getBoard()[3][0])
    }

    @Test
    fun `drop rejects full column`() {
        repeat(6) { game.dropChip(0, 1) }
        val stateBefore = game.getCurrentPlayer()
        game.dropChip(0, 2)
        val stateAfter = game.getCurrentPlayer()
        
        // State should not change
        assertEquals(stateBefore, stateAfter)
    }

    @Test
    fun `drop rejects invalid column`() {
        val stateBefore = game.getCurrentPlayer()
        game.dropChip(-1, 1)
        val stateAfter = game.getCurrentPlayer()
        
        assertEquals(stateBefore, stateAfter)
    }

    @Test
    fun `detects horizontal win`() {
        game.dropChip(0, 1) // P1
        game.dropChip(0, 2) // P2
        game.dropChip(1, 1) // P1
        game.dropChip(1, 2) // P2
        game.dropChip(2, 1) // P1
        game.dropChip(2, 2) // P2
        game.dropChip(3, 1) // P1 wins
        
        assertTrue(game.isGameOver)
        assertEquals(GameResult.Win(1), game.result)
    }

    @Test
    fun `detects vertical win`() {
        game.dropChip(0, 1) // P1
        game.dropChip(1, 2) // P2
        game.dropChip(0, 1) // P1
        game.dropChip(1, 2) // P2
        game.dropChip(0, 1) // P1
        game.dropChip(1, 2) // P2
        game.dropChip(0, 1) // P1 wins
        
        assertTrue(game.isGameOver)
        assertEquals(GameResult.Win(1), game.result)
    }

    @Test
    fun `reset clears board`() {
        game.dropChip(0, 1)
        game.dropChip(1, 2)
        game.dropChip(2, 1)
        
        game.reset()
        
        assertTrue(game.getBoard().all { row -> row.all { it == null } })
        assertEquals(1, game.getCurrentPlayer())
        assertFalse(game.isGameOver)
    }

    @Test
    fun `game over prevents further moves`() {
        // Create a winning condition
        game.dropChip(0, 1) // P1
        game.dropChip(1, 2) // P2
        game.dropChip(0, 1) // P1
        game.dropChip(1, 2) // P2
        game.dropChip(0, 1) // P1
        game.dropChip(1, 2) // P2
        game.dropChip(0, 1) // P1 wins
        
        val stateBefore = game.getCurrentPlayer()
        game.dropChip(2, 2)
        val stateAfter = game.getCurrentPlayer()
        
        // State should not change after game over
        assertEquals(stateBefore, stateAfter)
        assertTrue(game.isGameOver)
    }
}
