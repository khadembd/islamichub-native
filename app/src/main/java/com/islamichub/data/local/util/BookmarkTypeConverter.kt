package com.islamichub.data.local.util

import androidx.room.TypeConverter
import com.islamichub.data.model.BookmarkType

class BookmarkTypeConverter {
    @TypeConverter
    fun fromType(type: BookmarkType): String = type.name

    @TypeConverter
    fun toType(value: String): BookmarkType =
        runCatching { BookmarkType.valueOf(value) }.getOrDefault(BookmarkType.QURAN)
}
