package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.DEFAULT_UNLOCKED_CHARACTERS
import com.neon.connectsort.core.data.EconomyRepository
import com.neon.connectsort.core.data.ChipRepository
import com.neon.connectsort.core.domain.CharacterChip
import com.neon.connectsort.core.domain.ChipAbility
import com.neon.connectsort.ui.audio.AnalyticsTracker
import com.neon.connectsort.ui.audio.AudioManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CharacterChipsViewModel(
    private val economy: EconomyRepository,
    private val analyticsTracker: AnalyticsTracker,
    private val audioManager: AudioManager
) : ViewModel() {

    private val baseChips = ChipRepository.chips

    private val _characters = MutableStateFlow(baseChips)
    val characters: StateFlow<List<CharacterChip>> = _characters.asStateFlow()

    private val _selectedCharacter = MutableStateFlow<CharacterChip?>(null)
    val selectedCharacter: StateFlow<CharacterChip?> = _selectedCharacter.asStateFlow()

    private val _unlockedCharacters = MutableStateFlow(DEFAULT_UNLOCKED_CHARACTERS)
    val unlockedCharacters: StateFlow<Set<String>> = _unlockedCharacters.asStateFlow()

    private val _playerCredits = MutableStateFlow(0)
    val playerCredits: StateFlow<Int> = _playerCredits.asStateFlow()

    private val _equippedAbility = MutableStateFlow<ChipAbility?>(null)
    val equippedAbility: StateFlow<ChipAbility?> = _equippedAbility.asStateFlow()

    init {
        viewModelScope.launch {
            economy.unlockedChips.collect { unlocked ->
                _unlockedCharacters.value = unlocked
                _characters.value = baseChips.map { chip ->
                    chip.copy(isUnlocked = chip.id in unlocked)
                }
            }
        }

        viewModelScope.launch {
            economy.coinBalance.collect { _playerCredits.value = it }
        }

        viewModelScope.launch {
            economy.selectedChipId.collect { id ->
                val updated = _characters.value
                _selectedCharacter.value = updated.firstOrNull { it.id == id }
                analyticsTracker.logEvent("chip_selected", mapOf("chip" to (id ?: "none")))
            }
        }

        viewModelScope.launch {
            economy.equippedAbilityName.collect { abilityName ->
                _equippedAbility.value = abilityName?.let { name ->
                    _characters.value.flatMap { it.abilities }.firstOrNull { ability ->
                        ability.name == name
                    }
                }
            }
        }
    }

    fun selectCharacter(character: CharacterChip) {
        if (!character.isUnlocked) return
        viewModelScope.launch {
            economy.selectChip(character.id)
            audioManager.playSample(AudioManager.Sample.SELECT)
        }
    }

    fun purchaseCharacter(character: CharacterChip) {
        if (character.isUnlocked) return
        viewModelScope.launch {
            val balance = _playerCredits.value
            if (balance < character.price) return@launch
            economy.adjustCoins(-character.price)
            economy.unlockChip(character.id)
            economy.selectChip(character.id)
            analyticsTracker.logEvent(
                "chip_purchase",
                mapOf("chip" to character.id, "price" to character.price)
            )
            audioManager.playSample(AudioManager.Sample.COIN)
        }
    }

    fun equipAbility(ability: ChipAbility) {
        viewModelScope.launch {
            economy.selectAbility(ability.name)
            analyticsTracker.logEvent(
                "ability_equipped",
                mapOf("ability" to ability.name)
            )
            audioManager.playSample(AudioManager.Sample.POWER_UP)
        }
    }
}
