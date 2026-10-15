package com.kaikunxp.pokeshowdown.ui.teambuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaikunxp.pokeshowdown.data.model.Team
import com.kaikunxp.pokeshowdown.ui.components.PokemonIcon
import com.kaikunxp.pokeshowdown.ui.components.TypeBadge
import com.kaikunxp.pokeshowdown.util.CustomTeamMember
import com.kaikunxp.pokeshowdown.util.Pokedex
import com.kaikunxp.pokeshowdown.util.TeamPacker
import java.util.UUID

val availablePokemonList = listOf(
    "Pikachu", "Charizard", "Venusaur", "Blastoise", "Great Tusk", "Iron Valiant",
    "Dragapult", "Kingambit", "Gholdengo", "Roaring Moon", "Gliscor", "Heatran",
    "Zapdos", "Clefable", "Dragonite", "Samurott-Hisui", "Iron Moth", "Iron Treads",
    "Walking Wake", "Archaludon", "Volcarona", "Meowscarada", "Ceruledge", "Skeledirge",
    "Quaquaval", "Dondozo", "Clodsire", "Corviknight", "Toxapex", "Ferrothorn",
    "Excadrill", "Kyogre", "Groudon", "Rayquaza", "Espeon", "Lokix", "Sunflora", "Mandibuzz"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeambuilderScreen(
    teams: List<Team>,
    onSaveTeam: (Team) -> Unit,
    onDeleteTeam: (String) -> Unit,
    onBack: () -> Unit
) {
    var showCreateTeamDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teambuilder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateTeamDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Build Team")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "My Teams",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (teams.isEmpty()) {
                Text(
                    text = "No saved teams. Tap + to build a team visually!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(teams) { team ->
                        TeamCardItem(
                            team = team,
                            onDelete = { onDeleteTeam(team.id) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateTeamDialog) {
        VisualTeamBuilderDialog(
            onDismiss = { showCreateTeamDialog = false },
            onSave = { name, format, members ->
                val packed = TeamPacker.packTeam(members)
                val newTeam = Team(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    format = format,
                    packedString = packed
                )
                onSaveTeam(newTeam)
                showCreateTeamDialog = false
            }
        )
    }
}

@Composable
fun TeamCardItem(
    team: Team,
    onDelete: () -> Unit
) {
    // Extract pokemon names from packed string
    val pokemonNames = team.packedString.split("]")
        .mapNotNull { it.split("|").firstOrNull()?.trim() }
        .filter { it.isNotEmpty() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = team.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Format: ${team.format}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row of Pokemon Sprite Icons in team
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pokemonNames.forEach { species ->
                    PokemonIcon(species = species, size = 36.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualTeamBuilderDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, format: String, members: List<CustomTeamMember>) -> Unit
) {
    var teamName by remember { mutableStateOf("My Team") }
    var formatId by remember { mutableStateOf("gen9ou") }
    var formatExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val selectedMembers = remember { mutableStateListOf<CustomTeamMember>() }

    val filteredPokemon = availablePokemonList.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Visual Team Builder", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
            ) {
                // Team Name
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Format Selector
                ExposedDropdownMenuBox(
                    expanded = formatExpanded,
                    onExpandedChange = { formatExpanded = !formatExpanded }
                ) {
                    OutlinedTextField(
                        value = formatId,
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
                        listOf("gen9ou", "gen9ubers", "gen9uu", "gen9monotype", "gen9randombattle").forEach { fmt ->
                            DropdownMenuItem(
                                text = { Text(fmt) },
                                onClick = {
                                    formatId = fmt
                                    formatExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Selected Team (${selectedMembers.size}/6):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Selected Pokémon 2x3 Grid of Rounded Rectangles
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(6) { index ->
                        val member = selectedMembers.getOrNull(index)
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (member != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            if (member != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    ) {
                                        PokemonIcon(
                                            species = member.species,
                                            size = 32.dp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = member.species,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .clickable { selectedMembers.removeAt(index) }
                                            .align(Alignment.TopEnd),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+ Slot ${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Pokémon
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Pokémon...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pokémon Selector Grid with rounded rectangle cards
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredPokemon) { pokemonName ->
                        val isSelected = selectedMembers.any { it.species.equals(pokemonName, ignoreCase = true) }
                        val types = Pokedex.getTypesForSpecies(pokemonName)

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = selectedMembers.size < 6 && !isSelected) {
                                    selectedMembers.add(
                                        CustomTeamMember(
                                            species = pokemonName,
                                            moves = TeamPacker.getDefaultMoves(pokemonName)
                                        )
                                    )
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                PokemonIcon(species = pokemonName, size = 32.dp)
                                Text(
                                    text = pokemonName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                TypeBadge(type = types.firstOrNull() ?: "Normal")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (teamName.isNotBlank() && selectedMembers.isNotEmpty()) {
                        onSave(teamName, formatId, selectedMembers.toList())
                    }
                },
                enabled = teamName.isNotBlank() && selectedMembers.isNotEmpty()
            ) {
                Text("Save Team")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
