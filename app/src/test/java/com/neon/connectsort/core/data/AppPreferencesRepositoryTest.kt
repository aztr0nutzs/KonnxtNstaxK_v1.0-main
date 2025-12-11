package com.neon.connectsort.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class AppPreferencesRepositoryTest {

    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder()

    private fun testDataStore(testFile: File) = PreferenceDataStoreFactory.create(
        produceFile = { testFile }
    )

    @Test
    fun `test default preferences`() = runTest {
        val repository = AppPreferencesRepository(testDataStore(temporaryFolder.newFile("test_prefs_defaults.preferences_pb")))

        val prefs = repository.prefsFlow.first()
        assertEquals(UserPrefs(), prefs)
    }

    @Test
    fun `test set difficulty`() = runTest {
        val repository = AppPreferencesRepository(testDataStore(temporaryFolder.newFile("test_prefs_difficulty.preferences_pb")))

        repository.setDifficulty(3)
        val prefs = repository.prefsFlow.first()
        assertEquals(3, prefs.gameDifficulty)
    }
}
