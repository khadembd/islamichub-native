package com.islamichub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Salah tracker — daily prayer completion log.
 * One row per (date, prayerId).
 */
@Entity(tableName = "salah_tracker", primaryKeys = ["date", "prayerId"])
data class SalahTrackerEntity(
    val date: String,           // yyyy-MM-dd
    val prayerId: String,       // fajr | dhuhr | asr | maghrib | isha
    val completed: Boolean,
    val completedAt: Long? = null,
    val note: String = ""
)
