package com.neon.connectsort.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.neon.game.common.GameDifficulty
import com.neon.game.common.GameMode
import com.neon.game.common.PowerUp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class UserPrefs(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val volume: Float = 0.8f,
    val animationsEnabled: Boolean = true,
    val glowEffectsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showTutorials: Boolean = true,
    val gameDifficulty: Int = 2,
    val gameMode: String = GameMode.CLASSIC.name,
    val powerUps: Set<String> = setOf(PowerUp.BOMB.name, PowerUp.SHIELD.name, PowerUp.SWAP.name),
    val analyticsEnabled: Boolean = true
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
        val DIFFICULTY = intPreferencesKey("game_difficulty")
        val GAME_MODE = stringPreferencesKey("game_mode")
        val POWER_UPS = stringSetPreferencesKey("power_ups")
        val ANALYTICS = booleanPreferencesKey("analytics")
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
            gameDifficulty = prefs[Keys.DIFFICULTY] ?: 2,
            gameMode = prefs[Keys.GAME_MODE] ?: GameMode.CLASSIC.name,
            powerUps = prefs[Keys.POWER_UPS] ?: setOf(PowerUp.BOMB.name, PowerUp.SHIELD.name, PowerUp.SWAP.name),
            analyticsEnabled = prefs[Keys.ANALYTICS] ?: true
        )
    }

    fun getAudioSettingsFlow(): Flow<AudioSettings> = dataStore.data.map { prefs ->
        AudioSettings(
            soundEnabled = prefs[Keys.SOUND] ?: true,
            musicEnabled = prefs[Keys.MUSIC] ?: true,
            volume = prefs[Keys.VOLUME] ?: 0.8f
        )
    }

    fun getDifficultyFlow(): Flow<GameDifficulty> = dataStore.data.map {
        GameDifficulty.fromLevel(it[Keys.DIFFICULTY] ?: 2)
    }

    fun getGameModeFlow(): Flow<GameMode> = dataStore.data.map {
        GameMode.valueOf(it[Keys.GAME_MODE] ?: GameMode.CLASSIC.name)
    }

    fun getPowerUpsFlow(): Flow<Set<PowerUp>> = dataStore.data.map { prefs ->
        (prefs[Keys.POWER_UPS] ?: setOf(PowerUp.BOMB.name, PowerUp.SHIELD.name, PowerUp.SWAP.name))
            .map { PowerUp.valueOf(it) }
            .toSet()
    }

    fun analyticsEnabledFlow(): Flow<Boolean> = dataStore.data.map {
        it[Keys.ANALYTICS] ?: true
    }

    suspend fun setSound(enabled: Boolean) = write { it[Keys.SOUND] = enabled }
    suspend fun setMusic(enabled: Boolean) = write { it[Keys.MUSIC] = enabled }
    suspend fun setVolume(value: Float) = write { it[Keys.VOLUME] = value.coerceIn(0f, 1f) }
    suspend fun setAnimations(enabled: Boolean) = write { it[Keys.ANIMATIONS] = enabled }
    suspend fun setGlow(enabled: Boolean) = write { it[Keys.GLOW] = enabled }
    suspend fun setVibration(enabled: Boolean) = write { it[Keys.VIBRATION] = enabled }
    suspend fun setTutorials(enabled: Boolean) = write { it[Keys.TUTORIALS] = enabled }
    suspend fun setDifficulty(difficulty: Int) = write { it[Keys.DIFFICULTY] = difficulty.coerceIn(1, 3) }
    suspend fun setGameMode(gameMode: GameMode) = write { it[Keys.GAME_MODE] = gameMode.name }
    suspend fun setPowerUps(powerUps: Set<PowerUp>) = write { prefs ->
        prefs[Keys.POWER_UPS] = powerUps.map { it.name }.toSet()
    }
    suspend fun setAudioSettings(settings: AudioSettings) = write { prefs ->
        prefs[Keys.SOUND] = settings.soundEnabled
        prefs[Keys.MUSIC] = settings.musicEnabled
        prefs[Keys.VOLUME] = settings.volume.coerceIn(0f, 1f)
    }
    suspend fun setAnalytics(enabled: Boolean) = write { it[Keys.ANALYTICS] = enabled }

    private suspend fun write(block: (MutablePreferences) -> Unit) {
        dataStore.edit { prefs -> block(prefs) }
    }
}
