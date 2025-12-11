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
    fun `dropChip places chip in correct column`() {
        game.dropChip(3)
        assertEquals(1, game.getBoard()[5][3])
    }

    @Test
    fun `dropChip alternates players`() {
        game.dropChip(0)
        assertEquals(2, game.getCurrentPlayer())

        game.dropChip(1)
        assertEquals(1, game.getCurrentPlayer())
    }

    @Test
    fun `dropChip stacks chips correctly`() {
        game.dropChip(0) // P1
        game.dropChip(0) // P2
        game.dropChip(0) // P1

        assertEquals(1, game.getBoard()[5][0])
        assertEquals(2, game.getBoard()[4][0])
        assertEquals(1, game.getBoard()[3][0])
    }

    @Test
    fun `dropChip rejects full column`() {
        repeat(6) { 
            game.dropChip(0) // Alternate players
        }
        val result = game.dropChip(0) // Attempt to drop in full column
        assertNull(result)
    }

    @Test
    fun `dropChip rejects invalid column`() {
        assertNull(game.dropChip(-1))
        assertNull(game.dropChip(ConnectFourGame.COLS))
    }

    @Test
    fun `detects horizontal win`() {
        game.dropChip(0) // P1
        game.dropChip(0) // P2
        game.dropChip(1) // P1
        game.dropChip(1) // P2
        game.dropChip(2) // P1
        game.dropChip(2) // P2
        game.dropChip(3) // P1 wins

        assertTrue(game.isGameOver)
        assertEquals(GameResult.WIN, game.gameResult)
        assertEquals(1, game.winner)
    }

    @Test
    fun `detects vertical win`() {
        game.dropChip(0) // P1
        game.dropChip(1) // P2
        game.dropChip(0) // P1
        game.dropChip(1) // P2
        game.dropChip(0) // P1
        game.dropChip(1) // P2
        game.dropChip(0) // P1 wins

        assertTrue(game.isGameOver)
        assertEquals(GameResult.WIN, game.gameResult)
        assertEquals(1, game.winner)
    }

    @Test
    fun `detects up-right diagonal win`() {
        game.dropChip(0) // P1
        game.dropChip(1) // P2
        game.dropChip(1) // P1
        game.dropChip(2) // P2
        game.dropChip(2) // P1
        game.dropChip(3) // P2
        game.dropChip(2) // P1
        game.dropChip(3) // P2
        game.dropChip(3) // P1
        game.dropChip(0) // P2
        game.dropChip(3) // P1 wins

        assertTrue(game.isGameOver)
        assertEquals(GameResult.WIN, game.gameResult)
        assertEquals(1, game.winner)
    }

    @Test
    fun `detects down-right diagonal win`() {
        game.dropChip(3) // P1
        game.dropChip(2) // P2
        game.dropChip(2) // P1
        game.dropChip(1) // P2
        game.dropChip(1) // P1
        game.dropChip(0) // P2
        game.dropChip(1) // P1
        game.dropChip(0) // P2
        game.dropChip(0) // P1
        game.dropChip(3) // P2
        game.dropChip(0) // P1 wins
        
        assertTrue(game.isGameOver)
        assertEquals(GameResult.WIN, game.gameResult)
        assertEquals(1, game.winner)
    }

    @Test
    fun `AI vs AI game completes`() {
        val ai1 = ConnectFourAi()
        val ai2 = ConnectFourAi()

        while (!game.isGameOver) {
            val currentPlayer = game.getCurrentPlayer()
            val ai = if (currentPlayer == 1) ai1 else ai2
            val move = ai.getBestMove(game.getBoard(), currentPlayer)
            game.dropChip(move)
        }

        assertTrue(game.isGameOver)
        assertTrue(game.gameResult == GameResult.WIN || game.gameResult == GameResult.DRAW)
    }


    @Test
    fun `reset clears board`() {
        game.dropChip(0)
        game.dropChip(1)
        game.dropChip(2)

        game.reset()

        assertTrue(game.getBoard().all { row -> row.all { it == null } })
        assertEquals(1, game.getCurrentPlayer())
        assertFalse(game.isGameOver)
    }

    @Test
    fun `game over prevents further moves`() {
        // Create a winning condition
        game.dropChip(0) // P1
        game.dropChip(1) // P2
        game.dropChip(0) // P1
        game.dropChip(1) // P2
        game.dropChip(0) // P1
        game.dropChip(1) // P2
        game.dropChip(0) // P1 wins

        assertTrue(game.isGameOver)
        assertNull(game.dropChip(2)) // Attempt another move
    }
}
