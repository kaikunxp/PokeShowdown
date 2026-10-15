package com.kaikunxp.pokeshowdown.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun PokemonSprite(
    species: String,
    isBack: Boolean = false,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val context = LocalContext.current
    val normalizedSpecies = species.lowercase()
        .replace(Regex("[^a-z0-9\\-]"), "")
        .replace(" ", "")

    val baseUrl = "https://play.pokemonshowdown.com/sprites"
    val animatedUrl = if (isBack) {
        "$baseUrl/ani-back/$normalizedSpecies.gif"
    } else {
        "$baseUrl/ani/$normalizedSpecies.gif"
    }

    val staticUrl = if (isBack) {
        "$baseUrl/gen5-back/$normalizedSpecies.png"
    } else {
        "$baseUrl/gen5/$normalizedSpecies.png"
    }

    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                add(AnimatedImageDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val request = ImageRequest.Builder(context)
        .data(animatedUrl)
        .crossfade(true)
        .build()

    SubcomposeAsyncImage(
        model = request,
        imageLoader = imageLoader,
        contentDescription = species,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size),
        loading = {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CatchingPokemon,
                    contentDescription = "Loading",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    modifier = Modifier.size(size * 0.5f)
                )
            }
        },
        error = {
            // Fallback to static PNG if GIF fails
            val fallbackRequest = ImageRequest.Builder(context)
                .data(staticUrl)
                .crossfade(true)
                .build()

            SubcomposeAsyncImage(
                model = fallbackRequest,
                contentDescription = species,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(size),
                error = {
                    Icon(
                        imageVector = Icons.Default.CatchingPokemon,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(size * 0.5f)
                    )
                }
            )
        }
    )
}

@Composable
fun PokemonIcon(
    species: String,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp
) {
    val context = LocalContext.current
    val normalizedSpecies = species.lowercase()
        .replace(Regex("[^a-z0-9\\-]"), "")
        .replace(" ", "")

    val iconUrl = "https://play.pokemonshowdown.com/sprites/gen5/$normalizedSpecies.png"

    val request = ImageRequest.Builder(context)
        .data(iconUrl)
        .crossfade(true)
        .build()

    SubcomposeAsyncImage(
        model = request,
        contentDescription = species,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size),
        error = {
            Icon(
                imageVector = Icons.Default.CatchingPokemon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(size * 0.8f)
            )
        }
    )
}

@Composable
fun TypeBadge(type: String, modifier: Modifier = Modifier) {
    val color = getTypeColor(type)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = type.uppercase(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "normal" -> Color(0xFFA8A878)
        "fire" -> Color(0xFFF08030)
        "water" -> Color(0xFF6890F0)
        "grass" -> Color(0xFF78C850)
        "electric" -> Color(0xFFF8D030)
        "ice" -> Color(0xFF98D8D8)
        "fighting" -> Color(0xFFC03028)
        "poison" -> Color(0xFFA040A0)
        "ground" -> Color(0xFFE0C068)
        "flying" -> Color(0xFFA890F0)
        "psychic" -> Color(0xFFF85888)
        "bug" -> Color(0xFFA8B820)
        "rock" -> Color(0xFFB8A038)
        "ghost" -> Color(0xFF705898)
        "dragon" -> Color(0xFF7038F8)
        "dark" -> Color(0xFF705848)
        "steel" -> Color(0xFFB8B8D0)
        "fairy" -> Color(0xFFEE99AC)
        else -> Color.Gray
    }
}
