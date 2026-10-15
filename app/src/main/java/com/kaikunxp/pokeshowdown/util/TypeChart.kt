package com.kaikunxp.pokeshowdown.util

object TypeChart {

    private val types = listOf(
        "normal", "fire", "water", "grass", "electric", "ice",
        "fighting", "poison", "ground", "flying", "psychic", "bug",
        "rock", "ghost", "dragon", "dark", "steel", "fairy"
    )

    // Attacker index -> Defender index -> multiplier
    private val chart: Map<String, Map<String, Float>> = mapOf(
        "normal" to mapOf("rock" to 0.5f, "ghost" to 0f, "steel" to 0.5f),
        "fire" to mapOf("fire" to 0.5f, "water" to 0.5f, "grass" to 2f, "ice" to 2f, "bug" to 2f, "rock" to 0.5f, "dragon" to 0.5f, "steel" to 2f),
        "water" to mapOf("fire" to 2f, "water" to 0.5f, "grass" to 0.5f, "ground" to 2f, "rock" to 2f, "dragon" to 0.5f),
        "grass" to mapOf("fire" to 0.5f, "water" to 2f, "grass" to 0.5f, "poison" to 0.5f, "ground" to 2f, "flying" to 0.5f, "bug" to 0.5f, "rock" to 2f, "dragon" to 0.5f, "steel" to 0.5f),
        "electric" to mapOf("water" to 2f, "grass" to 0.5f, "electric" to 0.5f, "ground" to 0f, "flying" to 2f, "dragon" to 0.5f),
        "ice" to mapOf("fire" to 0.5f, "water" to 0.5f, "grass" to 2f, "ice" to 0.5f, "ground" to 2f, "flying" to 2f, "dragon" to 2f, "steel" to 0.5f),
        "fighting" to mapOf("normal" to 2f, "ice" to 2f, "poison" to 0.5f, "flying" to 0.5f, "psychic" to 0.5f, "bug" to 0.5f, "rock" to 2f, "ghost" to 0f, "dark" to 2f, "steel" to 2f, "fairy" to 0.5f),
        "poison" to mapOf("grass" to 2f, "poison" to 0.5f, "ground" to 0.5f, "rock" to 0.5f, "ghost" to 0.5f, "steel" to 0f, "fairy" to 2f),
        "ground" to mapOf("fire" to 2f, "grass" to 0.5f, "electric" to 2f, "poison" to 2f, "flying" to 0f, "bug" to 0.5f, "rock" to 2f, "steel" to 2f),
        "flying" to mapOf("grass" to 2f, "electric" to 0.5f, "fighting" to 2f, "bug" to 2f, "rock" to 0.5f, "steel" to 0.5f),
        "psychic" to mapOf("fighting" to 2f, "poison" to 2f, "psychic" to 0.5f, "dark" to 0f, "steel" to 0.5f),
        "bug" to mapOf("fire" to 0.5f, "grass" to 2f, "fighting" to 0.5f, "poison" to 0.5f, "flying" to 0.5f, "psychic" to 2f, "ghost" to 0.5f, "dark" to 2f, "steel" to 0.5f, "fairy" to 0.5f),
        "rock" to mapOf("fire" to 2f, "ice" to 2f, "fighting" to 0.5f, "ground" to 0.5f, "flying" to 2f, "bug" to 2f, "steel" to 0.5f),
        "ghost" to mapOf("normal" to 0f, "psychic" to 2f, "ghost" to 2f, "dark" to 0.5f),
        "dragon" to mapOf("dragon" to 2f, "steel" to 0.5f, "fairy" to 0f),
        "dark" to mapOf("fighting" to 0.5f, "psychic" to 2f, "ghost" to 2f, "dark" to 0.5f, "fairy" to 0.5f),
        "steel" to mapOf("fire" to 0.5f, "water" to 0.5f, "electric" to 0.5f, "ice" to 2f, "rock" to 2f, "steel" to 0.5f, "fairy" to 2f),
        "fairy" to mapOf("fire" to 0.5f, "fighting" to 2f, "poison" to 0.5f, "dragon" to 2f, "dark" to 2f, "steel" to 0.5f)
    )

    fun getMultiplier(moveType: String, targetType: String): Float {
        val atk = moveType.lowercase().trim()
        val def = targetType.lowercase().trim()
        val subMap = chart[atk] ?: return 1.0f
        return subMap[def] ?: 1.0f
    }

    fun getCombinedMultiplier(moveType: String, targetTypes: List<String>): Float {
        var mult = 1.0f
        for (t in targetTypes) {
            mult *= getMultiplier(moveType, t)
        }
        return mult
    }

    fun getEffectivenessLabel(multiplier: Float): String {
        return when {
            multiplier >= 4f -> "4x Super Effective!"
            multiplier >= 2f -> "2x Super Effective"
            multiplier == 1f -> "1x Effective"
            multiplier == 0.5f -> "0.5x Not Very Effective"
            multiplier == 0.25f -> "0.25x Not Very Effective"
            multiplier == 0f -> "0x No Effect"
            else -> "${multiplier}x Effective"
        }
    }
}
