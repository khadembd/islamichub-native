package com.islamichub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.islamichub.data.model.BookmarkType

/**
 * Room entity for bookmarks (Quran, Hadith, Dua, etc.)
 */
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemType: BookmarkType,
    val itemId: String,
    val title: String,
    val subtitle: String,
    val arabic: String,
    val category: String,
    val createdAt: Long
)
