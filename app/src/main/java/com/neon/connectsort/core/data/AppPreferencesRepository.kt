package com.neon.connectsort.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameMode
import com.neon.game.common.PowerUp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFS_NAME = "user_prefs"
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(PREFS_NAME)

private val DEFAULT_UNLOCKED_CHARACTERS = setOf("nexus_prime")

data class UserPrefs(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val volume: Float = 0.8f,
    val animationsEnabled: Boolean = true,
    val glowEffectsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showTutorials: Boolean = true,
    val coins: Int = 2500,
    val selectedCharacterId: String = "nexus_prime",
    val unlockedCharacterIds: Set<String> = DEFAULT_UNLOCKED_CHARACTERS,
    val gameDifficulty: Int = 2, // 1=Easy, 2=Medium, 3=Hard
    val highScoreBallSort: Int = 0,
    val highScoreMultiplier: Int = 0,
    val highScoreConnectFour: Int = 0,
    val gameMode: String = GameMode.CLASSIC.name,
    val powerUps: Set<String> = setOf(PowerUp.BOMB.name, PowerUp.SHIELD.name, PowerUp.SWAP.name)
)

data class AudioSettings(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val volume: Float = 0.8f
)

class AppPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val SOUND = booleanPreferencesKey("sound")
        val MUSIC = booleanPreferencesKey("music")
        val VOLUME = floatPreferencesKey("volume")
        val ANIMATIONS = booleanPreferencesKey("animations")
        val GLOW = booleanPreferencesKey("glow")
        val VIBRATION = booleanPreferencesKey("vibration")
        val TUTORIALS = booleanPreferencesKey("tutorials")
        val COINS = intPreferencesKey("coins")
        val SELECTED_CHAR = stringPreferencesKey("selected_char")
        val UNLOCKED = stringSetPreferencesKey("unlocked_chars")
        val DIFFICULTY = intPreferencesKey("game_difficulty")
        val HIGH_SCORE_BALL_SORT = intPreferencesKey("high_score_ball_sort")
        val HIGH_SCORE_MULTIPLIER = intPreferencesKey("high_score_multiplier")
        val HIGH_SCORE_CONNECT_FOUR = intPreferencesKey("high_score_connect_four")
        val GAME_MODE = stringPreferencesKey("game_mode")
        val POWER_UPS = stringSetPreferencesKey("power_ups")
    }

    val prefsFlow: Flow<UserPrefs> = dataStore.data.map { prefs ->
        UserPrefs(
            soundEnabled = prefs[Keys.SOUND] ?: true,
            musicEnabled = prefs[Keys.MUSIC] ?: true,
            volume = prefs[Keys.VOLUME] ?: 0.8f,
            animationsEnabled = prefs[Keys.ANIMATIONS] ?: true,
            glowEffectsEnabled = prefs[Keys.GLOW] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION] ?: true,
            showTutorials = prefs[Keys.TUTORIALS] ?: true,
            coins = prefs[Keys.COINS] ?: 2500,
            selectedCharacterId = prefs[Keys.SELECTED_CHAR] ?: "nexus_prime",
            unlockedCharacterIds = prefs[Keys.UNLOCKED] ?: DEFAULT_UNLOCKED_CHARACTERS,
            gameDifficulty = prefs[Keys.DIFFICULTY] ?: 2,
            highScoreBallSort = prefs[Keys.HIGH_SCORE_BALL_SORT] ?: 0,
            highScoreMultiplier = prefs[Keys.HIGH_SCORE_MULTIPLIER] ?: 0,
            highScoreConnectFour = prefs[Keys.HIGH_SCORE_CONNECT_FOUR] ?: 0,
            gameMode = prefs[Keys.GAME_MODE] ?: GameMode.CLASSIC.name,
            powerUps = prefs[Keys.POWER_UPS] ?: setOf(PowerUp.BOMB.name, PowerUp.SHIELD.name, PowerUp.SWAP.name)
        )
    }

    fun getGameModeFlow(): Flow<GameMode> = dataStore.data.map {
        GameMode.valueOf(it[Keys.GAME_MODE] ?: GameMode.CLASSIC.name)
    }

    fun getPowerUpsFlow(): Flow<Set<PowerUp>> = dataStore.data.map { prefs ->
        (prefs[Keys.POWER_UPS] ?: setOf(PowerUp.BOMB.name, PowerUp.SHIELD.name, PowerUp.SWAP.name)).map { PowerUp.valueOf(it) }.toSet()
    }

    fun getDifficultyFlow(): Flow<GameDifficulty> = dataStore.data.map {
        GameDifficulty.fromLevel(it[Keys.DIFFICULTY] ?: 2)
    }

    fun getUnlockedChipsFlow(): Flow<Set<String>> = dataStore.data.map {
        it[Keys.UNLOCKED] ?: DEFAULT_UNLOCKED_CHARACTERS
    }

    fun getAudioSettingsFlow(): Flow<AudioSettings> = dataStore.data.map { prefs ->
        AudioSettings(
            soundEnabled = prefs[Keys.SOUND] ?: true,
            musicEnabled = prefs[Keys.MUSIC] ?: true,
            volume = prefs[Keys.VOLUME] ?: 0.8f
        )
    }

    suspend fun setSound(enabled: Boolean) = write { it[Keys.SOUND] = enabled }
    suspend fun setMusic(enabled: Boolean) = write { it[Keys.MUSIC] = enabled }
    suspend fun setVolume(value: Float) = write { it[Keys.VOLUME] = value }
    suspend fun setAnimations(enabled: Boolean) = write { it[Keys.ANIMATIONS] = enabled }
    suspend fun setGlow(enabled: Boolean) = write { it[Keys.GLOW] = enabled }
    suspend fun setVibration(enabled: Boolean) = write { it[Keys.VIBRATION] = enabled }
    suspend fun setTutorials(enabled: Boolean) = write { it[Keys.TUTORIALS] = enabled }

    suspend fun setCoins(coins: Int) = write { it[Keys.COINS] = coins }

    suspend fun setSelectedCharacter(id: String) = write { it[Keys.SELECTED_CHAR] = id }

    suspend fun unlockChip(id: String) = write { prefs ->
        val current = prefs[Keys.UNLOCKED] ?: DEFAULT_UNLOCKED_CHARACTERS
        prefs[Keys.UNLOCKED] = current + id
    }

    suspend fun setAudioSettings(settings: AudioSettings) = write { prefs ->
        prefs[Keys.SOUND] = settings.soundEnabled
        prefs[Keys.MUSIC] = settings.musicEnabled
        prefs[Keys.VOLUME] = settings.volume.coerceIn(0f, 1f)
    }

    suspend fun setDifficulty(difficulty: Int) = write { prefs ->
        prefs[Keys.DIFFICULTY] = difficulty.coerceIn(1, 3)
    }

    suspend fun setGameMode(gameMode: GameMode) = write { prefs ->
        prefs[Keys.GAME_MODE] = gameMode.name
    }

    suspend fun setPowerUps(powerUps: Set<PowerUp>) = write { prefs ->
        prefs[Keys.POWER_UPS] = powerUps.map { it.name }.toSet()
    }

    suspend fun setHighScoreBallSort(score: Int) = write { prefs ->
        val current = prefs[Keys.HIGH_SCORE_BALL_SORT] ?: 0
        if (current == 0 || score < current) {
            prefs[Keys.HIGH_SCORE_BALL_SORT] = score
        }
    }

    suspend fun setHighScoreMultiplier(score: Int) = write { prefs ->
        val current = prefs[Keys.HIGH_SCORE_MULTIPLIER] ?: 0
        if (score > current) {
            prefs[Keys.HIGH_SCORE_MULTIPLIER] = score
        }
    }

    suspend fun setHighScoreConnectFour(score: Int) = write { prefs ->
        val current = prefs[Keys.HIGH_SCORE_CONNECT_FOUR] ?: 0
        if (score > current) {
            prefs[Keys.HIGH_SCORE_CONNECT_FOUR] = score
        }
    }

    private suspend fun write(block: (MutablePreferences) -> Unit) {
        dataStore.edit { prefs -> block(prefs) }
    }
}
