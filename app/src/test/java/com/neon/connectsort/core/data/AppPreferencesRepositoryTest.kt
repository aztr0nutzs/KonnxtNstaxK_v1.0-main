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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class AppPreferencesRepositoryTest {

    private lateinit var repository: AppPreferencesRepository
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Before
    fun setup() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { temporaryFolder.newFile("test_prefs.preferences_pb") }
        )
        repository = AppPreferencesRepository(context)
    }

    @Test
    fun `test default preferences`() = testScope.runTest {
        val prefs = repository.prefsFlow.first()
        assertEquals(UserPrefs(), prefs)
    }

    @Test
    fun `test set difficulty`() = testScope.runTest {
        repository.setDifficulty(3)
        val prefs = repository.prefsFlow.first()
        assertEquals(3, prefs.gameDifficulty)
    }
}