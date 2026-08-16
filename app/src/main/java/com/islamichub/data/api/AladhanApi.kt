package com.islamichub.data.api

import com.islamichub.data.model.AladhanResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Aladhan API — prayer times via aladhan.com
 * Free, no API key, CORS-enabled.
 *
 * Per source: prayer-times.js uses:
 *   https://api.aladhan.com/v1/timings?latitude=...&longitude=...&method=1
 *   https://api.aladhan.com/v1/timingsByCity?city=...&country=Bangladesh&method=1
 */
interface AladhanApi {

    @GET("v1/timings")
    suspend fun getTimingsByCoords(
        @Query("latitude") lat: Double,
        @Query("longitude") lng: Double,
        @Query("method") method: Int = 1
    ): AladhanResponse

    @GET("v1/timingsByCity")
    suspend fun getTimingsByCity(
        @Query("city") city: String,
        @Query("country") country: String = "Bangladesh",
        @Query("method") method: Int = 1
    ): AladhanResponse
}
