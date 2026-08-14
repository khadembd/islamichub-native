package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Dua (Supplication)
 * Source: dua-data.js → dua.json
 */
@Serializable
data class DuaCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: String
)

@Serializable
data class Dua(
    val id: String,
    val category: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val bangla: String,
    val ref: String = "",
    val virtue: String = ""
)

@Serializable
data class DuaCollection(
    val categories: List<DuaCategory> = emptyList(),
    val duas: List<Dua> = emptyList()
)
