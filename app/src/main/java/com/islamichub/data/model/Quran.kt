package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * AlQuran Cloud API response models.
 * API: https://api.alquran.cloud/v1/
 */
@Serializable
data class QuranSurahListResponse(
    val code: Int = 0,
    val status: String = "",
    val data: List<QuranSurah> = emptyList()
)

@Serializable
data class QuranSurah(
    val number: Int = 0,
    val name: String = "",
    val englishName: String = "",
    val englishNameTranslation: String = "",
    val numberOfAyahs: Int = 0,
    val revelationType: String = ""
)

@Serializable
data class QuranEditionResponse(
    val code: Int = 0,
    val status: String = "",
    val data: List<QuranEdition> = emptyList()
)

@Serializable
data class QuranEdition(
    val identifier: String = "",
    val language: String = "",
    val name: String = "",
    val englishName: String = "",
    val format: String = "",
    val type: String = "",
    val numberOfAyahs: Int = 0,
    val surahs: QuranSurahContent? = null
)

@Serializable
data class QuranSurahContent(
    val number: Int = 0,
    val name: String = "",
    val englishName: String = "",
    val englishNameTranslation: String = "",
    val revelationType: String = "",
    val numberOfAyahs: Int = 0,
    val ayahs: List<QuranAyah> = emptyList()
)

@Serializable
data class QuranAyah(
    val number: Int = 0,
    val text: String = "",
    val numberInSurah: Int = 0,
    val juz: Int = 0,
    val manzil: Int = 0,
    val page: Int = 0,
    val ruku: Int = 0,
    val hizbQuarter: Int = 0,
    val sajda: Boolean = false
)
