package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.ChipRepository
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.core.data.GameTitle
import com.neon.connectsort.core.domain.ChipAbility
import com.neon.connectsort.core.domain.EffectType
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.connectsort.ui.audio.AudioManager
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BallSortViewModel(
    private val preferencesRepository: AppPreferencesRepository,
    private val economy: EconomyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val audioManager: AudioManager
) : ViewModel() {
    private val game = BallSortGame()
    private var timerJob: Job? = null
    private var hasRecordedLevelComplete = false
    private var equippedAbility: ChipAbility? = null

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

    private var latestInventory: Map<PowerUp, Int> = emptyMap()
    private var enabledPowerUpsCache: Set<PowerUp> = emptySet()
    private var latestBestMoves: Int = 0

    private val ballSortPalette = listOf(
        NeonColors.neonCyan,
        NeonColors.neonMagenta,
        NeonColors.neonGreen,
        NeonColors.neonYellow,
        NeonColors.neonRed,
        NeonColors.neonBlue,
        NeonColors.hologramPurple,
        NeonColors.hologramPink
    )

    init {
        viewModelScope.launch {
            combine(
                preferencesRepository.getAudioSettingsFlow(),
                preferencesRepository.getDifficultyFlow(),
                preferencesRepository.getGameModeFlow(),
                preferencesRepository.getPowerUpsFlow()
            ) { _, difficulty, mode, powerUps ->
                Triple(difficulty, mode, powerUps)
            }.collect { (difficulty, mode, powerUps) ->
                enabledPowerUpsCache = powerUps
                val currentGameState = _gameState.value
                if (game.getDifficulty() != difficulty || game.getMode() != mode) {
                    game.reset(difficulty, mode)
                    game.startLevel(game.level)
                    hasRecordedLevelComplete = false
                    _isPaused.value = false
                    _selectedTube.value = null
                    _hintMove.value = null
                    _gameState.value = currentGameState.copy(timer = 0)
                    startTimer()
                }
                updateState()
            }
        }

        viewModelScope.launch {
            economy.highScoreFlow(GameTitle.BALL_SORT).collect { best ->
                latestBestMoves = best
                _gameState.value = _gameState.value.copy(bestMoves = best.takeIf { it > 0 })
            }
        }

        viewModelScope.launch {
            economy.powerUpInventoryFlow.collect { inventory ->
                latestInventory = inventory
                _gameState.value = _gameState.value.copy(powerUpInventory = inventory)
            }
        }

        viewModelScope.launch {
            economy.equippedAbilityName.collect { abilityName ->
                equippedAbility = abilityName?.let { name ->
                    ChipRepository.chips.flatMap { it.abilities }.firstOrNull { it.name == name }
                }
                updateState()
            }
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
        if ((latestInventory[powerUp] ?: 0) <= 0) return
        viewModelScope.launch {
            val consumed = economy.consumePowerUp(powerUp)
            if (!consumed) return@launch
            analyticsTracker.logEvent("power_up_used", mapOf("powerUp" to powerUp.name))
            audioManager.playSample(AudioManager.Sample.POWER_UP)
            when (powerUp) {
                PowerUp.SWAP -> performSwap()
                PowerUp.PEEK -> revealPeek()
                PowerUp.SHUFFLE -> shuffleTubes()
                PowerUp.SOLVE -> solvePuzzle()
                else -> Unit
            }
            updateState()
        }
    }

    private suspend fun performSwap() {
        val sources = game.tubes.indices.filter { game.tubes[it].isNotEmpty() }
        if (sources.size < 2) return
        val from = sources.random()
        val to = sources.filter { it != from }.random()
        _animationState.value = BallAnimationState(from, to, 0f)
        for (progress in 0..100 step 10) {
            _animationState.value = _animationState.value?.copy(progress = progress / 100f)
            delay(16)
        }
        val sourceTube = game._tubes[from]
        val destTube = game._tubes[to]
        val topSource = sourceTube.removeAt(sourceTube.lastIndex)
        val topDest = destTube.removeAt(destTube.lastIndex)
        sourceTube.add(topDest)
        destTube.add(topSource)
        _animationState.value = null
    }

    private suspend fun revealPeek() {
        val hint = game.findHint()
        if (hint != null) {
            _hintMove.value = BallSortHint(hint.first, hint.second)
            delay(1200)
            _hintMove.value = null
        }
    }

    private fun shuffleTubes() {
        val allBalls = game._tubes.flatten().toMutableList()
        allBalls.shuffle()
        val capacity = game.tubes.maxOfOrNull { it.size }?.coerceAtLeast(1) ?: 1
        game._tubes.forEach { it.clear() }
        while (allBalls.isNotEmpty()) {
            for (tube in game._tubes) {
                if (tube.size < capacity && allBalls.isNotEmpty()) {
                    tube.add(allBalls.removeAt(0))
                }
            }
        }
    }

    private fun solvePuzzle() {
        val capacity = game.tubes.maxOfOrNull { it.size }?.coerceAtLeast(1) ?: 1
        val ballCounts = game._tubes.flatten().groupingBy { it }.eachCount()
        val solved = mutableListOf<MutableList<Int>>()
        ballCounts.keys.sorted().forEach { color ->
            solved.add(MutableList(capacity) { color })
        }
        repeat(game._tubes.size - solved.size) {
            solved.add(mutableListOf())
        }
        game._tubes = solved
    }

    fun loadLevel(level: Int) {
        game.startLevel(level)
        _hintMove.value = null
        _selectedTube.value = null
        _isPaused.value = false
        _gameState.value = _gameState.value.copy(timer = 0)
        hasRecordedLevelComplete = false
        startTimer()
        updateState()
    }

    fun selectTube(index: Int) {
        if (_isPaused.value) return
        when (val selected = _selectedTube.value) {
            null -> if (game.tubes[index].isNotEmpty()) {
                _selectedTube.value = index
            }
            index -> _selectedTube.value = null
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
        hasRecordedLevelComplete = false
        startTimer()
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    fun requestHint() {
        if (_isPaused.value) return
        viewModelScope.launch {
            val hint = game.findHint()
            _hintMove.value = hint?.let { BallSortHint(it.first, it.second) }
        }
    }

    fun requestGameMode(gameMode: GameMode) {
        viewModelScope.launch {
            preferencesRepository.setGameMode(gameMode)
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
            economy.setHighScore(GameTitle.BALL_SORT, game.moves)
            val reward = calculateReward()
            economy.adjustCoins(reward)
            analyticsTracker.logEvent(
                "ballsort_victory",
                mapOf("level" to game.level, "moves" to game.moves, "reward" to reward)
            )
            audioManager.playSample(AudioManager.Sample.VICTORY)
        }
    }

    private fun calculateReward(): Int {
        val baseReward = 100 + game.level * 10
        val effect = equippedAbility?.effect
        val multiplier = if (effect?.type == EffectType.POINT_MULTIPLIER) {
            effect.value
        } else {
            1.0
        }
        return (baseReward * multiplier).toInt().coerceAtLeast(baseReward)
    }

    private fun mapToGameState(): BallSortGameState {
        val palette = ballSortPalette
        val currentState = _gameState.value
        return BallSortGameState(
            tubes = game.tubes.map { tube ->
                tube.map { colorIndex ->
                    palette.getOrNull(colorIndex) ?: NeonColors.hologramGreen
                }
            },
            level = game.level,
            moves = game.moves,
            bestMoves = latestBestMoves.takeIf { it > 0 },
            isLevelComplete = game.isSolved(),
            difficulty = game.getDifficulty(),
            gameMode = game.getMode(),
            enabledPowerUps = enabledPowerUpsCache,
            timer = currentState.timer,
            scores = currentState.scores,
            currentPlayer = currentState.currentPlayer,
            abilityName = equippedAbility?.name,
            abilityDescription = equippedAbility?.description,
            abilityMultiplier = equippedAbility?.effect?.value,
            powerUpInventory = latestInventory
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
    val currentPlayer: Int = 0,
    val abilityName: String? = null,
    val abilityDescription: String? = null,
    val abilityMultiplier: Double? = null,
    val powerUpInventory: Map<PowerUp, Int> = emptyMap()
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
