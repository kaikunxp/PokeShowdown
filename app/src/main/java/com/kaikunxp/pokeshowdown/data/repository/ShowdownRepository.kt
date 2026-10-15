package com.kaikunxp.pokeshowdown.data.repository

import android.util.Log
import com.kaikunxp.pokeshowdown.data.model.ActiveAttackAnimation
import com.kaikunxp.pokeshowdown.data.model.BattlePokemon
import com.kaikunxp.pokeshowdown.data.model.BattleState
import com.kaikunxp.pokeshowdown.data.model.ChallengeInfo
import com.kaikunxp.pokeshowdown.data.model.ChatMessage
import com.kaikunxp.pokeshowdown.data.model.ShowdownFormat
import com.kaikunxp.pokeshowdown.data.model.Team
import com.kaikunxp.pokeshowdown.data.model.User
import com.kaikunxp.pokeshowdown.data.remote.ProtocolParser
import com.kaikunxp.pokeshowdown.data.remote.ShowdownApiService
import com.kaikunxp.pokeshowdown.data.remote.ShowdownWebSocketClient
import com.kaikunxp.pokeshowdown.util.MoveDex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShowdownRepository(
    val webSocketClient: ShowdownWebSocketClient = ShowdownWebSocketClient(),
    val apiService: ShowdownApiService = ShowdownApiService()
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _user = MutableStateFlow(User(name = "Trainer${(1000..9999).random()}"))
    val user: StateFlow<User> = _user.asStateFlow()

    private val _formats = MutableStateFlow<List<ShowdownFormat>>(
        listOf(
            ShowdownFormat(id = "gen9randombattle", name = "[Gen 9] Random Battle", section = "Random Battles"),
            ShowdownFormat(id = "gen9ou", name = "[Gen 9] OU", section = "S/V Singles"),
            ShowdownFormat(id = "gen9ubers", name = "[Gen 9] Ubers", section = "S/V Singles"),
            ShowdownFormat(id = "gen9uu", name = "[Gen 9] UU", section = "S/V Singles"),
            ShowdownFormat(id = "gen9monotype", name = "[Gen 9] Monotype", section = "S/V Singles")
        )
    )
    val formats: StateFlow<List<ShowdownFormat>> = _formats.asStateFlow()

    private val _battleState = MutableStateFlow(BattleState())
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _teams = MutableStateFlow<List<Team>>(
        listOf(
            Team(
                id = "1",
                name = "Sample Gen 9 OU",
                format = "gen9ou",
                packedString = "Pikachu||lightball|lightningrod|thunderbolt,voltswitch,grassknot,surf|Timid|,252,,,252||||"
            )
        )
    )
    val teams: StateFlow<List<Team>> = _teams.asStateFlow()

    private val _isSearchingBattle = MutableStateFlow(false)
    val isSearchingBattle: StateFlow<Boolean> = _isSearchingBattle.asStateFlow()

    private val _incomingChallenge = MutableStateFlow<ChallengeInfo?>(null)
    val incomingChallenge: StateFlow<ChallengeInfo?> = _incomingChallenge.asStateFlow()

    private var challstr: String = ""

    init {
        scope.launch {
            apiService.loadShowdownDexData()
        }
        scope.launch {
            webSocketClient.messages.collect { msg ->
                handleIncomingMessage(msg)
            }
        }
    }

    fun connect() {
        webSocketClient.connect()
    }

    fun disconnect() {
        webSocketClient.disconnect()
    }

    private fun handleIncomingMessage(msg: com.kaikunxp.pokeshowdown.data.model.ShowdownMessage) {
        Log.d("ShowdownRepo", "Cmd: ${msg.command}, Room: ${msg.room}, Args: ${msg.args}")
        when (msg.command) {
            "challstr" -> {
                challstr = msg.args.joinToString("|")
                Log.d("ShowdownRepo", "Received challstr: $challstr")
                if (!_user.value.named && challstr.isNotEmpty()) {
                    loginGuest(_user.value.name)
                }
            }

            "updateuser" -> {
                val username = msg.args.getOrNull(0)?.trim() ?: ""
                val named = msg.args.getOrNull(1) == "1"
                val avatar = msg.args.getOrNull(2) ?: "1"
                _user.value = _user.value.copy(
                    name = username,
                    named = named,
                    avatar = avatar
                )
                Log.d("ShowdownRepo", "User updated: '$username', named=$named")
            }

            "updatesearch" -> {
                val jsonStr = msg.args.getOrNull(0) ?: ""
                Log.d("ShowdownRepo", "updatesearch json: $jsonStr")
                val searchData = ProtocolParser.parseUpdateSearch(jsonStr)
                if (searchData != null) {
                    if (searchData.searchingFormats.isEmpty() && searchData.games.isEmpty()) {
                        _isSearchingBattle.value = false
                    } else if (searchData.searchingFormats.isNotEmpty()) {
                        _isSearchingBattle.value = true
                    }

                    // Auto-join any battle games created by the server
                    searchData.games.forEach { (roomId, _) ->
                        Log.d("ShowdownRepo", "Joining battle game room: $roomId")
                        webSocketClient.send("", "/join $roomId")
                    }
                }
            }

            "pm" -> {
                val rawSender = msg.args.getOrNull(0)?.trim() ?: ""
                val receiver = msg.args.getOrNull(1)?.trim() ?: ""
                val pmText = msg.args.getOrNull(2) ?: ""

                val cleanSender = rawSender.replace(Regex("^[^a-zA-Z0-9]+"), "")
                val cleanUser = _user.value.name.trim().replace(Regex("^[^a-zA-Z0-9]+"), "")

                Log.d("ShowdownRepo", "PM from '$rawSender' (clean: '$cleanSender') to '$receiver': $pmText")

                if (pmText.startsWith("/challenge")) {
                    val parts = pmText.split(" ")
                    val format = parts.getOrNull(1)?.trim() ?: "gen9randombattle"

                    // Ignore if sender is yourself or echoed PM
                    if (!cleanSender.equals(cleanUser, ignoreCase = true) && cleanSender.isNotEmpty()) {
                        _incomingChallenge.value = ChallengeInfo(challenger = cleanSender, format = format)
                        _chatMessages.value = _chatMessages.value + ChatMessage(
                            room = "lobby",
                            sender = "SYSTEM",
                            text = "⚔️ $cleanSender challenged you to a $format battle!"
                        )
                    }
                } else if (pmText.startsWith("/nonchal") || pmText.contains("cancel")) {
                    // Challenge was cancelled, accepted, or cleared
                    _incomingChallenge.value = null
                } else if (pmText.startsWith("/error")) {
                    val errText = pmText.substringAfter("/error ").trim()
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        room = "lobby",
                        sender = "SYSTEM",
                        text = "⚠️ Server Error: $errText"
                    )
                } else if (pmText.contains("reject")) {
                    _incomingChallenge.value = null
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        room = "lobby",
                        sender = "SYSTEM",
                        text = "⚠️ Challenge rejected by $rawSender"
                    )
                }
            }

            "popup" -> {
                val popupText = msg.args.joinToString("|").replace("||", "\n")
                Log.d("ShowdownRepo", "Popup received: $popupText")
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    room = msg.room,
                    sender = "SYSTEM",
                    text = "⚠️ $popupText"
                )
            }

            "formats" -> {
                val parsed = ProtocolParser.parseFormats(msg.args)
                if (parsed.isNotEmpty()) {
                    val gen9Random = parsed.firstOrNull { it.id == "gen9randombattle" }
                    val rest = parsed.filter { it.id != "gen9randombattle" }
                    val reordered = if (gen9Random != null) listOf(gen9Random) + rest else parsed
                    _formats.value = reordered
                }
            }

            "init" -> {
                if (msg.args.getOrNull(0) == "battle") {
                    _battleState.value = BattleState(roomId = msg.room)
                    _isSearchingBattle.value = false
                    Log.d("ShowdownRepo", "Initialized battle room: ${msg.room}")
                }
            }

            "title" -> {
                val title = msg.args.joinToString(" ")
                val parts = title.split(" vs. ")
                if (parts.size == 2) {
                    _battleState.value = _battleState.value.copy(
                        p1 = _battleState.value.p1.copy(name = parts[0]),
                        p2 = _battleState.value.p2.copy(name = parts[1])
                    )
                }
            }

            "player" -> {
                val sideId = msg.args.getOrNull(0) ?: ""
                val name = msg.args.getOrNull(1) ?: ""
                if (sideId == "p1") {
                    _battleState.value = _battleState.value.copy(p1 = _battleState.value.p1.copy(name = name))
                    if (name.equals(_user.value.name, ignoreCase = true)) {
                        _battleState.value = _battleState.value.copy(mySideId = "p1")
                    }
                } else if (sideId == "p2") {
                    _battleState.value = _battleState.value.copy(p2 = _battleState.value.p2.copy(name = name))
                    if (name.equals(_user.value.name, ignoreCase = true)) {
                        _battleState.value = _battleState.value.copy(mySideId = "p2")
                    }
                }
            }

            "request" -> {
                val jsonStr = msg.args.getOrNull(0) ?: ""
                val req = ProtocolParser.parseBattleRequest(jsonStr)
                if (req != null) {
                    val currentBs = _battleState.value
                    val mySideId = req.sideId.ifEmpty { currentBs.mySideId }
                    val isP1 = mySideId == "p1"

                    val activeInTeam = req.sideTeam.firstOrNull { it.position == "active" } ?: req.sideTeam.firstOrNull()

                    val currentMySide = if (isP1) currentBs.p1 else currentBs.p2
                    val updatedMySide = currentMySide.copy(
                        active = activeInTeam ?: currentMySide.active,
                        team = req.sideTeam
                    )

                    _battleState.value = currentBs.copy(
                        mySideId = mySideId,
                        p1 = if (isP1) updatedMySide else currentBs.p1,
                        p2 = if (!isP1) updatedMySide else currentBs.p2,
                        availableMoves = req.moves,
                        availableSwitches = req.switches,
                        canMegaEvolve = req.canMega,
                        canTerastallize = req.canTera,
                        canDynamax = req.canDynamax,
                        waitingForChoice = true
                    )
                }
            }

            "turn" -> {
                val turnNum = msg.args.getOrNull(0)?.toIntOrNull() ?: (_battleState.value.turn + 1)
                _battleState.value = _battleState.value.copy(
                    turn = turnNum,
                    waitingForChoice = true,
                    logs = _battleState.value.logs + "--- Turn $turnNum ---"
                )
            }

            "switch", "drag" -> {
                val positionAndName = msg.args.getOrNull(0) ?: ""
                val details = msg.args.getOrNull(1) ?: ""
                val hpCond = msg.args.getOrNull(2) ?: "100/100"

                val isP1 = positionAndName.startsWith("p1")
                val parts = positionAndName.split(": ")
                val pos = parts.getOrNull(0) ?: ""
                val pName = parts.getOrNull(1) ?: details.split(",").firstOrNull() ?: "Pokemon"
                val species = details.split(",").firstOrNull()?.trim() ?: pName

                val hpParsed = ProtocolParser.parseHpCondition(hpCond)

                val bs = _battleState.value
                val targetSide = if (isP1) bs.p1 else bs.p2
                val existingInTeam = targetSide.team.firstOrNull {
                    it.species.equals(species, ignoreCase = true) || it.name.equals(pName, ignoreCase = true)
                }

                val activePoke = BattlePokemon(
                    id = pos,
                    name = pName,
                    species = species,
                    details = details,
                    hp = hpParsed.first,
                    maxHp = hpParsed.second,
                    status = hpParsed.third,
                    position = pos,
                    item = existingInTeam?.item.orEmpty(),
                    ability = existingInTeam?.ability.orEmpty(),
                    teraType = existingInTeam?.teraType.orEmpty(),
                    stats = existingInTeam?.stats ?: emptyMap(),
                    moves = existingInTeam?.moves ?: emptyList()
                )

                _battleState.value = if (isP1) {
                    bs.copy(
                        p1 = bs.p1.copy(active = activePoke),
                        logs = bs.logs + "${bs.p1.name} switched in $pName!"
                    )
                } else {
                    bs.copy(
                        p2 = bs.p2.copy(active = activePoke),
                        logs = bs.logs + "${bs.p2.name} switched in $pName!"
                    )
                }
            }

            "move" -> {
                val userPosName = msg.args.getOrNull(0) ?: ""
                val moveName = msg.args.getOrNull(1) ?: "Move"
                val targetPosName = msg.args.getOrNull(2) ?: ""

                val userName = userPosName.split(": ").getOrNull(1) ?: userPosName
                val targetName = targetPosName.split(": ").getOrNull(1) ?: targetPosName

                val attackerPos = if (userPosName.startsWith("p1")) "p1" else "p2"
                val targetPos = if (targetPosName.startsWith("p1")) "p1" else "p2"

                val details = MoveDex.getDetails(moveName.lowercase(), moveName, "Normal")

                val logText = if (targetName.isNotEmpty()) "$userName used $moveName on $targetName!" else "$userName used $moveName!"
                _battleState.value = _battleState.value.copy(
                    activeAnimation = ActiveAttackAnimation(
                        attackerPos = attackerPos,
                        targetPos = targetPos,
                        moveName = moveName,
                        moveType = details.type
                    ),
                    logs = _battleState.value.logs + logText
                )
            }

            "-damage", "-heal" -> {
                val posName = msg.args.getOrNull(0) ?: ""
                val hpCond = msg.args.getOrNull(1) ?: ""

                val isP1 = posName.startsWith("p1")
                val hpParsed = ProtocolParser.parseHpCondition(hpCond)

                val bs = _battleState.value
                val targetSide = if (isP1) bs.p1 else bs.p2
                val active = targetSide.active

                if (active != null) {
                    val updatedActive = active.copy(
                        hp = hpParsed.first,
                        maxHp = hpParsed.second,
                        status = hpParsed.third,
                        fainted = hpParsed.first <= 0
                    )
                    _battleState.value = if (isP1) {
                        bs.copy(p1 = bs.p1.copy(active = updatedActive))
                    } else {
                        bs.copy(p2 = bs.p2.copy(active = updatedActive))
                    }
                }
            }

            "win" -> {
                val winner = msg.args.getOrNull(0) ?: ""
                _battleState.value = _battleState.value.copy(
                    winner = winner,
                    ended = true,
                    waitingForChoice = false,
                    logs = _battleState.value.logs + "★ $winner won the battle!"
                )
            }

            "c", "chat" -> {
                val sender = msg.args.getOrNull(0) ?: ""
                val text = msg.args.getOrNull(1) ?: ""
                if (sender.isNotEmpty() && text.isNotEmpty()) {
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        room = msg.room,
                        sender = sender,
                        text = text
                    )
                }
            }
        }
    }

    fun clearActiveAnimation() {
        _battleState.value = _battleState.value.copy(activeAnimation = null)
    }

    fun loginGuest(rawUsername: String) {
        var username = rawUsername.trim()
        if (username.isEmpty() || username.lowercase().startsWith("guest")) {
            username = "Trainer${(1000..9999).random()}"
        }
        _user.value = _user.value.copy(name = username)
        Log.d("ShowdownRepo", "loginGuest clean username: $username")

        if (challstr.isNotEmpty()) {
            scope.launch {
                val res = apiService.getGuestAssertion(username, challstr)
                res.onSuccess { assertion ->
                    Log.d("ShowdownRepo", "Sending authenticated trn with assertion for $username")
                    webSocketClient.send("", "/trn $username,0,$assertion")
                }.onFailure { err ->
                    Log.e("ShowdownRepo", "Failed to get guest assertion: ${err.message}")
                    webSocketClient.send("", "/trn $username,0")
                }
            }
        } else {
            webSocketClient.send("", "/trn $username,0")
        }
    }

    fun loginAccount(username: String, pass: String) {
        Log.d("ShowdownRepo", "loginAccount: $username")
        scope.launch {
            val res = apiService.loginAccount(username, pass, challstr)
            res.onSuccess { assertion ->
                Log.d("ShowdownRepo", "Account login success")
                webSocketClient.send("", "/trn $username,0,$assertion")
            }
        }
    }

    fun searchBattle(formatId: String, teamPacked: String = "") {
        Log.d("ShowdownRepo", "searchBattle: format=$formatId, team=$teamPacked")
        _isSearchingBattle.value = true
        if (teamPacked.isNotEmpty()) {
            webSocketClient.send("", "/utm $teamPacked")
        }
        webSocketClient.send("", "/search $formatId")
    }

    fun cancelSearchBattle() {
        Log.d("ShowdownRepo", "cancelSearchBattle")
        _isSearchingBattle.value = false
        webSocketClient.send("", "/cancelsearch")
    }

    fun challengeUser(targetUser: String, formatId: String, teamPacked: String = "") {
        val cleanTarget = targetUser.trim()
        if (cleanTarget.isEmpty()) return
        Log.d("ShowdownRepo", "challengeUser: '$cleanTarget', format=$formatId")
        if (teamPacked.isNotEmpty()) {
            webSocketClient.send("", "/utm $teamPacked")
        }
        webSocketClient.send("", "/challenge $cleanTarget, $formatId")
        _chatMessages.value = _chatMessages.value + ChatMessage(
            room = "lobby",
            sender = "SYSTEM",
            text = "⚔️ Challenged $cleanTarget to a $formatId battle!"
        )
    }

    fun acceptChallenge(challenger: String, teamPacked: String = "") {
        Log.d("ShowdownRepo", "acceptChallenge from '$challenger'")
        if (teamPacked.isNotEmpty()) {
            webSocketClient.send("", "/utm $teamPacked")
        }
        webSocketClient.send("", "/accept $challenger")
        _incomingChallenge.value = null
    }

    fun rejectChallenge(challenger: String) {
        Log.d("ShowdownRepo", "rejectChallenge from '$challenger'")
        webSocketClient.send("", "/reject $challenger")
        _incomingChallenge.value = null
    }

    fun sendBattleMove(moveSlot: Int, mega: Boolean = false, tera: Boolean = false) {
        val bs = _battleState.value
        if (bs.roomId.isEmpty()) return

        var cmd = "/choose move $moveSlot"
        if (mega) cmd += " mega"
        if (tera) cmd += " terastallize"

        webSocketClient.send(bs.roomId, cmd)
        _battleState.value = bs.copy(waitingForChoice = false)
    }

    fun sendBattleSwitch(switchSlot: Int) {
        val bs = _battleState.value
        if (bs.roomId.isEmpty()) return

        webSocketClient.send(bs.roomId, "/choose switch $switchSlot")
        _battleState.value = bs.copy(waitingForChoice = false)
    }

    fun sendChatMessage(room: String, message: String) {
        webSocketClient.send(room, message)
    }

    fun saveTeam(team: Team) {
        val current = _teams.value.toMutableList()
        val index = current.indexOfFirst { it.id == team.id }
        if (index >= 0) {
            current[index] = team
        } else {
            current.add(team)
        }
        _teams.value = current
    }

    fun deleteTeam(teamId: String) {
        _teams.value = _teams.value.filter { it.id != teamId }
    }
}
