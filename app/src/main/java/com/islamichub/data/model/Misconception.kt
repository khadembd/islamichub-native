package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Misconceptions about Islam
 * Source: misconceptions-data.js → misconceptions.json
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
data class MisconceptionData(
    val categories: Map<String, List<Misconception>> = emptyMap(),
    val items: List<Misconception> = emptyList()
)
