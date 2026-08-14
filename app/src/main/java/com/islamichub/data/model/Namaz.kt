package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Namaz (Salah) — categories + prayers + extras
 * Sources:
 *   - namaz-data.js          → namaz.json         (categories + prayers)
 *   - namaz-extras-data.js   → namaz_extras.json  (short surahs for salah recitation)
 *   - extended-namaz-data.js → extended_namaz.json (additional rules)
 *   - namazshikkha-data.js   → namaz_shikkha.json (step-by-step learning)
 *
 * Per migration plan §15: namaz-data.js and namazshikkha-data.js both define
 * `window.namazData` with different shapes. Native Kotlin models keep them
 * completely separate — NamazData vs NamazShikkhaData — to avoid the collision.
 */
@Serializable
data class NamazCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: String
)

@Serializable
data class NamazPrayer(
    val id: String,
    val name: String,
    val time: String,
    val rakat: String,
    val description: String
)

@Serializable
data class NamazData(
    val categories: List<NamazCategory> = emptyList(),
    val prayers: List<NamazPrayer> = emptyList()
)

/** namazshikkha-data.js — different shape from NamazData, kept separate. */
@Serializable
data class NamazShikkhaStep(
    val id: String = "",
    val title: String = "",
    val arabic: String = "",
    val transliteration: String = "",
    val bangla: String = "",
    val description: String = "",
    val audio: String = ""
)

@Serializable
data class NamazShikkhaData(
    val metadata: Map<String, String> = emptyMap(),
    val steps: List<NamazShikkhaStep> = emptyList(),
    val categories: List<NamazCategory> = emptyList()
)

@Serializable
data class NamazSurah(
    val id: Int,
    val name: String,
    val arabic: String,
    val transliteration: String,
    val bangla: String,
    val ayahs: Int = 0,
    val audio: String = ""
)

@Serializable
data class NamazExtras(
    val namazSurahs: List<NamazSurah> = emptyList()
)
