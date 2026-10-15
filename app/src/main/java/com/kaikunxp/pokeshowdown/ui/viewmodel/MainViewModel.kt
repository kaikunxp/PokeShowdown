package com.kaikunxp.pokeshowdown.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.kaikunxp.pokeshowdown.data.model.BattleState
import com.kaikunxp.pokeshowdown.data.model.ChallengeInfo
import com.kaikunxp.pokeshowdown.data.model.ChatMessage
import com.kaikunxp.pokeshowdown.data.model.ShowdownFormat
import com.kaikunxp.pokeshowdown.data.model.Team
import com.kaikunxp.pokeshowdown.data.model.User
import com.kaikunxp.pokeshowdown.data.repository.ShowdownRepository
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(
    val repository: ShowdownRepository = ShowdownRepository()
) : ViewModel() {

    val user: StateFlow<User> = repository.user
    val formats: StateFlow<List<ShowdownFormat>> = repository.formats
    val battleState: StateFlow<BattleState> = repository.battleState
    val teams: StateFlow<List<Team>> = repository.teams
    val isSearchingBattle: StateFlow<Boolean> = repository.isSearchingBattle
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val incomingChallenge: StateFlow<ChallengeInfo?> = repository.incomingChallenge

    init {
        repository.connect()
    }

    fun loginGuest(username: String) {
        repository.loginGuest(username)
    }

    fun loginAccount(username: String, pass: String) {
        repository.loginAccount(username, pass)
    }

    fun searchBattle(formatId: String, teamPacked: String = "") {
        repository.searchBattle(formatId, teamPacked)
    }

    fun cancelSearchBattle() {
        repository.cancelSearchBattle()
    }

    fun challengeUser(username: String, formatId: String, teamPacked: String = "") {
        repository.challengeUser(username, formatId, teamPacked)
    }

    fun acceptChallenge(challenger: String, teamPacked: String = "") {
        repository.acceptChallenge(challenger, teamPacked)
    }

    fun rejectChallenge(challenger: String) {
        repository.rejectChallenge(challenger)
    }

    fun sendBattleMove(moveSlot: Int, mega: Boolean = false, tera: Boolean = false) {
        repository.sendBattleMove(moveSlot, mega, tera)
    }

    fun sendBattleSwitch(switchSlot: Int) {
        repository.sendBattleSwitch(switchSlot)
    }

    fun clearActiveAnimation() {
        repository.clearActiveAnimation()
    }

    fun sendChatMessage(room: String, message: String) {
        repository.sendChatMessage(room, message)
    }

    fun saveTeam(team: Team) {
        repository.saveTeam(team)
    }

    fun deleteTeam(teamId: String) {
        repository.deleteTeam(teamId)
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}
