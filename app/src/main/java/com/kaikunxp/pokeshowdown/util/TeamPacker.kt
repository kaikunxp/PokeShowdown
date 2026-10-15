package com.kaikunxp.pokeshowdown.util

data class CustomTeamMember(
    val species: String,
    val name: String = species,
    val item: String = "",
    val ability: String = "",
    val moves: List<String> = emptyList()
)

object TeamPacker {

    private val defaultMovesets: Map<String, List<String>> = mapOf(
        "pikachu" to listOf("Thunderbolt", "Volt Switch", "Grass Knot", "Surf"),
        "charizard" to listOf("Flamethrower", "Air Slash", "Dragon Pulse", "Roost"),
        "venusaur" to listOf("Sludge Bomb", "Giga Drain", "Earth Power", "Synthesis"),
        "blastoise" to listOf("Surf", "Ice Beam", "Rapid Spin", "Flash Cannon"),
        "greattusk" to listOf("Headlong Rush", "Close Combat", "Rapid Spin", "Stealth Rock"),
        "dragapult" to listOf("Draco Meteor", "Shadow Ball", "U-turn", "Fire Blast"),
        "kingambit" to listOf("Kowtow Cleave", "Sucker Punch", "Iron Head", "Swords Dance"),
        "gholdengo" to listOf("Make It Rain", "Shadow Ball", "Focus Blast", "Nasty Plot"),
        "ironvaliant" to listOf("Moonblast", "Close Combat", "Thunderbolt", "Knock Off"),
        "roaringmoon" to listOf("Outrage", "Knock Off", "Dragon Dance", "Earthquake"),
        "corviknight" to listOf("Brave Bird", "Body Press", "Roost", "U-turn"),
        "volcarona" to listOf("Fiery Dance", "Bug Buzz", "Giga Drain", "Quiver Dance"),
        "meowscarada" to listOf("Flower Trick", "Knock Off", "U-turn", "Play Rough"),
        "ceruledge" to listOf("Bitter Blade", "Poltergeist", "Shadow Sneak", "Swords Dance"),
        "skeledirge" to listOf("Torch Song", "Hex", "Slack Off", "Will-O-Wisp"),
        "clodsire" to listOf("Earthquake", "Toxic", "Recover", "Stealth Rock"),
        "dondozo" to listOf("Wave Crash", "Waterfall", "Rest", "Sleep Talk"),
        "toxapex" to listOf("Scald", "Toxic", "Recover", "Haze"),
        "ferrothorn" to listOf("Power Whip", "Gyro Ball", "Leech Seed", "Stealth Rock"),
        "excadrill" to listOf("Earthquake", "Iron Head", "Rapid Spin", "Rock Slide"),
        "kyogre" to listOf("Water Spout", "Origin Pulse", "Thunder", "Ice Beam"),
        "groudon" to listOf("Precipice Blades", "Stone Edge", "Stealth Rock", "Swords Dance"),
        "rayquaza" to listOf("Dragon Ascent", "Extreme Speed", "Earthquake", "Dragon Dance")
    )

    fun getDefaultMoves(species: String): List<String> {
        val key = species.lowercase().replace(Regex("[^a-z0-9]"), "")
        return defaultMovesets[key] ?: listOf("Tackle", "Protect")
    }

    fun packTeam(members: List<CustomTeamMember>): String {
        return members.joinToString("]") { member ->
            val cleanSpecies = member.species.replace(" ", "")
            val movesList = if (member.moves.isNotEmpty()) member.moves else getDefaultMoves(cleanSpecies)
            val packedMoves = movesList.joinToString(",") { it.lowercase().replace(Regex("[^a-z0-9]"), "") }
            
            // Format: Species|Nickname|Item|Ability|Moves|Nature|EVs|Gender|IVs|Shiny|Level|Happiness|TeraType
            "$cleanSpecies||${member.item}|${member.ability}|$packedMoves|Timid|||||||"
        }
    }
}
