package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Misconceptions about Islam
 * Source: misconceptions-data.js → misconceptions.json
 *
 * JSON structure:
 *   { metadata: {...}, categories: [ { id, title, icon, color, misconceptions: [...] } ] }
 */
@Serializable
data class Misconception(
    val id: String = "",
    val title: String = "",
    val question: String = "",
    val answer: String = "",
    val reference: String = "",
    val category: String = ""
)

@Serializable
data class MisconceptionCategory(
    val id: String = "",
    val title: String = "",
    val name: String = "",
    val icon: String = "",
    val color: String = "",
    val misconceptions: List<Misconception> = emptyList()
)

@Serializable
data class MisconceptionData(
    val metadata: Map<String, String> = emptyMap(),
    val categories: List<MisconceptionCategory> = emptyList()
)
