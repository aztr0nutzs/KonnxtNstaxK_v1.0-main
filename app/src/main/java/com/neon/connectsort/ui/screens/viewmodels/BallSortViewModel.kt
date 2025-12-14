package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.ui.theme.NeonColors
import com.neon.game.ballsort.BallSortGame
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameMode
import com.neon.game.common.PowerUp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BallSortViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {
    private val game = BallSortGame()

    private var timerJob: Job? = null
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

    init {
        viewModelScope.launch {
            combine(
                repository.getAudioSettingsFlow(),
                repository.getDifficultyFlow(),
                repository.getGameModeFlow(),
                repository.getPowerUpsFlow(),
                repository.prefsFlow
            ) { audio, difficulty, gameMode, powerUps, prefs ->
                val currentGameState = _gameState.value
                _gameState.value = currentGameState.copy(
                    difficulty = difficulty,
                    gameMode = gameMode,
                    enabledPowerUps = powerUps,
                    bestMoves = prefs.highScoreBallSort.takeIf { it > 0 }
                )
                if (game.getDifficulty() != difficulty || game.getMode() != gameMode) {
                    game.reset(difficulty, gameMode)
                    game.startLevel(game.level)
                    updateState()
                }
            }.launchIn(viewModelScope)
        }
        updateState()
    }

    private fun startTimer() {
        stopTimer()
        val mode = _gameState.value.gameMode
        if (mode == GameMode.TIMED || mode == GameMode.COMPETITIVE) {
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    if (!_isPaused.value) {
                        val newTime = _gameState.value.timer + 1
                        _gameState.value = _gameState.value.copy(timer = newTime)
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun usePowerUp(powerUp: PowerUp) {
        if (_isPaused.value || powerUp !in _gameState.value.enabledPowerUps) return
        // TODO: Implement logic for each power-up
        when (powerUp) {
            PowerUp.SWAP -> { /* swap logic */ }
            PowerUp.PEEK -> { /* peek logic */ }
            PowerUp.SHUFFLE -> { /* shuffle logic */ }
            PowerUp.SOLVE -> { /* solve logic */ }
            else -> {}
        }
        updateState()
    }


    fun loadLevel(level: Int) {
        game.startLevel(level)
        _hintMove.value = null
        _selectedTube.value = null
        _isPaused.value = false
        _gameState.value = _gameState.value.copy(timer = 0)
        startTimer()
        updateState()
    }

    fun selectTube(index: Int) {
        if (_isPaused.value) return
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

            for (progress in 0..100 step 10) {
                _animationState.value = _animationState.value?.copy(progress = progress / 100f)
                delay(16)
            }

            game.move(fromTube, toTube)

            if (_gameState.value.gameMode == GameMode.COMPETITIVE) {
                val scores = _gameState.value.scores.toMutableList()
                val playerIndex = _gameState.value.currentPlayer
                scores[playerIndex] += 1
                _gameState.value = _gameState.value.copy(
                    scores = scores,
                    currentPlayer = (playerIndex + 1) % 2
                )
            }

            updateState()

            _selectedTube.value = null
            _animationState.value = null
            _hintMove.value = null
        }
    }

    fun resetLevel() {
        game.reset(game.getDifficulty(), game.getMode())
        game.startLevel(game.level)
        updateState()
        _hintMove.value = null
        _selectedTube.value = null
        _isPaused.value = false
        _gameState.value = _gameState.value.copy(timer = 0, scores = listOf(0, 0), currentPlayer = 0)
        startTimer()
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    fun requestHint() {
        if (_isPaused.value) return
        _hintMove.value = game.findHint()?.let { BallSortHint(it.first, it.second) }
    }

    fun requestGameMode(gameMode: GameMode) {
        viewModelScope.launch {
            repository.setGameMode(gameMode)
        }
    }

    private fun updateState() {
        _gameState.value = mapToGameState()
        if (game.isSolved()) {
            maybeSaveBestMoves()
            stopTimer()
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
        return _gameState.value.copy(
            tubes = game.tubes.map { tube -> tube.map { palette[it % palette.size] } },
            level = game.level,
            moves = game.moves,
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
    val difficulty: GameDifficulty = GameDifficulty.MEDIUM,
    val gameMode: GameMode = GameMode.CLASSIC,
    val enabledPowerUps: Set<PowerUp> = emptySet(),
    val timer: Int = 0,
    val scores: List<Int> = listOf(0, 0),
    val currentPlayer: Int = 0 // 0 for player 1, 1 for player 2
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
