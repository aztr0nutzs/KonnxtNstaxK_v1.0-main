package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.ui.screens.ShopItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShopViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {

    private val _playerCoins = MutableStateFlow(2500)
    val playerCoins: StateFlow<Int> = _playerCoins.asStateFlow()

    private val _shopItems = MutableStateFlow(defaultInventory())
    val shopItems: StateFlow<List<ShopItem>> = _shopItems.asStateFlow()

    init {
        viewModelScope.launch {
            repository.prefsFlow.collect { prefs ->
                _playerCoins.value = prefs.coins
            }
        }
    }

    fun purchaseItem(itemId: String) {
        val snapshot = _shopItems.value
        val index = snapshot.indexOfFirst { it.id == itemId }
        if (index == -1) return
        val target = snapshot[index]
        if (target.isPurchased || _playerCoins.value < target.price) return

        _shopItems.update {
            it.toMutableList().also { items ->
                items[index] = items[index].copy(isPurchased = true)
            }
        }

        viewModelScope.launch {
            repository.setCoins(_playerCoins.value - target.price)
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
            id = "xp_boost",
            name = "XP Booster",
            description = "Doubles XP for the next 3 matches",
            price = 1250,
            effect = "x2 XP"
        )
    )
}
