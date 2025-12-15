package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.ui.screens.ShopItem
import com.neon.connectsort.ui.audio.AudioManager
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.game.common.PowerUp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShopViewModel(
    private val economy: EconomyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val audioManager: AudioManager
) : ViewModel() {

    private val _playerCoins = MutableStateFlow(0)
    val playerCoins: StateFlow<Int> = _playerCoins.asStateFlow()

    private val _shopItems = MutableStateFlow(defaultInventory())
    val shopItems: StateFlow<List<ShopItem>> = _shopItems.asStateFlow()

    init {
        viewModelScope.launch {
            economy.coinBalance.collect { _playerCoins.value = it }
        }

        viewModelScope.launch {
            economy.purchasedItems.collect { purchased ->
                _shopItems.update { items ->
                    items.map { it.copy(isPurchased = it.id in purchased) }
                }
            }
        }
    }

    fun purchaseItem(itemId: String) {
        val item = _shopItems.value.firstOrNull { it.id == itemId } ?: return
        if (item.isPurchased) return

        viewModelScope.launch {
            val success = economy.purchaseItem(itemId, item.price)
            if (!success) return@launch

            analyticsTracker.logEvent(
                "shop_purchase",
                mapOf("item" to itemId, "price" to item.price)
            )
            audioManager.playSample(AudioManager.Sample.COIN)

            when (itemId) {
                "power_surge" -> {
                    economy.restorePowerUp(PowerUp.SWAP, 2)
                    economy.restorePowerUp(PowerUp.PEEK, 2)
                }
            }
        }
    }

    private fun defaultInventory(): List<ShopItem> = listOf(
        ShopItem(
            id = "holo_trails",
            name = "Holo Trails",
            description = "Adds animated trails to falling chips",
            price = 750,
            effect = "+10% flair"
        ),
        ShopItem(
            id = "neon_frames",
            name = "Neon Frames",
            description = "Unlock premium cabinet frames",
            price = 1000,
            effect = "Cosmetic"
        ),
        ShopItem(
            id = "power_surge",
            name = "Power Surge Pack",
            description = "Restock SWAP and PEEK power-ups",
            price = 1250,
            effect = "+2 SWAP / +2 PEEK"
        )
    )
}
