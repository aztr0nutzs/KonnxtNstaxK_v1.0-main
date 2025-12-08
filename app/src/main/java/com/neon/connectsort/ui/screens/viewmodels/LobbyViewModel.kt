package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LobbyState(
    val totalCoins: Int = 0,
    val unlockedModes: Set<String> = setOf("connect4"),
    val activeChallenges: List<String> = emptyList()
)

class LobbyViewModel : ViewModel() {
    private val _state = MutableStateFlow(LobbyState())
    val state: StateFlow<LobbyState> = _state.asStateFlow()

    fun unlockMode(id: String) {
        _state.update { it.copy(unlockedModes = it.unlockedModes + id) }
    }

    fun awardCoins(amount: Int) {
        if (amount == 0) return
        _state.update { it.copy(totalCoins = (it.totalCoins + amount).coerceAtLeast(0)) }
    }
}
