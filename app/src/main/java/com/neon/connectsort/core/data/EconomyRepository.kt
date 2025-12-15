package com.neon.connectsort.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.neon.game.common.PowerUp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class StoryProgress(
    val completedChapters: Set<String>,
    val unlockedChapters: Set<String>,
    val activeChapter: String?
)

enum class GameTitle {
    BALL_SORT,
    MULTIPLIER,
    CONNECT_FOUR
}

class EconomyRepository(private val dataStore: DataStore<Preferences>) {

    private object Keys {
        val COINS = intPreferencesKey("coins")
        val PURCHASED_ITEMS = stringSetPreferencesKey("purchased_items")
        val UNLOCKED_CHIPS = stringSetPreferencesKey("unlocked_chips")
        val EQUIPPED_CHIP = stringPreferencesKey("equipped_chip")
        val EQUIPPED_ABILITY = stringPreferencesKey("equipped_ability")
        val STORY_COMPLETED = stringSetPreferencesKey("story_completed")
        val STORY_UNLOCKED = stringSetPreferencesKey("story_unlocked")
        val STORY_ACTIVE = stringPreferencesKey("story_active")
        val HIGH_SCORE_BALL_SORT = intPreferencesKey("high_score_ball_sort")
        val HIGH_SCORE_MULTIPLIER = intPreferencesKey("high_score_multiplier")
        val HIGH_SCORE_CONNECT_FOUR = intPreferencesKey("high_score_connect_four")
        val CACHE_TOKEN = stringPreferencesKey("cache_token")
        val LEVEL_CACHE = stringPreferencesKey("level_cache")
        val POWER_SWAP = intPreferencesKey("power_swap")
        val POWER_PEEK = intPreferencesKey("power_peek")
        val POWER_SHUFFLE = intPreferencesKey("power_shuffle")
        val POWER_SOLVE = intPreferencesKey("power_solve")
    }

    private val defaultPowerUpInventory = mapOf(
        PowerUp.SWAP to 3,
        PowerUp.PEEK to 2,
        PowerUp.SHUFFLE to 2,
        PowerUp.SOLVE to 1
    )

    private val powerUpKeys: Map<PowerUp, Preferences.Key<Int>> = mapOf(
        PowerUp.SWAP to Keys.POWER_SWAP,
        PowerUp.PEEK to Keys.POWER_PEEK,
        PowerUp.SHUFFLE to Keys.POWER_SHUFFLE,
        PowerUp.SOLVE to Keys.POWER_SOLVE
    )

    val coinBalance: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.COINS] ?: DEFAULT_COIN_BALANCE
    }
    val coinBalanceFlow: Flow<Int> = coinBalance

    val purchasedItems: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.PURCHASED_ITEMS] ?: emptySet()
    }

    val unlockedChips: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.UNLOCKED_CHIPS] ?: DEFAULT_UNLOCKED_CHARACTERS
    }

    val selectedChipId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.EQUIPPED_CHIP] ?: DEFAULT_UNLOCKED_CHARACTERS.firstOrNull()
    }

    val equippedAbilityName: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.EQUIPPED_ABILITY]
    }

    val storyProgressFlow: Flow<StoryProgress> = dataStore.data.map { prefs ->
        StoryProgress(
            completedChapters = prefs[Keys.STORY_COMPLETED] ?: emptySet(),
            unlockedChapters = prefs[Keys.STORY_UNLOCKED] ?: DEFAULT_STORY_UNLOCKED,
            activeChapter = prefs[Keys.STORY_ACTIVE] ?: DEFAULT_STORY_ACTIVE
        )
    }

    val powerUpInventoryFlow: Flow<Map<PowerUp, Int>> = dataStore.data.map { prefs ->
        defaultPowerUpInventory.mapValues { (powerUp, default) ->
            powerUpKeys[powerUp]?.let { prefs[it] } ?: default
        }
    }

    fun highScoreFlow(title: GameTitle): Flow<Int> = dataStore.data.map { prefs ->
        when (title) {
            GameTitle.BALL_SORT -> prefs[Keys.HIGH_SCORE_BALL_SORT] ?: 0
            GameTitle.MULTIPLIER -> prefs[Keys.HIGH_SCORE_MULTIPLIER] ?: 0
            GameTitle.CONNECT_FOUR -> prefs[Keys.HIGH_SCORE_CONNECT_FOUR] ?: 0
        }
    }

    suspend fun adjustCoins(delta: Int) {
        if (delta == 0) return
        dataStore.edit { prefs ->
            val current = prefs[Keys.COINS] ?: DEFAULT_COIN_BALANCE
            prefs[Keys.COINS] = (current + delta).coerceAtLeast(0)
        }
    }

    suspend fun earnCoins(amount: Int) {
        if (amount <= 0) return
        adjustCoins(amount)
    }

    suspend fun spendCoins(amount: Int): Boolean {
        if (amount <= 0) return true
        var success = false
        dataStore.edit { prefs ->
            val current = prefs[Keys.COINS] ?: DEFAULT_COIN_BALANCE
            if (current >= amount) {
                prefs[Keys.COINS] = current - amount
                success = true
            }
        }
        return success
    }

    suspend fun setCoins(amount: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.COINS] = amount.coerceAtLeast(0)
        }
    }

    suspend fun purchaseItem(itemId: String, price: Int): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val purchased = prefs[Keys.PURCHASED_ITEMS] ?: emptySet()
            val coins = prefs[Keys.COINS] ?: DEFAULT_COIN_BALANCE
            if (itemId in purchased || coins < price) {
                return@edit
            }
            prefs[Keys.PURCHASED_ITEMS] = purchased + itemId
            prefs[Keys.COINS] = coins - price
            success = true
        }
        return success
    }

    suspend fun unlockChip(chipId: String) {
        dataStore.edit { prefs ->
            val unlocked = prefs[Keys.UNLOCKED_CHIPS] ?: DEFAULT_UNLOCKED_CHARACTERS
            prefs[Keys.UNLOCKED_CHIPS] = unlocked + chipId
        }
    }

    suspend fun selectChip(chipId: String) {
        dataStore.edit { prefs ->
            prefs[Keys.EQUIPPED_CHIP] = chipId
        }
    }

    suspend fun selectAbility(abilityName: String) {
        dataStore.edit { prefs ->
            prefs[Keys.EQUIPPED_ABILITY] = abilityName
        }
    }

    suspend fun markChapterCompleted(chapterId: String) {
        dataStore.edit { prefs ->
            val completed = prefs[Keys.STORY_COMPLETED] ?: emptySet()
            prefs[Keys.STORY_COMPLETED] = completed + chapterId
        }
    }

    suspend fun unlockStoryChapter(chapterId: String) {
        dataStore.edit { prefs ->
            val unlocked = prefs[Keys.STORY_UNLOCKED] ?: DEFAULT_STORY_UNLOCKED
            prefs[Keys.STORY_UNLOCKED] = unlocked + chapterId
        }
    }

    suspend fun setActiveStoryChapter(chapterId: String?) {
        dataStore.edit { prefs ->
            if (chapterId == null) {
                prefs.remove(Keys.STORY_ACTIVE)
            } else {
                prefs[Keys.STORY_ACTIVE] = chapterId
            }
        }
    }

    suspend fun resetPowerUpInventory() {
        dataStore.edit { prefs ->
            defaultPowerUpInventory.forEach { (powerUp, default) ->
                powerUpKeys[powerUp]?.let { prefs[it] = default }
            }
        }
    }

    suspend fun consumePowerUp(powerUp: PowerUp): Boolean {
        val key = powerUpKeys[powerUp] ?: return false
        var success = false
        dataStore.edit { prefs ->
            val current = prefs[key] ?: defaultPowerUpInventory[powerUp] ?: 0
            if (current > 0) {
                prefs[key] = current - 1
                success = true
            }
        }
        return success
    }

    suspend fun restorePowerUp(powerUp: PowerUp, amount: Int) {
        val key = powerUpKeys[powerUp] ?: return
        if (amount <= 0) return
        dataStore.edit { prefs ->
            val current = prefs[key] ?: defaultPowerUpInventory[powerUp] ?: 0
            prefs[key] = current + amount
        }
    }

    suspend fun setHighScore(title: GameTitle, score: Int) {
        dataStore.edit { prefs ->
            when (title) {
                GameTitle.BALL_SORT -> {
                    val current = prefs[Keys.HIGH_SCORE_BALL_SORT] ?: 0
                    if (current == 0 || score < current) {
                        prefs[Keys.HIGH_SCORE_BALL_SORT] = score
                    }
                }
                GameTitle.MULTIPLIER -> {
                    val current = prefs[Keys.HIGH_SCORE_MULTIPLIER] ?: 0
                    if (score > current) {
                        prefs[Keys.HIGH_SCORE_MULTIPLIER] = score
                    }
                }
                GameTitle.CONNECT_FOUR -> {
                    val current = prefs[Keys.HIGH_SCORE_CONNECT_FOUR] ?: 0
                    if (score > current) {
                        prefs[Keys.HIGH_SCORE_CONNECT_FOUR] = score
                    }
                }
            }
        }
    }

    suspend fun clearCache() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.CACHE_TOKEN)
            prefs.remove(Keys.LEVEL_CACHE)
        }
    }

    suspend fun resetProgress() {
        dataStore.edit { prefs ->
            prefs[Keys.COINS] = DEFAULT_COIN_BALANCE
            prefs[Keys.PURCHASED_ITEMS] = emptySet()
            prefs[Keys.UNLOCKED_CHIPS] = DEFAULT_UNLOCKED_CHARACTERS
            DEFAULT_UNLOCKED_CHARACTERS.firstOrNull()?.let { prefs[Keys.EQUIPPED_CHIP] = it } ?: prefs.remove(
                Keys.EQUIPPED_CHIP
            )
            prefs.remove(Keys.EQUIPPED_ABILITY)
            prefs[Keys.STORY_COMPLETED] = emptySet()
            prefs[Keys.STORY_UNLOCKED] = DEFAULT_STORY_UNLOCKED
            prefs[Keys.STORY_ACTIVE] = DEFAULT_STORY_ACTIVE
            prefs[Keys.HIGH_SCORE_BALL_SORT] = 0
            prefs[Keys.HIGH_SCORE_MULTIPLIER] = 0
            prefs[Keys.HIGH_SCORE_CONNECT_FOUR] = 0
            defaultPowerUpInventory.forEach { (powerUp, default) ->
                powerUpKeys[powerUp]?.let { prefs[it] = default }
            }
            prefs.remove(Keys.CACHE_TOKEN)
            prefs.remove(Keys.LEVEL_CACHE)
        }
    }
}
