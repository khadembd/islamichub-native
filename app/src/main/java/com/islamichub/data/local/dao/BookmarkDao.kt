package com.islamichub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.islamichub.data.local.entities.BookmarkEntity
import com.islamichub.data.model.BookmarkType
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE itemType = :type ORDER BY createdAt DESC")
    fun observeByType(type: BookmarkType): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE itemType = :type AND itemId = :itemId)")
    suspend fun isBookmarked(type: BookmarkType, itemId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity): Long

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE itemType = :type AND itemId = :itemId")
    suspend fun deleteById(type: BookmarkType, itemId: String)
}
