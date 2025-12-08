package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.AppContextHolder
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.game.ballsort.BallSortGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BallSortViewModel(
    private val repository: AppPreferencesRepository = AppPreferencesRepository(AppContextHolder.appContext)
) : ViewModel() {
    private val game = BallSortGame()

    private val palette: List<Color> = listOf(
        NeonColors.neonCyan,
        NeonColors.neonMagenta,
        NeonColors.neonGreen,
        NeonColors.neonYellow,
        NeonColors.neonRed,
        NeonColors.neonBlue
    )

    private var tubesIds: List<MutableList<Int>> = emptyList()
    private var moveHistoryIds = mutableListOf<List<MutableList<Int>>>()

    private val _gameState = MutableStateFlow(BallSortGameState())
    val gameState: StateFlow<BallSortGameState> = _gameState.asStateFlow()
    
    private val _selectedTube = MutableStateFlow<Int?>(null)
    val selectedTube: StateFlow<Int?> = _selectedTube.asStateFlow()
    
    private val _animationState = MutableStateFlow<BallAnimationState?>(null)
    val animationState: StateFlow<BallAnimationState?> = _animationState.asStateFlow()
    
    fun loadLevel(level: Int) {
        tubesIds = game.generateLevel(level)
        moveHistoryIds.clear()
        moveHistoryIds.add(deepCopy(tubesIds))

        _gameState.value = BallSortGameState(
            tubes = mapToColors(tubesIds),
            level = level,
            moves = 0,
            bestMoves = getBestMoves(level),
            isLevelComplete = game.isSolved(tubesIds)
        )
        
        _selectedTube.value = null
        _animationState.value = null
    }
    
    fun selectTube(index: Int) {
        val currentState = _gameState.value
        
        when (_selectedTube.value) {
            null -> {
                // Select tube if it has balls
                if (currentState.tubes[index].isNotEmpty()) {
                    _selectedTube.value = index
                }
            }
            index -> {
                // Deselect if same tube clicked
                _selectedTube.value = null
            }
            else -> {
                // Try to move ball
                val fromTube = _selectedTube.value!!
                val toTube = index
                
                if (isValidMove(tubesIds, fromTube, toTube)) {
                    moveBall(fromTube, toTube)
                } else {
                    _selectedTube.value = null
                }
            }
        }
    }
    
    private fun moveBall(fromTube: Int, toTube: Int) {
        viewModelScope.launch {
            // Animate move
            _animationState.value = BallAnimationState(
                fromTube = fromTube,
                toTube = toTube,
                progress = 0f
            )
            
            // Animate
            for (progress in 0..100 step 5) {
                _animationState.value = _animationState.value?.copy(progress = progress / 100f)
                delay(16)
            }
            
            // Update state via domain model
            tubesIds = game.move(tubesIds, fromTube, toTube)

            val currentState = _gameState.value
            val newState = currentState.copy(
                tubes = mapToColors(tubesIds),
                moves = currentState.moves + 1,
                isLevelComplete = game.isSolved(tubesIds)
            )

            _gameState.value = newState
            _selectedTube.value = null
            _animationState.value = null
            
            // Save to history
            moveHistoryIds.add(deepCopy(tubesIds))
        }
    }
    
    private fun isValidMove(tubes: List<MutableList<Int>>, fromTube: Int, toTube: Int): Boolean =
        game.isValidMove(tubes, fromTube, toTube)
    
    fun undoMove() {
        if (moveHistoryIds.size > 1) {
            moveHistoryIds.removeLast()
            tubesIds = deepCopy(moveHistoryIds.last())
            _gameState.value = _gameState.value.copy(
                tubes = mapToColors(tubesIds),
                moves = moveHistoryIds.size - 1,
                isLevelComplete = game.isSolved(tubesIds)
            )
            _selectedTube.value = null
            _animationState.value = null
        }
    }
    
    fun resetLevel() {
        loadLevel(_gameState.value.level)
    }
    
    fun nextLevel() {
        val currentLevel = _gameState.value.level
        loadLevel(currentLevel + 1)
    }
    
    private fun getBestMoves(level: Int): Int? {
        // TODO: Load from repository when best scores are implemented
        return null
    }
    
    private fun mapToColors(tubes: List<MutableList<Int>>): List<List<Color>> =
        tubes.map { tube -> tube.map { palette[it % palette.size] } }

    private fun deepCopy(source: List<MutableList<Int>>): MutableList<MutableList<Int>> =
        source.map { it.toMutableList() }.toMutableList()
}

data class BallSortGameState(
    val tubes: List<List<Color>> = emptyList(),
    val level: Int = 1,
    val moves: Int = 0,
    val bestMoves: Int? = null,
    val isLevelComplete: Boolean = false
) {
    fun copy(): BallSortGameState {
        return BallSortGameState(
            tubes = tubes.map { it.toList() },
            level = level,
            moves = moves,
            bestMoves = bestMoves,
            isLevelComplete = isLevelComplete
        )
    }
}

data class BallAnimationState(
    val fromTube: Int,
    val toTube: Int,
    val progress: Float
)
