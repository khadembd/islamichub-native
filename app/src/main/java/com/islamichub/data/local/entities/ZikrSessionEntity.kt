package com.islamichub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for Zikr (Tasbih) counter sessions.
 */
@Entity(tableName = "zikr_sessions")
data class ZikrSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zikrType: String,       // subhanallah, alhamdulillah, allahu_akbar, custom
    val count: Int,
    val target: Int,
    val date: String,           // yyyy-MM-dd
    val completedAt: Long
)
