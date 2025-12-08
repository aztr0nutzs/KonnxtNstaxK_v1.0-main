package com.neon.connectsort.ui.screens.viewmodels

import androidx.compose.ui.graphics.Color

data class CharacterChip(
    val id: String,
    val name: String,
    val title: String,
    val bio: String,
    val faction: String,
    val rarity: Rarity,
    val color: Color,
    val icon: String,
    val price: Int,
    val isUnlocked: Boolean,
    val neuralSync: Int,
    val abilities: List<CharacterAbility>,
    val storyConnection: String
)

data class CharacterAbility(
    val name: String,
    val description: String,
    val type: String,
    val icon: String,
    val color: Color,
    val energyCost: Int,
    val cooldown: Int,
    val effect: GameEffect? = null
)

data class GameEffect(
    val type: EffectType,
    val value: Double,
    val duration: Int
)

enum class EffectType {
    POINT_MULTIPLIER,
    HINT_BOOST,
    COOLDOWN_RESET,
    FORESIGHT,
    ABILITY_LOCK,
    POINT_STEAL,
    INVULNERABILITY,
    CHIP_DUPLICATION,
    CHIP_REMOVAL,
    DAMAGE_REFLECT,
    RANGE_BOOST,
    FULL_REVEAL,
    TIME_MANIPULATION,
    TIMELINE_SELECTION,
    MOVE_REWIND,
    ABILITY_MIMICRY,
    ABILITY_SLOT_BOOST,
    RANDOM_ABILITIES
}

enum class Rarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}
