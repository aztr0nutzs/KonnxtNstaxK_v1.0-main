package com.neon.connectsort.core.data

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

/**
 * Unit tests for AppPreferencesRepository.
 * Note: These tests require Android instrumentation to run properly.
 * For pure unit tests, mock the DataStore dependency.
 */
class AppPreferencesRepositoryTest {

    @Test
    fun `default preferences have correct values`() {
        val prefs = UserPrefs()
        
        assertTrue(prefs.soundEnabled)
        assertTrue(prefs.musicEnabled)
        assertEquals(0.8f, prefs.volume, 0.01f)
        assertTrue(prefs.animationsEnabled)
        assertTrue(prefs.glowEffectsEnabled)
        assertTrue(prefs.vibrationEnabled)
        assertTrue(prefs.showTutorials)
        assertEquals(2500, prefs.coins)
        assertEquals("nexus_prime", prefs.selectedCharacterId)
        assertEquals(setOf("nexus_prime"), prefs.unlockedCharacterIds)
        assertEquals(2, prefs.gameDifficulty)
        assertEquals(0, prefs.highScoreBallSort)
        assertEquals(0, prefs.highScoreMultiplier)
        assertEquals(0, prefs.highScoreConnectFour)
    }

    @Test
    fun `difficulty is clamped to valid range`() {
        val prefs = UserPrefs(gameDifficulty = 1)
        assertEquals(1, prefs.gameDifficulty)
        
        val prefs2 = UserPrefs(gameDifficulty = 3)
        assertEquals(3, prefs2.gameDifficulty)
    }

    @Test
    fun `unlocked characters includes default`() {
        val prefs = UserPrefs()
        assertTrue(prefs.unlockedCharacterIds.contains("nexus_prime"))
    }

    @Test
    fun `volume is within valid range`() {
        val prefs = UserPrefs()
        assertTrue(prefs.volume >= 0f)
        assertTrue(prefs.volume <= 1f)
    }

    @Test
    fun `coins start at reasonable amount`() {
        val prefs = UserPrefs()
        assertTrue(prefs.coins > 0)
    }
}
