package com.kaikunxp.pokeshowdown.ui.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaikunxp.pokeshowdown.data.model.BattleState
import com.kaikunxp.pokeshowdown.data.model.ChallengeInfo
import com.kaikunxp.pokeshowdown.data.model.ChatMessage
import com.kaikunxp.pokeshowdown.data.model.ShowdownFormat
import com.kaikunxp.pokeshowdown.data.model.Team
import com.kaikunxp.pokeshowdown.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    user: User,
    formats: List<ShowdownFormat>,
    teams: List<Team>,
    isSearching: Boolean,
    chatMessages: List<ChatMessage>,
    activeBattleState: BattleState? = null,
    incomingChallenge: ChallengeInfo? = null,
    onResumeBattle: (() -> Unit)? = null,
    onAcceptChallenge: (String, String) -> Unit,
    onRejectChallenge: (String) -> Unit,
    onOpenAuthDialog: () -> Unit,
    onOpenTeambuilder: () -> Unit,
    onSearchBattle: (String, String) -> Unit,
    onCancelSearch: () -> Unit,
    onChallengeUser: (String, String, String) -> Unit,
    onSendChatMessage: (String) -> Unit
) {
    var selectedFormat by remember(formats) {
        mutableStateOf(
            formats.firstOrNull { it.id == "gen9randombattle" }
                ?: formats.firstOrNull()
                ?: ShowdownFormat("gen9randombattle", "[Gen 9] Random Battle")
        )
    }
    var selectedTeam by remember(teams) { mutableStateOf(teams.firstOrNull()) }

    var formatExpanded by remember { mutableStateOf(false) }
    var teamExpanded by remember { mutableStateOf(false) }

    var challengeUsername by remember { mutableStateOf("") }
    var chatInputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pokémon Showdown", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenTeambuilder) {
                        Icon(Icons.Default.Build, contentDescription = "Teambuilder")
                    }
                    IconButton(onClick = onOpenAuthDialog) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // User status bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "User: ",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (user.named) user.name else "${user.name} (Guest)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Battle Resume Banner
            if (activeBattleState != null && activeBattleState.roomId.isNotEmpty() && !activeBattleState.ended) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚔️ Active Battle in Progress",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "${activeBattleState.p1.name} vs ${activeBattleState.p2.name} (Turn ${activeBattleState.turn})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        if (onResumeBattle != null) {
                            Button(
                                onClick = onResumeBattle,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("Resume")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Battle Matchmaking Setup
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Battle Matchmaking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Format selector
                    ExposedDropdownMenuBox(
                        expanded = formatExpanded,
                        onExpandedChange = { formatExpanded = !formatExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFormat.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Format") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formatExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = formatExpanded,
                            onDismissRequest = { formatExpanded = false }
                        ) {
                            formats.forEach { fmt ->
                                DropdownMenuItem(
                                    text = { Text(fmt.name) },
                                    onClick = {
                                        selectedFormat = fmt
                                        formatExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Team selector
                    ExposedDropdownMenuBox(
                        expanded = teamExpanded,
                        onExpandedChange = { teamExpanded = !teamExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (selectedFormat.id.contains("random")) "Random Team" else (selectedTeam?.name ?: "Select Team"),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Team") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = teamExpanded) },
                            enabled = !selectedFormat.id.contains("random"),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = teamExpanded,
                            onDismissRequest = { teamExpanded = false }
                        ) {
                            teams.forEach { team ->
                                DropdownMenuItem(
                                    text = { Text(team.name) },
                                    onClick = {
                                        selectedTeam = team
                                        teamExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isSearching) {
                        Button(
                            onClick = onCancelSearch,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onError,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Searching... Cancel")
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                val packed = if (selectedFormat.id.contains("random")) "" else (selectedTeam?.packedString ?: "")
                                onSearchBattle(selectedFormat.id, packed)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Battle!", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Direct Challenge Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Direct Challenge",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = challengeUsername,
                            onValueChange = { challengeUsername = it },
                            placeholder = { Text("Username") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (challengeUsername.isNotBlank()) {
                                    val packed = if (selectedFormat.id.contains("random")) "" else (selectedTeam?.packedString ?: "")
                                    onChallengeUser(challengeUsername, selectedFormat.id, packed)
                                    challengeUsername = ""
                                }
                            },
                            enabled = challengeUsername.isNotBlank()
                        ) {
                            Text("Challenge")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Global Lobby Chat Feed
            Text(
                text = "Lobby Chat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(chatMessages.reversed()) { chat ->
                    Text(
                        text = "${chat.sender}: ${chat.text}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInputText,
                    onValueChange = { chatInputText = it },
                    placeholder = { Text("Chat in lobby...") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (chatInputText.isNotBlank()) {
                            onSendChatMessage(chatInputText)
                            chatInputText = ""
                        }
                    },
                    enabled = chatInputText.isNotBlank()
                ) {
                    Text("Send")
                }
            }
        }
    }

    // Incoming Challenge Modal
    if (incomingChallenge != null) {
        AlertDialog(
            onDismissRequest = { onRejectChallenge(incomingChallenge.challenger) },
            title = { Text("⚔️ Incoming Challenge!", fontWeight = FontWeight.Bold) },
            text = {
                Text("${incomingChallenge.challenger} has challenged you to a [${incomingChallenge.format}] battle!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val packed = if (selectedFormat.id.contains("random")) "" else (selectedTeam?.packedString ?: "")
                        onAcceptChallenge(incomingChallenge.challenger, packed)
                    }
                ) {
                    Text("Accept Battle")
                }
            },
            dismissButton = {
                TextButton(onClick = { onRejectChallenge(incomingChallenge.challenger) }) {
                    Text("Decline")
                }
            }
        )
    }
}
