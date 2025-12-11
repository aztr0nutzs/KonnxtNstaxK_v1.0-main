package com.neon.connectsort.core.domain

import androidx.compose.ui.graphics.Color

data class CharacterChip(
    val id: String,
    val name: String,
    val title: String,
    val bio: String,
    val faction: String,
    val rarity: ChipRarity,
    val color: Color,
    val icon: String,
    val price: Int,
    val isUnlocked: Boolean = false,
    val neuralSync: Int,
    val abilities: List<ChipAbility> = emptyList(),
    val storyConnection: String
)

enum class ChipRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

data class GameEffect(
    val type: EffectType,
    val value: Double,
    val duration: Int
)

enum class EffectType {
    POINT_MULTIPLIER,
    EXTRA_MOVE,
    SHIELD,
    HAZARD_RESIST,
    SCORE_BOOST
}

sealed class ChipAbility(
    val name: String,
    val description: String,
    val icon: String,
    val type: String,
    val color: Color,
    val energyCost: Int,
    val cooldown: Int,
    val effect: GameEffect? = null
) {
    object BonusPoints : ChipAbility(
        name = "Bonus Points",
        description = "Earn 12% extra points on a successful win.",
        icon = "+P",
        type = "Score",
        color = Color(0xFF00FF8A),
        energyCost = 5,
        cooldown = 12,
        effect = GameEffect(EffectType.POINT_MULTIPLIER, 1.12, 0)
    )

    data class ExtraMove(val extraMoves: Int) : ChipAbility(
        name = "Extra Move",
        description = "Start each run with $extraMoves bonus move(s).",
        icon = "+M",
        type = "Opening",
        color = Color(0xFF7F7FFF),
        energyCost = 4,
        cooldown = 8,
        effect = GameEffect(EffectType.EXTRA_MOVE, extraMoves.toDouble(), 0)
    )

    data class Shield(val charges: Int) : ChipAbility(
        name = "Shield Charge",
        description = "Absorb $charges hazard(s) per engagement.",
        icon = "SH",
        type = "Defense",
        color = Color(0xFFFFC15F),
        energyCost = 6,
        cooldown = 18,
        effect = GameEffect(EffectType.SHIELD, charges.toDouble(), 0)
    )

    object HazardResist : ChipAbility(
        name = "Hazard Sentry",
        description = "Reduce incoming hazard chance by 15%.",
        icon = "HZ",
        type = "Mitigation",
        color = Color(0xFF00BCD4),
        energyCost = 5,
        cooldown = 10,
        effect = GameEffect(EffectType.HAZARD_RESIST, 0.85, 0)
    )

    object ScoreBoost : ChipAbility(
        name = "Prism Surge",
        description = "Score bonus grows when streaks exceed 3.",
        icon = "SB",
        type = "Boost",
        color = Color(0xFFFF5E99),
        energyCost = 6,
        cooldown = 15,
        effect = GameEffect(EffectType.SCORE_BOOST, 1.25, 0)
    )
}
