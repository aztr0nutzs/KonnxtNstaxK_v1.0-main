package com.neon.connectsort.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@ExperimentalCoroutinesApi
class AppPreferencesRepositoryTest {

    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun `test default preferences`() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher())
        val dataStore = PreferenceDataStoreFactory.create(
            scope = testScope.backgroundScope,
            produceFile = { temporaryFolder.newFile("test_prefs_defaults.preferences_pb") }
        )
        val repository = AppPreferencesRepository(dataStore)

        val prefs = repository.prefsFlow.first()
        assertEquals(UserPrefs(), prefs)
    }

    @Test
    fun `test set difficulty`() = runTest {
        val testScope = TestScope(UnconfinedTestDispatcher())
        val dataStore = PreferenceDataStoreFactory.create(
            scope = testScope.backgroundScope,
            produceFile = { temporaryFolder.newFile("test_prefs_difficulty.preferences_pb") }
        )
        val repository = AppPreferencesRepository(dataStore)

        repository.setDifficulty(3)
        val prefs = repository.prefsFlow.first()
        assertEquals(3, prefs.gameDifficulty)
    }
}
