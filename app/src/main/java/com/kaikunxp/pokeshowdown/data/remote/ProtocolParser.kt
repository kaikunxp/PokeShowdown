package com.kaikunxp.pokeshowdown.data.remote

import com.kaikunxp.pokeshowdown.data.model.BattleMove
import com.kaikunxp.pokeshowdown.data.model.BattlePokemon
import com.kaikunxp.pokeshowdown.data.model.ShowdownFormat
import com.kaikunxp.pokeshowdown.data.model.ShowdownMessage
import com.kaikunxp.pokeshowdown.util.MoveDex
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ProtocolParser {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Parses raw incoming socket text frames into discrete [ShowdownMessage] instances.
     */
    fun parseFrame(rawFrame: String): List<ShowdownMessage> {
        val messages = mutableListOf<ShowdownMessage>()
        var currentRoom = "lobby"

        val lines = rawFrame.split("\n")
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            if (trimmed.startsWith(">")) {
                currentRoom = trimmed.substring(1).trim()
                continue
            }

            if (trimmed.startsWith("|")) {
                val parts = trimmed.split("|")
                val command = parts.getOrNull(1) ?: ""
                val args = if (parts.size > 2) parts.subList(2, parts.size) else emptyList()
                messages.add(
                    ShowdownMessage(
                        room = currentRoom,
                        command = command,
                        args = args,
                        rawLine = trimmed
                    )
                )
            }
        }
        return messages
    }

    /**
     * Parses Showdown's `|formats|` string into a structured list of formats.
     */
    fun parseFormats(args: List<String>): List<ShowdownFormat> {
        val formats = mutableListOf<ShowdownFormat>()
        var currentSection = "Other"

        for (item in args) {
            if (item.isEmpty()) continue
            if (item.contains(",")) {
                val parts = item.split(",")
                val name = parts[0]
                val searchability = parts.getOrNull(1)?.toIntOrNull() ?: 1
                val id = name.lowercase().replace(Regex("[^a-z0-9]"), "")
                formats.add(
                    ShowdownFormat(
                        id = id,
                        name = name,
                        section = currentSection,
                        isSearchable = searchability > 0
                    )
                )
            } else if (item.startsWith(",") || item.endsWith(",") || !item.contains("|")) {
                val sectionName = item.replace(Regex("^,+|,+$"), "").trim()
                if (sectionName.isNotEmpty()) {
                    currentSection = sectionName
                }
            }
        }
        return formats
    }

    /**
     * Parses Showdown's `|updatesearch|` JSON payload.
     */
    fun parseUpdateSearch(jsonString: String): UpdateSearchData? {
        if (jsonString.isBlank()) return null
        return try {
            val root = json.parseToJsonElement(jsonString).jsonObject

            val searchingList = mutableListOf<String>()
            root["searching"]?.jsonArray?.forEach { elem ->
                searchingList.add(elem.jsonPrimitive.content)
            }

            val gamesMap = mutableMapOf<String, String>()
            root["games"]?.jsonObject?.forEach { (roomId, fmt) ->
                gamesMap[roomId] = fmt.jsonPrimitive.content
            }

            UpdateSearchData(searchingList, gamesMap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Parses Showdown's `|request|` JSON payload sent during battles.
     * Returns pair of (availableMoves, availableSwitches, canMega, canTera, canDynamax).
     */
    fun parseBattleRequest(
        jsonString: String
    ): BattleRequestData? {
        if (jsonString.isBlank()) return null
        return try {
            val root = json.parseToJsonElement(jsonString).jsonObject

            val active = root["active"]?.jsonArray?.getOrNull(0)?.jsonObject
            val movesList = mutableListOf<BattleMove>()

            active?.get("moves")?.jsonArray?.forEach { moveElem ->
                val mObj = moveElem.jsonObject
                val moveName = mObj["move"]?.jsonPrimitive?.content ?: ""
                val moveId = mObj["id"]?.jsonPrimitive?.content ?: moveName.lowercase().replace(Regex("[^a-z0-9]"), "")
                val pp = mObj["pp"]?.jsonPrimitive?.intOrNull ?: 10
                val maxPp = mObj["maxpp"]?.jsonPrimitive?.intOrNull ?: 10
                val disabled = mObj["disabled"]?.jsonPrimitive?.booleanOrNull ?: false
                val target = mObj["target"]?.jsonPrimitive?.content ?: "normal"

                val details = MoveDex.getDetails(moveId, moveName, "Normal")

                movesList.add(
                    BattleMove(
                        id = moveId,
                        name = details.name,
                        pp = pp,
                        maxPp = maxPp,
                        target = target,
                        disabled = disabled,
                        type = details.type
                    )
                )
            }

            val canMega = active?.get("canMegaEvolve")?.jsonPrimitive?.booleanOrNull ?: false
            val canTera = active?.get("canTerastallize")?.jsonPrimitive?.content != null ||
                    active?.get("canTerastallize")?.jsonPrimitive?.booleanOrNull == true
            val canDynamax = active?.get("canDynamax")?.jsonPrimitive?.booleanOrNull ?: false

            val side = root["side"]?.jsonObject
            val sideId = side?.get("id")?.jsonPrimitive?.content ?: "p1"
            val teamList = mutableListOf<BattlePokemon>()

            side?.get("pokemon")?.jsonArray?.forEach { pokeElem ->
                val pObj = pokeElem.jsonObject
                val ident = pObj["ident"]?.jsonPrimitive?.content ?: ""
                val details = pObj["details"]?.jsonPrimitive?.content ?: ""
                val condition = pObj["condition"]?.jsonPrimitive?.content ?: "100/100"
                val activeFlag = pObj["active"]?.jsonPrimitive?.booleanOrNull ?: false

                val item = pObj["item"]?.jsonPrimitive?.content ?: ""
                val ability = pObj["ability"]?.jsonPrimitive?.content ?: pObj["baseAbility"]?.jsonPrimitive?.content ?: ""
                val teraType = pObj["teraType"]?.jsonPrimitive?.content ?: ""

                val statsMap = mutableMapOf<String, Int>()
                pObj["stats"]?.jsonObject?.forEach { (k, v) ->
                    statsMap[k] = v.jsonPrimitive.intOrNull ?: 0
                }

                val hpParts = parseHpCondition(condition)
                val species = details.split(",").firstOrNull()?.trim() ?: ident

                val pokeMoves = mutableListOf<BattleMove>()
                pObj["moves"]?.jsonArray?.forEach { mName ->
                    val name = mName.jsonPrimitive.content
                    val moveId = name.lowercase().replace(Regex("[^a-z0-9]"), "")
                    val moveDetails = MoveDex.getDetails(moveId, name, "Normal")
                    pokeMoves.add(
                        BattleMove(
                            id = moveId,
                            name = moveDetails.name,
                            type = moveDetails.type
                        )
                    )
                }

                teamList.add(
                    BattlePokemon(
                        id = ident,
                        name = species,
                        species = species,
                        details = details,
                        hp = hpParts.first,
                        maxHp = hpParts.second,
                        status = hpParts.third,
                        fainted = hpParts.first <= 0,
                        item = item,
                        ability = ability,
                        teraType = teraType,
                        stats = statsMap,
                        moves = pokeMoves,
                        position = if (activeFlag) "active" else "benched"
                    )
                )
            }

            BattleRequestData(
                sideId = sideId,
                moves = movesList,
                switches = teamList.filter { !it.fainted && it.position != "active" },
                sideTeam = teamList,
                canMega = canMega,
                canTera = canTera,
                canDynamax = canDynamax
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Parses Showdown HP condition strings like "100/100", "45/100 brn", "0 fnt", "35/100 [from] item: Leftovers".
     * Returns Triple(currentHp, maxHp, statusString).
     */
    fun parseHpCondition(condition: String): Triple<Int, Int, String?> {
        val parts = condition.trim().split(" ")
        val hpPart = parts.getOrNull(0) ?: "100/100"

        val validStatuses = setOf("brn", "psn", "tox", "par", "slp", "frz", "fnt")
        val status = parts.drop(1).firstOrNull { validStatuses.contains(it.lowercase()) }

        if (hpPart == "0" || hpPart.startsWith("0/")) {
            return Triple(0, 100, status ?: "fnt")
        }

        val hpSplit = hpPart.split("/")
        val curHp = hpSplit.getOrNull(0)?.toIntOrNull() ?: 100
        val maxHp = hpSplit.getOrNull(1)?.toIntOrNull() ?: 100

        return Triple(curHp, maxHp, if (status == "fnt") null else status)
    }
}

data class UpdateSearchData(
    val searchingFormats: List<String>,
    val games: Map<String, String>
)

data class BattleRequestData(
    val sideId: String = "p1",
    val moves: List<BattleMove>,
    val switches: List<BattlePokemon>,
    val sideTeam: List<BattlePokemon>,
    val canMega: Boolean,
    val canTera: Boolean,
    val canDynamax: Boolean
)
