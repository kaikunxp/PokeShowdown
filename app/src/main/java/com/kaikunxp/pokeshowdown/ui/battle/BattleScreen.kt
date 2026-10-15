package com.kaikunxp.pokeshowdown.ui.battle

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaikunxp.pokeshowdown.data.model.BattleMove
import com.kaikunxp.pokeshowdown.data.model.BattlePokemon
import com.kaikunxp.pokeshowdown.data.model.BattleState
import com.kaikunxp.pokeshowdown.ui.components.PokemonIcon
import com.kaikunxp.pokeshowdown.ui.components.PokemonSprite
import com.kaikunxp.pokeshowdown.ui.components.TypeBadge
import com.kaikunxp.pokeshowdown.ui.components.getTypeColor
import com.kaikunxp.pokeshowdown.util.MoveDex
import com.kaikunxp.pokeshowdown.util.Pokedex
import com.kaikunxp.pokeshowdown.util.TypeChart

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BattleScreen(
    battleState: BattleState,
    onSendMove: (Int, Boolean, Boolean) -> Unit,
    onSendSwitch: (Int) -> Unit,
    onClearAnimation: () -> Unit,
    onLeaveBattle: () -> Unit
) {
    var megaChecked by remember { mutableStateOf(false) }
    var teraChecked by remember { mutableStateOf(false) }

    var inspectedMove by remember { mutableStateOf<BattleMove?>(null) }
    var inspectedPokemon by remember { mutableStateOf<BattlePokemon?>(null) }

    val opponentSide = battleState.opponentSide
    val mySide = battleState.mySide

    val opponentActive = opponentSide.active ?: BattlePokemon(name = "Opponent", species = "pikachu")
    val myActive = mySide.active ?: BattlePokemon(name = "My Pokemon", species = "charizard")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${mySide.name} vs ${opponentSide.name} (Turn ${battleState.turn})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onLeaveBattle) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Leave Battle")
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
        ) {
            // 2D Battle Field Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            ) {
                // Dynamic Battle Arena Background (Day/Sunset/Night Transitions + Drifting Clouds + 3D Turf Bases)
                DynamicBattleArenaBackground(
                    turn = battleState.turn,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Player HP (Top Left)
                    PokemonHpBarCard(
                        pokemon = myActive,
                        playerName = mySide.name,
                        onClick = { inspectedPokemon = myActive },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 12.dp, start = 12.dp)
                    )

                    // Opponent Pokémon (Top Right - Floating on P2 platform)
                    PokemonSprite(
                        species = opponentActive.species.ifEmpty { "pikachu" },
                        isBack = false,
                        size = 110.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 36.dp, end = 24.dp)
                            .clickable { inspectedPokemon = opponentActive }
                    )

                    // Player's Pokémon (Bottom Left - Floating on P1 platform)
                    PokemonSprite(
                        species = myActive.species.ifEmpty { "charizard" },
                        isBack = true,
                        size = 130.dp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 24.dp, start = 24.dp)
                            .clickable { inspectedPokemon = myActive }
                    )

                    // Opponent HP (Bottom Right)
                    PokemonHpBarCard(
                        pokemon = opponentActive,
                        playerName = opponentSide.name,
                        onClick = { inspectedPokemon = opponentActive },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 12.dp, end = 12.dp)
                    )

                    // Attack Animation Overlay
                    battleState.activeAnimation?.let { anim ->
                        AttackAnimationOverlay(
                            animation = anim,
                            isAttackerP1 = anim.attackerPos == "p1",
                            onAnimationEnd = onClearAnimation,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Battle Turn Logs & Events Feed
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    reverseLayout = true
                ) {
                    items(battleState.logs.reversed()) { logLine ->
                        Text(
                            text = logLine,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }
            }

            // Action Selection Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (battleState.waitingForChoice) "Select move or switch:" else "Waiting for opponent...",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "Hold for info",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mega / Terastallize Toggles
                    if (battleState.canMegaEvolve || battleState.canTerastallize) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (battleState.canMegaEvolve) {
                                Checkbox(
                                    checked = megaChecked,
                                    onCheckedChange = { megaChecked = it }
                                )
                                Text("Mega Evolve", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            if (battleState.canTerastallize) {
                                Checkbox(
                                    checked = teraChecked,
                                    onCheckedChange = { teraChecked = it }
                                )
                                Text("Terastallize", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Move Grid (4 move buttons with tap + long-press)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val moves = battleState.availableMoves.ifEmpty {
                            listOf(
                                BattleMove("thunderbolt", "Thunderbolt", 15, 15, type = "Electric"),
                                BattleMove("voltswitch", "Volt Switch", 20, 20, type = "Electric"),
                                BattleMove("flamethrower", "Flamethrower", 15, 15, type = "Fire"),
                                BattleMove("surf", "Surf", 15, 15, type = "Water")
                            )
                        }

                        itemsIndexed(moves) { index, move ->
                            val enabled = battleState.waitingForChoice && !move.disabled
                            val btnColor = getTypeColor(move.type)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (enabled) btnColor else btnColor.copy(alpha = 0.4f))
                                    .combinedClickable(
                                        enabled = enabled,
                                        onClick = {
                                            onSendMove(index + 1, megaChecked, teraChecked)
                                        },
                                        onLongClick = {
                                            inspectedMove = move
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = move.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "PP ${move.pp}/${move.maxPp}",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Switch Benched Pokémon Grid of Rounded Rectangles
                    Text("Switch Pokémon (Hold for stats):", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))

                    val switches = battleState.availableSwitches.ifEmpty {
                        listOf(
                            BattlePokemon(name = "Pikachu", species = "pikachu", hp = 100, maxHp = 100),
                            BattlePokemon(name = "Blastoise", species = "blastoise", hp = 100, maxHp = 100)
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (switches.size > 3) 104.dp else 50.dp)
                    ) {
                        itemsIndexed(switches) { idx, poke ->
                            val enabled = battleState.waitingForChoice && !poke.fainted
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .combinedClickable(
                                        onClick = {
                                            if (enabled) onSendSwitch(idx + 1)
                                        },
                                        onLongClick = {
                                            inspectedPokemon = poke
                                        }
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PokemonIcon(
                                        species = poke.species.ifEmpty { poke.name },
                                        size = 30.dp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = poke.name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (poke.maxHp == 100) "${poke.hp}% HP" else "${poke.hp}/${poke.maxHp} HP",
                                            fontSize = 8.sp,
                                            color = if (poke.fainted) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Pokémon Tooltip / Inspection Dialog (on long-press)
    inspectedPokemon?.let { poke ->
        PokemonDetailDialog(
            pokemon = poke,
            onDismiss = { inspectedPokemon = null }
        )
    }

    // Move Inspection Dialog (on long-press)
    inspectedMove?.let { move ->
        val details = MoveDex.getDetails(move.id, move.name, move.type)
        val opponentSpecies = opponentActive.species.ifEmpty { opponentActive.name }
        val opponentTypes = Pokedex.getTypesForSpecies(opponentSpecies)

        val multiplier = TypeChart.getCombinedMultiplier(details.type, opponentTypes)
        val effectivenessLabel = TypeChart.getEffectivenessLabel(multiplier)

        AlertDialog(
            onDismissRequest = { inspectedMove = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = details.name, fontWeight = FontWeight.Bold)
                    TypeBadge(type = details.type)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Category: ${details.category}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Power: ${if (details.basePower > 0) details.basePower else "—"}", fontSize = 12.sp)
                        Text("Acc: ${if (details.accuracy > 0) "${details.accuracy}%" else "—"}", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "vs ${opponentActive.name} (${opponentTypes.joinToString("/")}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    multiplier >= 2f -> Color(0xFF2E7D32)
                                    multiplier == 1f -> Color(0xFF616161)
                                    multiplier > 0f -> Color(0xFFC62828)
                                    else -> Color(0xFF37474F)
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = effectivenessLabel,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = details.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { inspectedMove = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Victory / Defeat Modal
    if (battleState.ended && battleState.winner != null) {
        AlertDialog(
            onDismissRequest = onLeaveBattle,
            title = {
                Text(
                    text = if (battleState.winner.equals(mySide.name, ignoreCase = true)) "Victory!" else "Battle Finished",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("${battleState.winner} won the battle!")
            },
            confirmButton = {
                Button(onClick = onLeaveBattle) {
                    Text("Return to Lobby")
                }
            }
        )
    }
}

@Composable
fun PokemonDetailDialog(
    pokemon: BattlePokemon,
    onDismiss: () -> Unit
) {
    val types = Pokedex.getTypesForSpecies(pokemon.species.ifEmpty { pokemon.name })

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = pokemon.name, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    types.forEach { t ->
                        TypeBadge(type = t)
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PokemonSprite(
                        species = pokemon.species.ifEmpty { pokemon.name },
                        size = 80.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (pokemon.maxHp == 100) "HP: ${pokemon.hp}%" else "HP: ${pokemon.hp}/${pokemon.maxHp}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (pokemon.ability.orEmpty().isNotEmpty()) {
                            Text("Ability: ${pokemon.ability}", fontSize = 12.sp)
                        }
                        if (pokemon.item.orEmpty().isNotEmpty()) {
                            Text("Item: ${pokemon.item}", fontSize = 12.sp)
                        }
                        if (pokemon.teraType.orEmpty().isNotEmpty()) {
                            Text("Tera Type: ${pokemon.teraType}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Grid
                if (pokemon.stats.isNotEmpty()) {
                    Text("Battle Stats:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Atk", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${pokemon.stats["atk"] ?: "—"}", fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Def", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${pokemon.stats["def"] ?: "—"}", fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SpA", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${pokemon.stats["spa"] ?: "—"}", fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SpD", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${pokemon.stats["spd"] ?: "—"}", fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Spe", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("${pokemon.stats["spe"] ?: "—"}", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Known Moves List
                if (pokemon.moves.isNotEmpty()) {
                    Text("Moves:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    pokemon.moves.forEach { m ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ${m.name}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            TypeBadge(type = m.type)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun PokemonHpBarCard(
    pokemon: BattlePokemon,
    playerName: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val animatedHpPercent by animateFloatAsState(targetValue = pokemon.hpPercent)

    val hpColor = when {
        animatedHpPercent > 0.5f -> Color(0xFF4CAF50) // Green
        animatedHpPercent > 0.2f -> Color(0xFFFFC107) // Yellow
        else -> Color(0xFFF44336) // Red
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .width(160.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Text(
                text = pokemon.name.ifEmpty { "Pokemon" },
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                color = Color.Black
            )
            Text(
                text = playerName,
                fontSize = 10.sp,
                color = Color(0xFF555555)
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { animatedHpPercent },
                color = hpColor,
                trackColor = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (pokemon.maxHp == 100) "${pokemon.hp}% HP" else "${pokemon.hp}/${pokemon.maxHp} HP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                pokemon.status?.let { status ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Red.copy(alpha = 0.8f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = status.uppercase(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
