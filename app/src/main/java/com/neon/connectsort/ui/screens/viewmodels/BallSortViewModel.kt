package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.game.ballsort.BallSortGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BallSortViewModel(
    private val repository: AppPreferencesRepository? = null
) : ViewModel() {
    private val game = BallSortGame()

    private val _gameState = MutableStateFlow(mapToGameState())
    val gameState: StateFlow<BallSortGameState> = _gameState.asStateFlow()

    private val _selectedTube = MutableStateFlow<Int?>(null)
    val selectedTube: StateFlow<Int?> = _selectedTube.asStateFlow()

    private val _animationState = MutableStateFlow<BallAnimationState?>(null)
    val animationState: StateFlow<BallAnimationState?> = _animationState.asStateFlow()

    fun loadLevel(level: Int) {
        game.startLevel(level)
        updateState()
    }

    fun selectTube(index: Int) {
        when (val selected = _selectedTube.value) {
            null -> {
                if (game.tubes[index].isNotEmpty()) {
                    _selectedTube.value = index
                }
            }
            index -> {
                _selectedTube.value = null
            }
            else -> {
                if (game.isValidMove(selected, index)) {
                    moveBall(selected, index)
                } else {
                    _selectedTube.value = null
                }
            }
        }
    }

    private fun moveBall(fromTube: Int, toTube: Int) {
        viewModelScope.launch {
            _animationState.value = BallAnimationState(fromTube, toTube, 0f)

            // Animation loop
            for (progress in 0..100 step 10) {
                _animationState.value = _animationState.value?.copy(progress = progress / 100f)
                delay(16)
            }

            game.move(fromTube, toTube)
            updateState()

            _selectedTube.value = null
            _animationState.value = null
        }
    }

    fun resetLevel() {
        game.reset()
        updateState()
    }

    private fun updateState() {
        _gameState.value = mapToGameState()
    }

    private fun mapToGameState(): BallSortGameState {
        val palette = listOf(
            NeonColors.neonCyan,
            NeonColors.neonMagenta,
            NeonColors.neonGreen,
            NeonColors.neonYellow,
            NeonColors.neonRed,
            NeonColors.neonBlue
        )
        return BallSortGameState(
            tubes = game.tubes.map { tube -> tube.map { palette[it % palette.size] } },
            level = game.level,
            moves = game.moves,
            bestMoves = getBestMoves(game.level),
            isLevelComplete = game.isSolved()
        )
    }

    private fun getBestMoves(level: Int): Int? {
        // TODO: Load from repository when best scores are implemented
        return null
    }
}

data class BallSortGameState(
    val tubes: List<List<Color>> = emptyList(),
    val level: Int = 1,
    val moves: Int = 0,
    val bestMoves: Int? = null,
    val isLevelComplete: Boolean = false
)

data class BallAnimationState(
    val fromTube: Int,
    val toTube: Int,
    val progress: Float
)
