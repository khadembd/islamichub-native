package com.islamichub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.islamichub.data.local.dao.BookmarkDao
import com.islamichub.data.local.dao.SalahTrackerDao
import com.islamichub.data.local.dao.ZikrSessionDao
import com.islamichub.data.local.entities.BookmarkEntity
import com.islamichub.data.local.entities.SalahTrackerEntity
import com.islamichub.data.local.entities.ZikrSessionEntity
import com.islamichub.data.local.util.BookmarkTypeConverter

/**
 * Islamic Hub — Room database
 * Holds: bookmarks, salah tracker, zikr sessions
 */
@Database(
    entities = [
        BookmarkEntity::class,
        SalahTrackerEntity::class,
        ZikrSessionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(BookmarkTypeConverter::class)
abstract class IslamicHubDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun salahTrackerDao(): SalahTrackerDao
    abstract fun zikrSessionDao(): ZikrSessionDao

    companion object {
        const val NAME = "islamichub.db"
    }
}
