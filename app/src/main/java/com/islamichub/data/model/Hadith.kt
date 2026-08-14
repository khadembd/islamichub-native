package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Hadith — Sahih Bukhari / Muslim excerpts (Bangla)
 * Source: hadith-data.js → hadith.json
 *        + extended-hadith-data.js → extended_hadith.json
 *
 * NOTE: Extended Hadith uses `items` (not `hadiths`) per migration plan §14 bug.
 */
@Serializable
data class Hadith(
    val id: Int,
    val title: String,
    val arabic: String,
    val bangla: String,
    val reference: String = "",
    val explanation: String = "",
    val narrator: String = "",
    val category: String = ""
)

@Serializable
data class HadithCollection(
    val hadiths: List<Hadith> = emptyList()
)

@Serializable
data class ExtendedHadithCollection(
    val items: List<Hadith> = emptyList()
)
