package com.kaikunxp.pokeshowdown.data.model

import kotlinx.serialization.Serializable

/**
 * Raw parsed line message from Pokémon Showdown WebSocket.
 */
data class ShowdownMessage(
    val room: String,
    val command: String,
    val args: List<String>,
    val rawLine: String
)

/**
 * Logged-in user information.
 */
data class User(
    val name: String = "",
    val named: Boolean = false,
    val avatar: String = "1",
    val group: String = "",
    val token: String? = null
)

/**
 * Available play format in Pokémon Showdown.
 */
data class ShowdownFormat(
    val id: String,
    val name: String,
    val section: String = "",
    val isSearchable: Boolean = true
)

/**
 * Battle Move representation in an active battle or team request.
 */
@Serializable
data class BattleMove(
    val id: String = "",
    val name: String = "",
    val pp: Int = 0,
    val maxPp: Int = 0,
    val target: String = "normal",
    val disabled: Boolean = false,
    val type: String = "Normal"
)

/**
 * Active or benched Pokémon in a battle state.
 */
data class BattlePokemon(
    val id: String = "",
    val name: String = "",
    val species: String = "",
    val details: String = "",
    val hp: Int = 100,
    val maxHp: Int = 100,
    val status: String? = null,
    val fainted: Boolean = false,
    val level: Int = 100,
    val gender: String? = null,
    val item: String? = null,
    val ability: String? = null,
    val teraType: String? = null,
    val stats: Map<String, Int> = emptyMap(),
    val moves: List<BattleMove> = emptyList(),
    val position: String = "" // e.g. "p1a", "p2a"
) {
    val hpPercent: Float
        get() = if (maxHp > 0) hp.toFloat() / maxHp.toFloat() else 0f
}

/**
 * Battle Side (Player or Opponent).
 */
data class BattleSide(
    val id: String = "p1",
    val name: String = "Player",
    val active: BattlePokemon? = null,
    val team: List<BattlePokemon> = emptyList()
)

/**
 * Active attack animation playing over the 2D field.
 */
data class ActiveAttackAnimation(
    val attackerPos: String, // "p1" or "p2"
    val targetPos: String,   // "p2" or "p1"
    val moveName: String,
    val moveType: String,
    val id: Long = System.currentTimeMillis()
)

/**
 * Comprehensive Battle State representation.
 */
data class BattleState(
    val roomId: String = "",
    val format: String = "gen9randombattle",
    val turn: Int = 0,
    val mySideId: String = "p1", // "p1" or "p2"
    val p1: BattleSide = BattleSide(id = "p1", name = "Player 1"),
    val p2: BattleSide = BattleSide(id = "p2", name = "Player 2"),
    val waitingForChoice: Boolean = false,
    val canMegaEvolve: Boolean = false,
    val canTerastallize: Boolean = false,
    val canDynamax: Boolean = false,
    val availableMoves: List<BattleMove> = emptyList(),
    val availableSwitches: List<BattlePokemon> = emptyList(),
    val logs: List<String> = emptyList(),
    val activeAnimation: ActiveAttackAnimation? = null,
    val winner: String? = null,
    val ended: Boolean = false
) {
    val mySide: BattleSide
        get() = if (mySideId == "p1") p1 else p2

    val opponentSide: BattleSide
        get() = if (mySideId == "p1") p2 else p1
}

/**
 * Saved or created Pokémon Team.
 */
data class Team(
    val id: String,
    val name: String,
    val format: String,
    val packedString: String
)

/**
 * Chat message inside lobby or battle room.
 */
data class ChatMessage(
    val room: String,
    val sender: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Incoming Direct Challenge details.
 */
data class ChallengeInfo(
    val challenger: String,
    val format: String
)
