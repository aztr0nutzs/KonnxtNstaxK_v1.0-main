package com.neon.game.common

import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameCommonTest {

    private class TestGameState : BaseGameState() {
        fun seedState() {
            score = 42
            moves = 7
            turnCount = 3
        }

        fun exposeWin(winner: Int? = 1) = markWin(winner)
        fun exposeLoss() = markLoss()
        fun exposeDraw() = markDraw()
        fun exposeInProgress() = markInProgress()
    }

    @Test
    fun `reset clears counters and applies difficulty`() {
        val game = TestGameState()
        game.seedState()
        game.reset(GameDifficulty.HARD)

        assertEquals(0, game.score)
        assertEquals(0, game.moves)
        assertEquals(0, game.turnCount)
        assertEquals(GameDifficulty.HARD, game.getDifficulty())
    }

    @Test
    fun `mark win exposes winner and terminal flag`() {
        val game = TestGameState()
        game.exposeWin(winner = 2)

        assertEquals(GameResult.WIN, game.gameResult)
        assertEquals(2, game.winner)
        assertTrue(game.isWin)
    }

    @Test
    fun `mark loss toggles loss state`() {
        val game = TestGameState()
        game.exposeLoss()

        assertEquals(GameResult.LOSS, game.gameResult)
        assertTrue(game.isLoss)
    }

    @Test
    fun `mark draw sets draw result`() {
        val game = TestGameState()
        game.exposeDraw()

        assertEquals(GameResult.DRAW, game.gameResult)
        assertTrue(game.isDraw)
    }

    @Test
    fun `game difficulty mapping honors levels and names`() {
        assertEquals(GameDifficulty.EASY, GameDifficulty.fromLevel(1))
        assertEquals(GameDifficulty.MEDIUM, GameDifficulty.fromLevel(2))
        assertEquals(GameDifficulty.HARD, GameDifficulty.fromLevel(3))
        assertEquals(GameDifficulty.MEDIUM, GameDifficulty.fromLevel(999))
        assertEquals(GameDifficulty.EASY, GameDifficulty.fromName("easy"))
        assertEquals(GameDifficulty.HARD, GameDifficulty.fromName("HARD"))
        assertEquals(GameDifficulty.MEDIUM, GameDifficulty.fromName("unknown"))
    }

    @Test
    fun `game result terminal states are accurate`() {
        assertFalse(GameResult.IN_PROGRESS.isTerminal())
        assertTrue(GameResult.WIN.isTerminal())
        assertTrue(GameResult.LOSS.isTerminal())
        assertTrue(GameResult.DRAW.isTerminal())
    }
}
