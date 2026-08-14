package com.islamichub.data.repository

import com.islamichub.data.api.AladhanApi
import com.islamichub.data.model.AladhanData
import com.islamichub.data.preferences.AppPreferences
import com.islamichub.services.PrayerTimeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PrayerTimeRepository — hybrid prayer time source.
 *
 * Strategy:
 *  1. Try Aladhan API (network) — matches source prayer-times.js exactly
 *  2. Fallback to local astronomical calculation (PrayerTimeCalculator)
 *  3. Cache result for the day
 *
 * Returns Hijri date alongside timings (source islamic.html shows Hijri date).
 */
@Singleton
class PrayerTimeRepository @Inject constructor(
    private val aladhanApi: AladhanApi,
    private val prefs: AppPreferences
) {

    private var cached: AladhanData? = null
    private var cacheDate: String? = null

    suspend fun fetchPrayerTimes(lat: Double? = null, lng: Double? = null): AladhanData? {
        val today = LocalDate.now().toString()
        if (cached != null && cacheDate == today) return cached

        val latitude = lat ?: prefs.latitude.first()
        val longitude = lng ?: prefs.longitude.first()

        // 1. Try Aladhan API
        val apiResult = try {
            withContext(Dispatchers.IO) {
                aladhanApi.getTimingsByCoords(latitude, longitude, method = 1).data
            }
        } catch (e: Exception) {
            null
        }

        if (apiResult != null) {
            cached = apiResult
            cacheDate = today
            return apiResult
        }

        // 2. Fallback: local astronomical calculation
        val tz = 6.0 // Bangladesh
        val local = PrayerTimeCalculator.compute(LocalDate.now(), latitude, longitude, tz, PrayerTimeCalculator.MWL, 1)
        // Wrap into AladhanData shape so UI doesn't care which source produced it
        val fallback = AladhanData(
            timings = com.islamichub.data.model.AladhanTimings(
                Fajr = formatTime(local.fajr),
                Sunrise = formatTime(local.sunrise),
                Dhuhr = formatTime(local.dhuhr),
                Asr = formatTime(local.asr),
                Maghrib = formatTime(local.maghrib),
                Isha = formatTime(local.isha)
            ),
            date = com.islamichub.data.model.AladhanDate(
                readable = LocalDate.now().toString(),
                hijri = null,
                gregorian = null
            ),
            meta = com.islamichub.data.model.AladhanMeta(
                latitude = latitude,
                longitude = longitude,
                timezone = "Asia/Dhaka"
            )
        )
        cached = fallback
        cacheDate = today
        return fallback
    }

    private fun formatTime(millis: Long): String {
        val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
        val h = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val m = cal.get(java.util.Calendar.MINUTE)
        return "%02d:%02d".format(h, m)
    }

    fun clearCache() {
        cached = null
        cacheDate = null
    }
}
