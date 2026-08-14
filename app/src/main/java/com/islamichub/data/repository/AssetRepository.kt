package com.islamichub.data.repository

import android.content.Context
import com.islamichub.data.model.AnswerData
import com.islamichub.data.model.AsmaulHusna
import com.islamichub.data.model.BangladeshLocations
import com.islamichub.data.model.DuaCollection
import com.islamichub.data.model.ExtendedHadithCollection
import com.islamichub.data.model.HadithCollection
import com.islamichub.data.model.IslamicStoriesData
import com.islamichub.data.model.KalimaCollection
import com.islamichub.data.model.MisconceptionData
import com.islamichub.data.model.NamazData
import com.islamichub.data.model.NamazExtras
import com.islamichub.data.model.NamazShikkhaData
import com.islamichub.data.model.QuestionData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AssetRepository — reads bundled JSON data files from `assets/data/`.
 *
 * সব ডেটা JSON আকারে bundled থাকে — কোনো runtime network fetch নয়।
 * Conversion script: scripts/convert_data.js
 */
@Singleton
class AssetRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private suspend fun readAsset(name: String): String = withContext(Dispatchers.IO) {
        context.assets.open("data/$name").bufferedReader().use { it.readText() }
    }

    private inline fun <reified T> decode(s: String): T = json.decodeFromString<T>(s)

    suspend fun kalimas(): KalimaCollection = decode(readAsset("kalima.json"))
    suspend fun duas(): DuaCollection = decode(readAsset("dua.json"))
    suspend fun asmaulHusna(): List<AsmaulHusna> = decode(readAsset("asmaul_husna.json"))
    suspend fun hadith(): HadithCollection = decode(readAsset("hadith.json"))
    suspend fun extendedHadith(): ExtendedHadithCollection = decode(readAsset("extended_hadith.json"))
    suspend fun namaz(): NamazData = decode(readAsset("namaz.json"))
    suspend fun namazExtras(): NamazExtras = decode(readAsset("namaz_extras.json"))
    suspend fun namazShikkha(): NamazShikkhaData = decode(readAsset("namaz_shikkha.json"))
    suspend fun questions(): QuestionData = decode(readAsset("questions.json"))
    suspend fun answers(): AnswerData = decode(readAsset("answers.json"))
    suspend fun misconceptions(): MisconceptionData = decode(readAsset("misconceptions.json"))
    suspend fun stories(): IslamicStoriesData = decode(readAsset("islamic_stories.json"))
    suspend fun locations(): BangladeshLocations {
        // locations.json has divisions as top-level keys, wrap it
        val raw = readAsset("locations.json")
        val map = json.decodeFromString<Map<String, Map<String, List<String>>>>(raw)
        return BangladeshLocations(divisions = map)
    }
}
