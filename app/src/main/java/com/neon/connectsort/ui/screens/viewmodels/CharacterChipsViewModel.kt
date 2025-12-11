package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.data.AppPreferencesRepository
import com.neon.connectsort.core.data.ChipRepository
import com.neon.connectsort.core.domain.CharacterChip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CharacterChipsViewModel(
    private val repository: AppPreferencesRepository
) : ViewModel() {

    private val baseChips = ChipRepository.chips

    private val _characters = MutableStateFlow(baseChips)
    val characters: StateFlow<List<CharacterChip>> = _characters.asStateFlow()

    private val _selectedCharacter = MutableStateFlow<CharacterChip?>(null)
    val selectedCharacter: StateFlow<CharacterChip?> = _selectedCharacter.asStateFlow()

    private val _unlockedCharacters = MutableStateFlow<Set<String>>(setOf("nexus_prime"))
    val unlockedCharacters: StateFlow<Set<String>> = _unlockedCharacters.asStateFlow()

    private val _playerCredits = MutableStateFlow(2500)
    val playerCredits: StateFlow<Int> = _playerCredits.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUnlockedChipsFlow().collect { unlocked ->
                _unlockedCharacters.value = unlocked
            }
        }

        viewModelScope.launch {
            repository.prefsFlow.collect { prefs ->
                val updated = baseChips.map { chip ->
                    chip.copy(isUnlocked = prefs.unlockedCharacterIds.contains(chip.id))
                }
                _playerCredits.value = prefs.coins
                _selectedCharacter.value = updated.firstOrNull { it.id == prefs.selectedCharacterId }
                _characters.value = updated
            }
        }
    }

    fun selectCharacter(character: CharacterChip) {
            if (character.isUnlocked) {
                viewModelScope.launch { repository.setSelectedCharacter(character.id) }
            }
    }

    fun purchaseCharacter(character: CharacterChip) {
        viewModelScope.launch {
            val currentCoins = _playerCredits.value
            if (currentCoins >= character.price) {
                repository.setCoins(currentCoins - character.price)
                repository.unlockChip(character.id)
                repository.setSelectedCharacter(character.id)
            }
        }
    }

}
