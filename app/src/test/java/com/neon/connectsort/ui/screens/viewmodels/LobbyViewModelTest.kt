package com.neon.connectsort.ui.screens.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.game.common.GameDifficulty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class LobbyViewModelTest {

    private lateinit var repository: AppPreferencesRepository
    private lateinit var viewModel: LobbyViewModel
    private lateinit var dataStore: DataStore<Preferences>
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context: Context = RuntimeEnvironment.getApplication()
        dataStore = PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("test_prefs") }
        )
        repository = AppPreferencesRepository(dataStore)
        viewModel = LobbyViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state reflects repository values`() = runTest {
        repository.setHighScoreBallSort(10)
        repository.setHighScoreMultiplier(100)
        repository.setDifficulty(3)

        val state = viewModel.state.first {
            it.highScoreBallSort == 10
                && it.highScoreMultiplier == 100
                && it.gameDifficulty == GameDifficulty.HARD
        }

        assertEquals(10, state.highScoreBallSort)
        assertEquals(100, state.highScoreMultiplier)
        assertEquals(GameDifficulty.HARD, state.gameDifficulty)
    }
}
