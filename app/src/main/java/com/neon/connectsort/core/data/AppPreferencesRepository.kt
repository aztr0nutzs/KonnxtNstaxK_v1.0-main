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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFS_NAME = "user_prefs"
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(PREFS_NAME)

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
    val unlockedCharacterIds: Set<String> = setOf("nexus_prime"),
    val gameDifficulty: Int = 2, // 1=Easy, 2=Medium, 3=Hard
    val highScoreBallSort: Int = 0,
    val highScoreMultiplier: Int = 0,
    val highScoreConnectFour: Int = 0
)

class AppPreferencesRepository(private val context: Context) {

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
    }

    val prefsFlow: Flow<UserPrefs> = context.userPreferencesDataStore.data.map { prefs ->
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
            unlockedCharacterIds = prefs[Keys.UNLOCKED] ?: setOf("nexus_prime"),
            gameDifficulty = prefs[Keys.DIFFICULTY] ?: 2,
            highScoreBallSort = prefs[Keys.HIGH_SCORE_BALL_SORT] ?: 0,
            highScoreMultiplier = prefs[Keys.HIGH_SCORE_MULTIPLIER] ?: 0,
            highScoreConnectFour = prefs[Keys.HIGH_SCORE_CONNECT_FOUR] ?: 0
        )
    }

    val difficultyFlow: Flow<Int> = context.userPreferencesDataStore.data.map {
        it[Keys.DIFFICULTY] ?: 2
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

    suspend fun unlockCharacter(id: String) = write { prefs ->
        val current = prefs[Keys.UNLOCKED] ?: setOf("nexus_prime")
        prefs[Keys.UNLOCKED] = current + id
    }
    
    suspend fun setDifficulty(difficulty: Int) = write { prefs ->
        prefs[Keys.DIFFICULTY] = difficulty.coerceIn(1, 3)
    }
    
    suspend fun setHighScoreBallSort(score: Int) = write { prefs ->
        val current = prefs[Keys.HIGH_SCORE_BALL_SORT] ?: 0
        if (score > current) {
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
        context.userPreferencesDataStore.edit { prefs -> block(prefs) }
    }
}
