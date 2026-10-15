package com.kaikunxp.pokeshowdown.util

import java.util.concurrent.ConcurrentHashMap

data class MoveDetails(
    val name: String,
    val type: String,
    val basePower: Int,
    val accuracy: Int,
    val category: String, // "Physical", "Special", "Status"
    val description: String
)

object MoveDex {

    private val dynamicMoveDex = ConcurrentHashMap<String, MoveDetails>()

    fun updateFromApi(data: Map<String, MoveDetails>) {
        dynamicMoveDex.putAll(data)
    }

    fun updateDescriptionsFromApi(descriptions: Map<String, String>) {
        descriptions.forEach { (moveKey, descText) ->
            val existing = dynamicMoveDex[moveKey] ?: dex[moveKey]
            if (existing != null) {
                dynamicMoveDex[moveKey] = existing.copy(description = descText)
            } else {
                val fallbackName = moveKey.replaceFirstChar { it.uppercase() }
                dynamicMoveDex[moveKey] = MoveDetails(
                    name = fallbackName,
                    type = "Normal",
                    basePower = 80,
                    accuracy = 100,
                    category = "Special",
                    description = descText
                )
            }
        }
    }

    private val dex: Map<String, MoveDetails> = mapOf(
        "thunderbolt" to MoveDetails("Thunderbolt", "Electric", 90, 100, "Special", "10% chance to paralyze the target."),
        "voltswitch" to MoveDetails("Volt Switch", "Electric", 70, 100, "Special", "User switches out after damaging the target."),
        "flamethrower" to MoveDetails("Flamethrower", "Fire", 90, 100, "Special", "10% chance to burn the target."),
        "fireblast" to MoveDetails("Fire Blast", "Fire", 110, 85, "Special", "10% chance to burn the target."),
        "surf" to MoveDetails("Surf", "Water", 90, 100, "Special", "Hits all adjacent Pokémon in battle."),
        "scald" to MoveDetails("Scald", "Water", 80, 100, "Special", "30% chance to burn the target."),
        "hydropump" to MoveDetails("Hydro Pump", "Water", 110, 80, "Special", "High power Water attack."),
        "earthquake" to MoveDetails("Earthquake", "Ground", 100, 100, "Physical", "Hits all adjacent Pokémon. Power doubles if target underground."),
        "closecombat" to MoveDetails("Close Combat", "Fighting", 120, 100, "Physical", "Lowers the user's Defense and Sp. Def by 1 stage."),
        "rapidspin" to MoveDetails("Rapid Spin", "Normal", 50, 100, "Physical", "Frees user from hazards/bind and raises Speed by 1 stage."),
        "stealthrock" to MoveDetails("Stealth Rock", "Rock", 0, 100, "Status", "Lays entry hazards that damage foes switching in."),
        "spikes" to MoveDetails("Spikes", "Ground", 0, 100, "Status", "Lays entry hazards that hurt foes grounded on switch-in."),
        "headlongrush" to MoveDetails("Headlong Rush", "Ground", 120, 100, "Physical", "Lowers the user's Defense and Sp. Def by 1 stage."),
        "uturn" to MoveDetails("U-turn", "Bug", 70, 100, "Physical", "User switches out after damaging the target."),
        "suckerpunch" to MoveDetails("Sucker Punch", "Dark", 70, 100, "Physical", "Usually goes first. Fails if target is not preparing an attack."),
        "firstimpression" to MoveDetails("First Impression", "Bug", 90, 100, "Physical", "Hits first. Only works on the user's first turn out."),
        "knockoff" to MoveDetails("Knock Off", "Dark", 65, 100, "Physical", "1.5x power if target holds an item, and removes the item."),
        "outrage" to MoveDetails("Outrage", "Dragon", 120, 100, "Physical", "Attacks for 2-3 turns, then user becomes confused."),
        "dragondance" to MoveDetails("Dragon Dance", "Dragon", 0, 100, "Status", "Raises user's Attack and Speed by 1 stage."),
        "heatcrash" to MoveDetails("Heat Crash", "Fire", 1, 100, "Physical", "Damage increases the heavier the user is compared to the target."),
        "psyshock" to MoveDetails("Psyshock", "Psychic", 80, 100, "Special", "Calculates damage using target's Physical Defense."),
        "psychic" to MoveDetails("Psychic", "Psychic", 90, 100, "Special", "10% chance to lower the target's Sp. Def by 1 stage."),
        "calmmind" to MoveDetails("Calm Mind", "Psychic", 0, 100, "Status", "Raises user's Sp. Atk and Sp. Def by 1 stage."),
        "shadowball" to MoveDetails("Shadow Ball", "Ghost", 80, 100, "Special", "20% chance to lower the target's Sp. Def by 1 stage."),
        "ironhead" to MoveDetails("Iron Head", "Steel", 80, 100, "Physical", "30% chance to make the target flinch."),
        "makeitrain" to MoveDetails("Make It Rain", "Steel", 120, 100, "Special", "Lowers the user's Sp. Atk by 1 stage. Money drops."),
        "kowtowcleave" to MoveDetails("Kowtow Cleave", "Dark", 85, 100, "Physical", "This move does not miss."),
        "moonblast" to MoveDetails("Moonblast", "Fairy", 95, 100, "Special", "30% chance to lower the target's Sp. Atk by 1 stage."),
        "dazzlinggleam" to MoveDetails("Dazzling Gleam", "Fairy", 80, 100, "Special", "Hits all adjacent foes."),
        "sludgebomb" to MoveDetails("Sludge Bomb", "Poison", 90, 100, "Special", "30% chance to poison the target."),
        "earthpower" to MoveDetails("Earth Power", "Ground", 90, 100, "Special", "10% chance to lower the target's Sp. Def by 1 stage."),
        "leafstorm" to MoveDetails("Leaf Storm", "Grass", 130, 90, "Special", "Lowers the user's Sp. Atk by 2 stages."),
        "energyball" to MoveDetails("Energy Ball", "Grass", 90, 100, "Special", "10% chance to lower the target's Sp. Def by 1 stage."),
        "gigadrain" to MoveDetails("Giga Drain", "Grass", 75, 100, "Special", "User recovers 50% of the damage dealt."),
        "roost" to MoveDetails("Roost", "Flying", 0, 100, "Status", "User restores 50% max HP. Loses Flying type for the turn."),
        "morningsun" to MoveDetails("Morning Sun", "Normal", 0, 100, "Status", "Restores HP depending on weather."),
        "toxic" to MoveDetails("Toxic", "Poison", 0, 90, "Status", "Badly poisons the target."),
        "foulplay" to MoveDetails("Foul Play", "Dark", 95, 100, "Physical", "Uses the target's Attack stat in damage calculation."),
        "icebeam" to MoveDetails("Ice Beam", "Ice", 90, 100, "Special", "10% chance to freeze the target."),
        "blizzard" to MoveDetails("Blizzard", "Ice", 110, 70, "Special", "10% chance to freeze. 100% accuracy in Snow."),
        "dracometeor" to MoveDetails("Draco Meteor", "Dragon", 130, 90, "Special", "Lowers the user's Sp. Atk by 2 stages."),
        "dragonclaw" to MoveDetails("Dragon Claw", "Dragon", 80, 100, "Physical", "Standard Dragon-type physical attack.")
    )

    fun getDetails(moveId: String, moveName: String, fallbackType: String): MoveDetails {
        val key = moveId.lowercase().replace(Regex("[^a-z0-9]"), "")
        val found = dynamicMoveDex[key] ?: dex[key]
        if (found != null) return found

        return MoveDetails(
            name = moveName,
            type = fallbackType,
            basePower = 80,
            accuracy = 100,
            category = "Special",
            description = "$moveName is a $fallbackType-type move."
        )
    }
}
