package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Namaz (Salah) — categories + prayers + extras + shikkha
 * Sources:
 *   - namaz-data.js          → namaz.json         (categories + prayers)
 *   - namaz-extras-data.js   → namaz_extras.json  (short surahs for salah recitation)
 *   - namazshikkha-data.js   → namaz_shikkha.json (step-by-step learning)
 */

@Serializable
data class NamazCategory(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val color: String = ""
)

@Serializable
data class NamazPrayer(
    val id: String = "",
    val name: String = "",
    val time: String = "",
    val rakat: String = "",
    val description: String = ""
)

@Serializable
data class NamazData(
    val categories: List<NamazCategory> = emptyList(),
    val prayers: List<NamazPrayer> = emptyList()
)

/** namazshikkha-data.js → namaz_shikkha.json — actual structure */
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
data class NamazShikkhaCategory(
    val id: String = "",
    val name: String = "",
    val icon: String = "",
    val color: String = "",
    val steps: List<NamazShikkhaStep> = emptyList()
)

@Serializable
data class NamazShikkhaData(
    val metadata: Map<String, String> = emptyMap(),
    val common_steps: Map<String, NamazShikkhaStep> = emptyMap(),
    val categories: List<NamazShikkhaCategory> = emptyList()
)

@Serializable
data class NamazSurah(
    val id: Int = 0,
    val name: String = "",
    val arabic: String = "",
    val transliteration: String = "",
    val bangla: String = "",
    val ayahs: Int = 0,
    val audio: String = ""
)

@Serializable
data class NamazExtras(
    val namazSurahs: List<NamazSurah> = emptyList(),
    val namazImportantDuas: List<NamazSurah> = emptyList(),
    val kalimas: List<NamazSurah> = emptyList(),
    val namazDuas: List<NamazSurah> = emptyList(),
    val allDuas: List<NamazSurah> = emptyList(),
    val koumiHadiths: List<NamazSurah> = emptyList()
)
