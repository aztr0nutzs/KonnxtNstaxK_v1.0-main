package com.neon.connectsort.ui.screens

import androidx.compose.ui.graphics.Color

/**
 * A generic UI state for all games.
 */
data class GameUiState(
    val tubes: List<List<Color>> = emptyList(),
    val level: Int = 1,
    val moves: Int = 0,
    val bestMoves: Int? = null,
    val isLevelComplete: Boolean = false
)
