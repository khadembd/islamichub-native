package com.islamichub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.islamichub.data.local.entities.ZikrSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZikrSessionDao {

    @Query("SELECT * FROM zikr_sessions ORDER BY completedAt DESC LIMIT 100")
    fun observeRecent(): Flow<List<ZikrSessionEntity>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM zikr_sessions WHERE zikrType = :type AND date = :date")
    fun observeDailyTotal(type: String, date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(count), 0) FROM zikr_sessions WHERE zikrType = :type")
    fun observeGrandTotal(type: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ZikrSessionEntity): Long
}
