package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Hadith — Sahih Bukhari / Muslim excerpts (Bangla)
 * Sources:
 *   - hadith-data.js          → hadith.json         (top-level: hadiths[])
 *   - extended-hadith-data.js → extended_hadith.json (top-level: hadith_topics[])
 *
 * NOTE: Extended Hadith uses `hadith_topics` (not `items` or `hadiths`).
 * Each topic has: id, name, arabic, description, category, subcategory.
 */
@Serializable
data class Hadith(
    val id: Int = 0,
    val title: String = "",
    val arabic: String = "",
    val bangla: String = "",
    val reference: String = "",
    val explanation: String = "",
    val narrator: String = "",
    val category: String = ""
)

@Serializable
data class HadithCollection(
    val hadiths: List<Hadith> = emptyList()
)

/** extended_hadith.json has hadith_topics[] not items[] or hadiths[] */
@Serializable
data class ExtendedHadithTopic(
    val id: Int = 0,
    val name: String = "",
    val arabic: String = "",
    val description: String = "",
    val category: String = "",
    val subcategory: String = ""
)

@Serializable
data class ExtendedHadithCollection(
    val hadith_topics: List<ExtendedHadithTopic> = emptyList()
)
