package com.kaikunxp.pokeshowdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaikunxp.pokeshowdown.ui.auth.AuthDialog
import com.kaikunxp.pokeshowdown.ui.battle.BattleScreen
import com.kaikunxp.pokeshowdown.ui.lobby.LobbyScreen
import com.kaikunxp.pokeshowdown.ui.teambuilder.TeambuilderScreen
import com.kaikunxp.pokeshowdown.ui.theme.PokeShowdownTheme
import com.kaikunxp.pokeshowdown.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeShowdownTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokeShowdownApp()
                }
            }
        }
    }
}

@Composable
fun PokeShowdownApp(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    val user by viewModel.user.collectAsState()
    val formats by viewModel.formats.collectAsState()
    val teams by viewModel.teams.collectAsState()
    val battleState by viewModel.battleState.collectAsState()
    val isSearching by viewModel.isSearchingBattle.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val incomingChallenge by viewModel.incomingChallenge.collectAsState()

    var showAuthDialog by remember { mutableStateOf(false) }

    // Auto-navigate to BattleScreen when entering a new battle room
    LaunchedEffect(battleState.roomId) {
        if (battleState.roomId.isNotEmpty() && !battleState.ended) {
            navController.navigate("battle") {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "lobby"
    ) {
        composable("lobby") {
            LobbyScreen(
                user = user,
                formats = formats,
                teams = teams,
                isSearching = isSearching,
                chatMessages = chatMessages,
                activeBattleState = battleState,
                incomingChallenge = incomingChallenge,
                onResumeBattle = {
                    navController.navigate("battle") {
                        launchSingleTop = true
                    }
                },
                onAcceptChallenge = { challenger, teamPacked ->
                    viewModel.acceptChallenge(challenger, teamPacked)
                },
                onRejectChallenge = { challenger ->
                    viewModel.rejectChallenge(challenger)
                },
                onOpenAuthDialog = { showAuthDialog = true },
                onOpenTeambuilder = { navController.navigate("teambuilder") },
                onSearchBattle = { formatId, teamPacked ->
                    viewModel.searchBattle(formatId, teamPacked)
                },
                onCancelSearch = { viewModel.cancelSearchBattle() },
                onChallengeUser = { username, formatId, teamPacked ->
                    viewModel.challengeUser(username, formatId, teamPacked)
                },
                onSendChatMessage = { text ->
                    viewModel.sendChatMessage("lobby", text)
                }
            )
        }

        composable("battle") {
            BattleScreen(
                battleState = battleState,
                onSendMove = { moveSlot, mega, tera ->
                    viewModel.sendBattleMove(moveSlot, mega, tera)
                },
                onSendSwitch = { switchSlot ->
                    viewModel.sendBattleSwitch(switchSlot)
                },
                onClearAnimation = {
                    viewModel.clearActiveAnimation()
                },
                onLeaveBattle = {
                    navController.popBackStack("lobby", inclusive = false)
                }
            )
        }

        composable("teambuilder") {
            TeambuilderScreen(
                teams = teams,
                onSaveTeam = { team -> viewModel.saveTeam(team) },
                onDeleteTeam = { teamId -> viewModel.deleteTeam(teamId) },
                onBack = { navController.popBackStack() }
            )
        }
    }

    if (showAuthDialog) {
        AuthDialog(
            onDismiss = { showAuthDialog = false },
            onGuestLogin = { username -> viewModel.loginGuest(username) },
            onAccountLogin = { username, password -> viewModel.loginAccount(username, password) }
        )
    }
}
