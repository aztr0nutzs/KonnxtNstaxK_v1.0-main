package com.neon.game.connectfour

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
        val state = game.state()
        assertEquals(6, state.board.size)
        assertEquals(7, state.board[0].size)
        assertTrue(state.board.all { row -> row.all { it == null } })
    }

    @Test
    fun `initial state is player 1 turn`() {
        val state = game.state()
        assertEquals(1, state.currentPlayer)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `drop places chip in correct column`() {
        val state = game.drop(3)
        assertEquals(1, state.board[5][3])
    }

    @Test
    fun `drop alternates players`() {
        var state = game.drop(0)
        assertEquals(2, state.currentPlayer)
        
        state = game.drop(1)
        assertEquals(1, state.currentPlayer)
    }

    @Test
    fun `drop stacks chips correctly`() {
        game.drop(0)
        game.drop(0)
        val state = game.drop(0)
        
        assertEquals(1, state.board[5][0])
        assertEquals(2, state.board[4][0])
        assertEquals(1, state.board[3][0])
    }

    @Test
    fun `drop rejects full column`() {
        repeat(6) { game.drop(0) }
        val stateBefore = game.state()
        val stateAfter = game.drop(0)
        
        // State should not change
        assertEquals(stateBefore.currentPlayer, stateAfter.currentPlayer)
    }

    @Test
    fun `drop rejects invalid column`() {
        val stateBefore = game.state()
        val stateAfter = game.drop(-1)
        
        assertEquals(stateBefore.currentPlayer, stateAfter.currentPlayer)
    }

    @Test
    fun `detects horizontal win`() {
        game.drop(0) // P1
        game.drop(0) // P2
        game.drop(1) // P1
        game.drop(1) // P2
        game.drop(2) // P1
        game.drop(2) // P2
        val state = game.drop(3) // P1 wins
        
        assertTrue(state.isGameOver)
        assertEquals(1, state.winner)
    }

    @Test
    fun `detects vertical win`() {
        game.drop(0) // P1
        game.drop(1) // P2
        game.drop(0) // P1
        game.drop(1) // P2
        game.drop(0) // P1
        game.drop(1) // P2
        val state = game.drop(0) // P1 wins
        
        assertTrue(state.isGameOver)
        assertEquals(1, state.winner)
    }

    @Test
    fun `reset clears board`() {
        game.drop(0)
        game.drop(1)
        game.drop(2)
        
        val state = game.reset()
        
        assertTrue(state.board.all { row -> row.all { it == null } })
        assertEquals(1, state.currentPlayer)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `game over prevents further moves`() {
        // Create a winning condition
        game.drop(0) // P1
        game.drop(1) // P2
        game.drop(0) // P1
        game.drop(1) // P2
        game.drop(0) // P1
        game.drop(1) // P2
        game.drop(0) // P1 wins
        
        val stateBefore = game.state()
        val stateAfter = game.drop(2)
        
        // State should not change after game over
        assertEquals(stateBefore.currentPlayer, stateAfter.currentPlayer)
        assertTrue(stateAfter.isGameOver)
    }
}
