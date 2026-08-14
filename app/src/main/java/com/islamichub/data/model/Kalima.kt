package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Kalima — 6 fundamentals of Islamic faith
 * Source: kalima-data.js → kalima.json
 */
@Serializable
data class Kalima(
    val id: Int,
    val name: String,
    val arabic: String,
    val bangla: String,
    val meaning: String
)

@Serializable
data class KalimaCollection(
    val kalimas: List<Kalima> = emptyList()
)
