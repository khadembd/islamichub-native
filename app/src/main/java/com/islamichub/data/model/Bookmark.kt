package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Bookmark — for Quran ayahs, Hadith, Duas, etc.
 * Stored locally via Room (BookmarkEntity) — see data/local/entities.
 */
@Serializable
data class Bookmark(
    val id: Long = 0,
    val itemType: BookmarkType,
    val itemId: String,
    val title: String,
    val subtitle: String = "",
    val arabic: String = "",
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
enum class BookmarkType {
    QURAN, HADITH, DUA, ASMAUL_HUSNA, QUESTION, MISCONCEPTION, STORY
}
