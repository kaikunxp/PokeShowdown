package com.kaikunxp.pokeshowdown.data.remote

import android.util.Log
import com.kaikunxp.pokeshowdown.util.MoveDetails
import com.kaikunxp.pokeshowdown.util.MoveDex
import com.kaikunxp.pokeshowdown.util.Pokedex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ShowdownApiService(
    private val baseUrl: String = "https://play.pokemonshowdown.com"
) {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    /**
     * Attempts guest authentication with the given username and challenge string.
     * Returns assertion string if successful.
     */
    suspend fun getGuestAssertion(
        username: String,
        challstr: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder()
            .add("act", "getassertion")
            .add("userid", username.lowercase().replace(Regex("[^a-z0-9]"), ""))
            .add("challstr", challstr)
            .build()

        val request = Request.Builder()
            .url("$baseUrl/action.php")
            .post(formBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("HTTP Error ${response.code}"))
                }
                val bodyText = response.body?.string() ?: ""
                if (bodyText.startsWith(";")) {
                    return@withContext Result.failure(Exception("Assertion rejected: $bodyText"))
                }
                Result.success(bodyText.trim())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Login with full username and password using Showdown action.php.
     * Returns assertion string on success.
     */
    suspend fun loginAccount(
        username: String,
        password: String,
        challstr: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder()
            .add("act", "login")
            .add("name", username)
            .add("pass", password)
            .add("challstr", challstr)
            .build()

        val request = Request.Builder()
            .url("$baseUrl/action.php")
            .post(formBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("HTTP Error ${response.code}"))
                }
                val bodyText = response.body?.string() ?: ""
                if (bodyText.startsWith("]") || bodyText.startsWith("{")) {
                    val jsonObj = json.parseToJsonElement(bodyText.substring(1)).jsonObject
                    val success = jsonObj["actionsuccess"]?.jsonPrimitive?.booleanOrNull ?: false
                    if (success) {
                        val assertion = jsonObj["assertion"]?.jsonPrimitive?.content ?: ""
                        return@withContext Result.success(assertion)
                    }
                }
                Result.failure(Exception("Login failed: $bodyText"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Dynamically fetches Official Pokémon Showdown Pokedex, Move Dex, and Text description files.
     * Caches species types and move details directly in Pokedex and MoveDex singletons.
     */
    suspend fun loadShowdownDexData() = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch pokedex.json
            val pokeRequest = Request.Builder().url("$baseUrl/data/pokedex.json").build()
            client.newCall(pokeRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val root = json.parseToJsonElement(body).jsonObject
                    val speciesMap = mutableMapOf<String, List<String>>()

                    root.forEach { (speciesKey, elem) ->
                        val obj = elem.jsonObject
                        val typesArray = obj["types"]?.jsonArray
                        val typesList = mutableListOf<String>()
                        typesArray?.forEach { t ->
                            typesList.add(t.jsonPrimitive.content)
                        }
                        if (typesList.isNotEmpty()) {
                            speciesMap[speciesKey] = typesList
                        }
                    }
                    Pokedex.updateFromApi(speciesMap)
                    Log.d("ShowdownApiService", "Loaded ${speciesMap.size} species types from Showdown API.")
                }
            }

            // 2. Fetch moves.json
            val moveRequest = Request.Builder().url("$baseUrl/data/moves.json").build()
            client.newCall(moveRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val root = json.parseToJsonElement(body).jsonObject
                    val movesMap = mutableMapOf<String, MoveDetails>()

                    root.forEach { (moveKey, elem) ->
                        val obj = elem.jsonObject
                        val name = obj["name"]?.jsonPrimitive?.content ?: moveKey
                        val type = obj["type"]?.jsonPrimitive?.content ?: "Normal"
                        val basePower = obj["basePower"]?.jsonPrimitive?.intOrNull ?: 0
                        val accPrim = obj["accuracy"]?.jsonPrimitive
                        val accuracy = accPrim?.intOrNull ?: if (accPrim?.booleanOrNull == true) 0 else 100
                        val category = obj["category"]?.jsonPrimitive?.content ?: "Special"
                        val desc = obj["shortDesc"]?.jsonPrimitive?.content
                            ?: obj["desc"]?.jsonPrimitive?.content
                            ?: "$name is a $type-type $category move."

                        movesMap[moveKey] = MoveDetails(
                            name = name,
                            type = type,
                            basePower = basePower,
                            accuracy = accuracy,
                            category = category,
                            description = desc
                        )
                    }
                    MoveDex.updateFromApi(movesMap)
                    Log.d("ShowdownApiService", "Loaded ${movesMap.size} moves from Showdown API.")
                }
            }

            // 3. Fetch text.js & text/en.js for detailed move descriptions
            val textUrls = listOf("$baseUrl/data/text.js", "$baseUrl/data/text/en.js")
            for (url in textUrls) {
                val textRequest = Request.Builder().url(url).build()
                client.newCall(textRequest).execute().use { response ->
                    if (response.isSuccessful) {
                        val textBody = response.body?.string() ?: ""
                        val moveDescs = parseTextJsDescriptions(textBody)
                        if (moveDescs.isNotEmpty()) {
                            MoveDex.updateDescriptionsFromApi(moveDescs)
                            Log.d("ShowdownApiService", "Loaded ${moveDescs.size} move descriptions from $url.")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ShowdownApiService", "Error fetching Showdown dex data: ${e.message}")
        }
    }

    fun parseTextJsDescriptions(textJs: String): Map<String, String> {
        val result = mutableMapOf<String, String>()

        // text/en.js is minified into a single line.
        // Format: movekey:{name:"Move Name",desc:"...",shortDesc:"..."}
        // Use regex to find all moves that have a `name:"..."` and extract their shortDesc or desc.
        val moveRegex = Regex("""([a-z0-9]+|"[a-z0-9]+"):\{name:"[^"]+"([^}]+)\}""")
        val descRegex = Regex("""desc:"(.*?)(?<!\\)"""")
        val shortDescRegex = Regex("""shortDesc:"(.*?)(?<!\\)"""")

        moveRegex.findAll(textJs).forEach { match ->
            val rawKey = match.groupValues[1]
            val key = rawKey.replace("\"", "") // Remove quotes if present
            val innerContent = match.groupValues[2]

            val desc = descRegex.find(innerContent)?.groupValues?.get(1) ?: ""
            val shortDesc = shortDescRegex.find(innerContent)?.groupValues?.get(1) ?: ""

            // Prefer shortDesc if available, otherwise use desc
            val bestDesc = if (shortDesc.isNotBlank()) shortDesc else desc
            if (bestDesc.isNotBlank()) {
                result[key] = bestDesc
            }
        }

        return result
    }
}
