package com.islamichub.services

import com.islamichub.data.model.PrayerTimes
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * PrayerTimeCalculator — pure astronomical calculation of the five daily
 * prayer times for any location on Earth. No network needed.
 *
 * পুরোপুরি নেটিভ calculation — browser/JS engine dependency নেই।
 *
 * Algorithm based on PrayTimes.org (open-source, public domain).
 *
 * Supported calculation methods:
 *   - MWL     (Muslim World League)        — default
 *   - KARACHI (Univ. of Islamic Sciences, Karachi)
 *   - EGYPT   (Egyptian General Authority)
 *   - MAKKAH  (Umm al-Qura)
 *   - KARACHI_HANAF
 *
 * Supported madhabs:
 *   - shafi  (standard Asr factor 1)
 *   - hanafi (Asr factor 2)
 */
object PrayerTimeCalculator {

    // Method → (Fajr angle, Isha angle, Isha interval minutes [0 = use angle])
    data class Method(val fajr: Double, val isha: Double, val ishaInterval: Int = 0)

    val MWL     = Method(18.0, 17.0)
    val KARACHI = Method(18.0, 18.0)
    val EGYPT   = Method(19.5, 17.5)
    val MAKKAH = Method(18.5, 0.0, 90)
    val KARACHI_HANAF = Method(18.0, 18.0)

    private const val KAABA_LAT = 21.422487
    private const val KAABA_LON = 39.826206

    fun compute(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        timezone: Double,
        method: Method = MWL,
        asrFactor: Int = 1   // 1 = Shafi, 2 = Hanafi
    ): PrayerTimes {
        val jd = julianDate(date.year, date.monthValue, date.dayOfMonth) - longitude / (15.0 * 24.0)
        val sunPos = sunPosition(jd)
        val decl = sunPos.declination
        val eqt = sunPos.equationOfTime

        val dhuhrMinutes = computeDhuhr(longitude, timezone, eqt)
        val sunriseMin = computeTime(180 - 0.833, latitude, decl, dhuhrMinutes, timezone)
        val sunsetMin = computeTime(0.833, latitude, decl, dhuhrMinutes, timezone)
        val fajrMin = computeTime(180 - method.fajr, latitude, decl, dhuhrMinutes, timezone)
        val ishaMin: Double = if (method.ishaInterval > 0) {
            sunsetMin + method.ishaInterval
        } else {
            computeTime(method.isha, latitude, decl, dhuhrMinutes, timezone)
        }
        val maghribMin = sunsetMin
        val asrMin = computeAsr(asrFactor, latitude, decl, dhuhrMinutes, timezone)
        val ishaComputedMin = if (ishaMin < maghribMin) ishaMin + 24 * 60 else ishaMin

        val zone = ZoneId.of(zoneIdForOffset(timezone))
        val base = ZonedDateTime.of(date, java.time.LocalTime.MIDNIGHT, zone)

        return PrayerTimes(
            fajr = base.plusMinutes(fajrMin.toLong()).toInstant().toEpochMilli(),
            sunrise = base.plusMinutes(sunriseMin.toLong()).toInstant().toEpochMilli(),
            dhuhr = base.plusMinutes(dhuhrMinutes.toLong()).toInstant().toEpochMilli(),
            asr = base.plusMinutes(asrMin.toLong()).toInstant().toEpochMilli(),
            maghrib = base.plusMinutes(maghribMin.toLong()).toInstant().toEpochMilli(),
            isha = base.plusMinutes(ishaComputedMin.toLong()).toInstant().toEpochMilli(),
            date = date.toString(),
            location = "$latitude,$longitude",
            latitude = latitude,
            longitude = longitude,
            timezone = timezone,
            method = "MWL",
            madhab = if (asrFactor == 2) "hanafi" else "shafi"
        )
    }

    /** Qibla bearing (degrees from True North) from the given location. */
    fun qiblaBearing(latitude: Double, longitude: Double): Double {
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LAT)
        val dL = Math.toRadians(KAABA_LON - longitude)
        val y = sin(dL)
        val x = cos(phi1) * tan(phi2) - sin(phi1) * cos(dL)
        var bearing = Math.toDegrees(atan2(y, x))
        return ((bearing + 360.0) % 360.0)
    }

    /** Great-circle distance to the Kaaba in kilometers. */
    fun distanceToKaabaKm(latitude: Double, longitude: Double): Double {
        val R = 6371.0
        val phi1 = Math.toRadians(latitude)
        val phi2 = Math.toRadians(KAABA_LAT)
        val dPhi = Math.toRadians(KAABA_LAT - latitude)
        val dL = Math.toRadians(KAABA_LON - longitude)
        val a = sin(dPhi / 2) * sin(dPhi / 2) + cos(phi1) * cos(phi2) * sin(dL / 2) * sin(dL / 2)
        val c = 2 * atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return R * c
    }

    // ---- internals ----

    private data class SunPos(val declination: Double, val equationOfTime: Double)

    private fun sunPosition(jd: Double): SunPos {
        val D = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * D)
        val q = fixAngle(280.459 + 0.98564736 * D)
        val L = fixAngle(q + 1.915 * sin(g.toRad()) + 0.020 * sin((2 * g).toRad()))
        val e = 23.439 - 0.00000036 * D
        val RA = atan2(cos(e.toRad()) * sin(L.toRad()), cos(L.toRad())) / 15.0
        val eqt = q / 15.0 - fixHour(RA)
        val decl = Math.toDegrees(asin(sin(e.toRad()) * sin(L.toRad())))
        return SunPos(decl, eqt)
    }

    private fun computeTime(angle: Double, lat: Double, decl: Double, dhuhr: Double, tz: Double): Double {
        val t = (1.0 / 15.0) * Math.toDegrees(acos(
            (-sin(angle.toRad()) - sin(lat.toRad()) * sin(decl.toRad())) /
                    (cos(lat.toRad()) * cos(decl.toRad()))
        ))
        return dhuhr + (if (angle > 90) -t else t) - tz / 15.0 * 0
    }

    private fun computeAsr(factor: Int, lat: Double, decl: Double, dhuhr: Double, tz: Double): Double {
        val angle = -atan(1.0 / (factor + tan(abs(lat - decl).toRad())))
        val t = (1.0 / 15.0) * Math.toDegrees(acos(
            (-sin(angle) - sin(lat.toRad()) * sin(decl.toRad())) /
                    (cos(lat.toRad()) * cos(decl.toRad()))
        ))
        return dhuhr + t
    }

    private fun computeDhuhr(lon: Double, tz: Double, eqt: Double): Double {
        return 12 + tz - lon / 15.0 - eqt
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        if (month <= 2) { return julianDate(year - 1, month + 12, day) }
        val A = floor(year / 100.0)
        val B = 2 - A + floor(A / 4.0)
        return floor(365.25 * (year + 4716)) + floor(30.6001 * (month + 1)) + day + B - 1524.5
    }

    private fun fixAngle(a: Double) = ((a % 360) + 360) % 360
    private fun fixHour(h: Double) = ((h % 24) + 24) % 24
    private fun Double.toRad() = Math.toRadians(this)

    private fun zoneIdForOffset(offsetHours: Double): String {
        val sign = if (offsetHours >= 0) "+" else "-"
        val absH = abs(offsetHours)
        val h = floor(absH).toInt()
        val m = ((absH - h) * 60).toInt()
        return "UTC%s%02d:%02d".format(sign, h, m)
    }
}
