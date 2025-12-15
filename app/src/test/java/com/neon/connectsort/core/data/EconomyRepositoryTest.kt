package com.neon.connectsort.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.neon.connectsort.core.data.DEFAULT_COIN_BALANCE
import com.neon.game.common.PowerUp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class EconomyRepositoryTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val scope = TestScope(dispatcher)
    private lateinit var dataStoreFile: File
    private lateinit var repository: EconomyRepository

    @Before
    fun setup() {
        dataStoreFile = File.createTempFile("prefs", ".pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = scope,
            produceFile = { dataStoreFile }
        )
        repository = EconomyRepository(dataStore)
    }

    @After
    fun teardown() {
        dataStoreFile.delete()
    }

    @Test
    fun purchaseItemConsumesCoins() = runTest {
        repository.setCoins(1000)
        assertTrue(repository.purchaseItem("test_item", 200))
        assertEquals(800, repository.coinBalance.first())
        assertTrue(repository.purchasedItems.first().contains("test_item"))
    }

    @Test
    fun powerUpInventoryReturnsDefaults() = runTest {
        val inventory = repository.powerUpInventoryFlow.first()
        assertEquals(3, inventory[PowerUp.SWAP])
        assertEquals(2, inventory[PowerUp.PEEK])
    }

    @Test
    fun resetProgressRestoresDefaults() = runTest {
        repository.setCoins(0)
        repository.purchaseItem("reset_me", 0)
        repository.resetProgress()
        assertEquals(DEFAULT_COIN_BALANCE, repository.coinBalance.first())
        assertTrue(repository.purchasedItems.first().isEmpty())
    }

    @Test
    fun earnAndSpendCoinsValidateBalance() = runTest {
        repository.setCoins(100)
        repository.earnCoins(50)
        assertEquals(150, repository.coinBalance.first())
        val spent = repository.spendCoins(120)
        assertTrue(spent)
        assertEquals(30, repository.coinBalance.first())
        val overspend = repository.spendCoins(1000)
        assertTrue(!overspend)
        assertEquals(30, repository.coinBalance.first())
    }
}
