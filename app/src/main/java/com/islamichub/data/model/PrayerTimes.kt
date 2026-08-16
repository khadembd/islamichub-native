package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Prayer times — computed locally using astronomical calculations
 * (no network needed).
 *
 * Calculation method default: Muslim World League (MWL)
 * Madhab default: Shafi (Hanafi option in settings)
 *
 * These models are NOT loaded from JSON — they are computed at runtime
 * by PrayerTimeCalculator.
 */
@Serializable
data class PrayerTimes(
    val fajr: Long,         // epoch millis
    val sunrise: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long,
    val date: String,       // yyyy-MM-dd
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: Double,
    val method: String,
    val madhab: String
)

@Serializable
data class CustomJamaatTime(
    val prayerId: String,
    val offsetMinutes: Int = 0,   // offset from the adhan time
    val enabled: Boolean = false
)
