package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.game.ballsort.BallSortGame
import com.neon.game.common.GameDifficulty
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineStart

class BallSortViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {
    private val game = BallSortGame()

    private val _bestMoves = MutableStateFlow<Int?>(null)
    private var hasRecordedLevelComplete = false

    private val _gameState = MutableStateFlow(BallSortGameState())
    val gameState: StateFlow<BallSortGameState> = _gameState.asStateFlow()

    private val _selectedTube = MutableStateFlow<Int?>(null)
    val selectedTube: StateFlow<Int?> = _selectedTube.asStateFlow()

    private val _animationState = MutableStateFlow<BallAnimationState?>(null)
    val animationState: StateFlow<BallAnimationState?> = _animationState.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _hintMove = MutableStateFlow<BallSortHint?>(null)
    val hintMove: StateFlow<BallSortHint?> = _hintMove.asStateFlow()

    data class UiState(
        val game: BallSortGameState = BallSortGameState(),
        val selectedTube: Int? = null,
        val animation: BallAnimationState? = null,
        val isPaused: Boolean = false,
        val hint: BallSortHint? = null
    )

    val uiState: StateFlow<UiState> = combine(
        gameState,
        selectedTube,
        animationState,
        isPaused,
        hintMove
    ) { game, selected, animation, paused, hint ->
        UiState(
            game = game,
            selectedTube = selected,
            animation = animation,
            isPaused = paused,
            hint = hint
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
        initialValue = UiState()
    )

    init {
        repository.prefsFlow.onEach { prefs ->
            _bestMoves.value = prefs.highScoreBallSort.takeIf { it > 0 }
            updateState()
        }.launchIn(viewModelScope)

        repository.getDifficultyFlow().onEach { difficulty ->
            if (game.getDifficulty() != difficulty) {
                game.reset(difficulty)
                game.startLevel(game.level)
                updateState()
            }
        }.launchIn(viewModelScope)

        updateState()
    }

    fun loadLevel(level: Int) {
        game.startLevel(level)
        _hintMove.value = null
        _selectedTube.value = null
        _isPaused.value = false
        updateState()
    }

    fun selectTube(index: Int) {
        if (_isPaused.value) return
        if (index !in game.tubes.indices) return
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
            try {
                for (progress in 0..100 step 10) {
                    _animationState.value = _animationState.value?.copy(progress = progress / 100f)
                    delay(16)
                }

                if (game.isValidMove(fromTube, toTube)) {
                    game.move(fromTube, toTube)
                    updateState()
                }
            } finally {
                _selectedTube.value = null
                _animationState.value = null
                _hintMove.value = null
            }
        }
    }

    fun resetLevel() {
        game.reset(game.getDifficulty())
        game.startLevel(game.level)
        updateState()
        _hintMove.value = null
        _selectedTube.value = null
        _isPaused.value = false
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    fun setPaused(paused: Boolean) {
        _isPaused.value = paused
    }

    fun requestHint() {
        if (_isPaused.value) return
        _hintMove.value = game.findHint()?.let { BallSortHint(it.first, it.second) }
    }

    fun undoLastMove() {
        if (_isPaused.value) return
        viewModelScope.launch {
            if (game.undo()) {
                updateState()
            }
            _selectedTube.value = null
            _hintMove.value = null
        }
    }

    fun setDifficulty(difficulty: GameDifficulty) {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            repository.setDifficulty(difficulty.level)
        }
    }

    private fun updateState() {
        _gameState.value = mapToGameState()
        if (game.isSolved()) {
            maybeSaveBestMoves()
        } else {
            hasRecordedLevelComplete = false
        }
    }

    private fun maybeSaveBestMoves() {
        if (hasRecordedLevelComplete) return
        hasRecordedLevelComplete = true
        viewModelScope.launch {
            repository.setHighScoreBallSort(game.moves)
        }
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
            bestMoves = _bestMoves.value,
            isLevelComplete = game.isSolved(),
            difficulty = game.getDifficulty()
        )
    }
}

data class BallSortGameState(
    val tubes: List<List<Color>> = emptyList(),
    val level: Int = 1,
    val moves: Int = 0,
    val bestMoves: Int? = null,
    val isLevelComplete: Boolean = false,
    val difficulty: GameDifficulty = GameDifficulty.MEDIUM
)

data class BallAnimationState(
    val fromTube: Int,
    val toTube: Int,
    val progress: Float
)

data class BallSortHint(
    val fromTube: Int,
    val toTube: Int
)
