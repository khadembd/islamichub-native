package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Asmaul Husna — 99 Names of Allah
 * Source: asmaul-husna-data.js → asmaul_husna.json
 * NOTE: data uses `transliteration` field (not `bangla` per migration plan §14 bug)
 */
@Serializable
data class AsmaulHusna(
    val id: Int,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val explanation: String,
    val amal: String
)
