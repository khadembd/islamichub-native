package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Aladhan API response — prayer times + Hijri date.
 * API: https://api.aladhan.com/v1/timings
 */
@Serializable
data class AladhanResponse(
    val code: Int = 0,
    val status: String = "",
    val data: AladhanData? = null
)

@Serializable
data class AladhanData(
    val timings: AladhanTimings? = null,
    val date: AladhanDate? = null,
    val meta: AladhanMeta? = null
)

@Serializable
data class AladhanTimings(
    val Fajr: String = "",
    val Sunrise: String = "",
    val Dhuhr: String = "",
    val Asr: String = "",
    val Sunset: String = "",
    val Maghrib: String = "",
    val Isha: String = "",
    val Imsak: String = "",
    val Midnight: String = "",
    val Firstthird: String = "",
    val Lastthird: String = ""
)

@Serializable
data class AladhanDate(
    val readable: String = "",
    val timestamp: String = "",
    val hijri: AladhanHijri? = null,
    val gregorian: AladhanGregorian? = null
)

@Serializable
data class AladhanHijri(
    val date: String = "",
    val day: String = "",
    val month: AladhanMonth? = null,
    val year: String = "",
    val weekday: AladhanWeekday? = null,
    val designation: AladhanDesignation? = null,
    val holidays: List<String> = emptyList()
)

@Serializable
data class AladhanGregorian(
    val date: String = "",
    val day: String = "",
    val month: AladhanMonth? = null,
    val year: String = "",
    val weekday: AladhanWeekday? = null,
    val designation: AladhanDesignation? = null
)

@Serializable
data class AladhanMonth(
    val number: Int = 0,
    val en: String = "",
    val ar: String = ""
)

@Serializable
data class AladhanWeekday(
    val en: String = "",
    val ar: String = ""
)

@Serializable
data class AladhanDesignation(
    val abbreviated: String = "",
    val expanded: String = ""
)

@Serializable
data class AladhanMeta(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timezone: String = "",
    val method: AladhanMethod? = null
)

@Serializable
data class AladhanMethod(
    val id: Int = 0,
    val name: String = ""
)
