package com.neon.connectsort.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neon.connectsort.core.AppContextHolder
import com.neon.connectsort.core.data.AppPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterChipsViewModel : ViewModel() {
    private val repo = AppPreferencesRepository(AppContextHolder.appContext)

    private val _characters = MutableStateFlow(defaultRoster())
    val characters: StateFlow<List<CharacterChip>> = _characters.asStateFlow()

    private val _selectedCharacter = MutableStateFlow<CharacterChip?>(null)
    val selectedCharacter: StateFlow<CharacterChip?> = _selectedCharacter.asStateFlow()

    private val _unlockedCharacters = MutableStateFlow<Set<String>>(setOf("nexus_prime"))
    val unlockedCharacters: StateFlow<Set<String>> = _unlockedCharacters.asStateFlow()

    private val _playerCredits = MutableStateFlow(2500)
    val playerCredits: StateFlow<Int> = _playerCredits.asStateFlow()

    init {
        viewModelScope.launch {
            repo.prefsFlow.collect { prefs ->
                _unlockedCharacters.value = prefs.unlockedCharacterIds
                _playerCredits.value = prefs.coins
                _selectedCharacter.value = _characters.value
                    .map { it.copy(isUnlocked = prefs.unlockedCharacterIds.contains(it.id)) }
                    .firstOrNull { it.id == prefs.selectedCharacterId }
                _characters.value = _characters.value.map {
                    it.copy(isUnlocked = prefs.unlockedCharacterIds.contains(it.id))
                }
            }
        }
    }

    fun selectCharacter(character: CharacterChip) {
        if (character.isUnlocked) {
            viewModelScope.launch { repo.setSelectedCharacter(character.id) }
        }
    }

    fun purchaseCharacter(character: CharacterChip) {
        viewModelScope.launch {
            val currentCoins = _playerCredits.value
            if (currentCoins >= character.price) {
                repo.setCoins(currentCoins - character.price)
                repo.unlockCharacter(character.id)
                repo.setSelectedCharacter(character.id)
            }
        }
    }
}

// --- Static roster data (trimmed for brevity but still functional) ---
private fun defaultRoster(): List<CharacterChip> = listOf(
    CharacterChip(
        id = "nexus_prime",
        name = "NEXUS",
        title = "Primary Neural Interface",
        bio = "Baseline neural interface. Balanced and reliable.",
        faction = "Neon Syndicate",
        rarity = Rarity.COMMON,
        color = androidx.compose.ui.graphics.Color(0xFF00FFFF),
        icon = "?",
        price = 0,
        isUnlocked = true,
        neuralSync = 85,
        abilities = emptyList(),
        storyConnection = "Foundation of the Connect-Sort network."
    ),
    CharacterChip(
        id = "cypher",
        name = "CYPHER",
        title = "Cryptographic Analyst",
        bio = "Pattern recognition specialist with probability tricks.",
        faction = "Circuit Breakers",
        rarity = Rarity.RARE,
        color = androidx.compose.ui.graphics.Color(0xFF9D00FF),
        icon = "??",
        price = 500,
        isUnlocked = false,
        neuralSync = 92,
        abilities = emptyList(),
        storyConnection = "Operates in Neo-Tokyo data markets."
    ),
    CharacterChip(
        id = "spectre",
        name = "SPECTRE",
        title = "Phantom Data Wraith",
        bio = "Rogue AI fragment; phase-shifts through barriers.",
        faction = "Data Wraiths",
        rarity = Rarity.EPIC,
        color = androidx.compose.ui.graphics.Color(0xFF00FFAA),
        icon = "??",
        price = 1500,
        isUnlocked = false,
        neuralSync = 99,
        abilities = emptyList(),
        storyConnection = "Searching for its origin code."
    ),
    CharacterChip(
        id = "valkyrie",
        name = "VALKYRIE",
        title = "Aegis Enforcer",
        bio = "Combat-tuned interface from Aegis Corp.",
        faction = "Neon Syndicate",
        rarity = Rarity.EPIC,
        color = androidx.compose.ui.graphics.Color(0xFFFF0055),
        icon = "??",
        price = 2000,
        isUnlocked = false,
        neuralSync = 110,
        abilities = emptyList(),
        storyConnection = "Hunts spies using stolen tech."
    ),
    CharacterChip(
        id = "oracle",
        name = "ORACLE",
        title = "Temporal Seer",
        bio = "Sees multiple timelines.",
        faction = "Neural Collective",
        rarity = Rarity.LEGENDARY,
        color = androidx.compose.ui.graphics.Color(0xFFFFFF00),
        icon = "??",
        price = 5000,
        isUnlocked = false,
        neuralSync = 150,
        abilities = emptyList(),
        storyConnection = "Appears during temporal anomalies."
    ),
    CharacterChip(
        id = "chimera",
        name = "CHIMERA",
        title = "Hybrid Construct",
        bio = "Adaptive fusion of champion minds.",
        faction = "Circuit Breakers",
        rarity = Rarity.LEGENDARY,
        color = androidx.compose.ui.graphics.Color(0xFFFF00FF),
        icon = "??",
        price = 7500,
        isUnlocked = false,
        neuralSync = 135,
        abilities = emptyList(),
        storyConnection = "Seeks purpose beyond combat."
    )
)
