package com.neon.connectsort.ui.screens.viewmodels

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.neon.connectsort.core.data.AppPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
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
class SettingsViewModelTest {

    private lateinit var repository: AppPreferencesRepository
    private lateinit var viewModel: SettingsViewModel
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
        viewModel = SettingsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleSound updates soundEnabled`() = runTest {
        val initialAudio = viewModel.settings.first().audio
        viewModel.toggleSound()
        val newAudio = viewModel.settings.first {
            it.audio.soundEnabled != initialAudio.soundEnabled
        }.audio
        assertEquals(!initialAudio.soundEnabled, newAudio.soundEnabled)
    }
}
