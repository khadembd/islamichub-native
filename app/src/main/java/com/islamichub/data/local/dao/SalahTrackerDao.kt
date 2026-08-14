package com.islamichub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.islamichub.data.local.entities.SalahTrackerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SalahTrackerDao {

    @Query("SELECT * FROM salah_tracker WHERE date = :date")
    fun observeForDate(date: String): Flow<List<SalahTrackerEntity>>

    @Query("SELECT * FROM salah_tracker WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeForRange(startDate: String, endDate: String): Flow<List<SalahTrackerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: SalahTrackerEntity)

    @Query("SELECT COUNT(*) FROM salah_tracker WHERE completed = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun countCompletedInRange(startDate: String, endDate: String): Int

    @Query("SELECT DISTINCT date FROM salah_tracker WHERE completed = 1 ORDER BY date DESC LIMIT 30")
    fun observeStreakDates(): Flow<List<String>>
}
