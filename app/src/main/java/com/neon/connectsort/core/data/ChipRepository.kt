package com.neon.connectsort.core.data

import com.neon.connectsort.core.domain.CharacterChip
import com.neon.connectsort.core.domain.ChipAbility
import com.neon.connectsort.core.domain.ChipRarity
import androidx.compose.ui.graphics.Color

object ChipRepository {
    val chips: List<CharacterChip> = listOf(
        CharacterChip(
            id = "nexus_prime",
            name = "NEXUS",
            title = "Primary Neural Interface",
            bio = "Balanced, resilient, and endlessly adaptive.",
            faction = "Neon Syndicate",
            rarity = ChipRarity.COMMON,
            color = Color(0xFF00FFFF),
            icon = "NX",
            price = 0,
            isUnlocked = true,
            neuralSync = 85,
            abilities = listOf(
                ChipAbility.ExtraMove(extraMoves = 1)
            ),
            storyConnection = "Foundation of the Connect-Sort network."
        ),
        CharacterChip(
            id = "cypher",
            name = "CYPHER",
            title = "Cryptographic Analyst",
            bio = "Sees patterns before they form, bending probability tables.",
            faction = "Circuit Breakers",
            rarity = ChipRarity.RARE,
            color = Color(0xFF9D00FF),
            icon = "CY",
            price = 500,
            neuralSync = 92,
            abilities = listOf(
                ChipAbility.BonusPoints,
                ChipAbility.HazardResist
            ),
            storyConnection = "Operates in Neo-Tokyo data markets."
        ),
        CharacterChip(
            id = "spectre",
            name = "SPECTRE",
            title = "Phantom Data Wraith",
            bio = "Phase-shifts around threats and rewinds momentary errors.",
            faction = "Data Wraiths",
            rarity = ChipRarity.EPIC,
            color = Color(0xFF00FFAA),
            icon = "SP",
            price = 1500,
            neuralSync = 99,
            abilities = listOf(
                ChipAbility.Shield(charges = 1),
                ChipAbility.ScoreBoost
            ),
            storyConnection = "Searching for its origin code."
        ),
        CharacterChip(
            id = "valkyrie",
            name = "VALKYRIE",
            title = "Aegis Enforcer",
            bio = "Combat-hardened and tuned for relentless pressure.",
            faction = "Neon Syndicate",
            rarity = ChipRarity.EPIC,
            color = Color(0xFFFF0055),
            icon = "VK",
            price = 2000,
            neuralSync = 110,
            abilities = listOf(
                ChipAbility.Shield(charges = 2),
                ChipAbility.ExtraMove(extraMoves = 1)
            ),
            storyConnection = "Hunts spies using stolen tech."
        ),
        CharacterChip(
            id = "oracle",
            name = "ORACLE",
            title = "Temporal Seer",
            bio = "A ripple through timelines that whispers perfect moves.",
            faction = "Neural Collective",
            rarity = ChipRarity.LEGENDARY,
            color = Color(0xFFFFFF00),
            icon = "OR",
            price = 5000,
            neuralSync = 150,
            abilities = listOf(
                ChipAbility.ExtraMove(extraMoves = 2),
                ChipAbility.ScoreBoost
            ),
            storyConnection = "Appears during temporal anomalies."
        ),
        CharacterChip(
            id = "chimera",
            name = "CHIMERA",
            title = "Hybrid Construct",
            bio = "Fuses champion minds into a single unpredictable entity.",
            faction = "Circuit Breakers",
            rarity = ChipRarity.LEGENDARY,
            color = Color(0xFFFF00FF),
            icon = "CH",
            price = 7500,
            neuralSync = 135,
            abilities = listOf(
                ChipAbility.BonusPoints,
                ChipAbility.HazardResist,
                ChipAbility.ScoreBoost
            ),
            storyConnection = "Seeks purpose beyond combat."
        )
    )
}
