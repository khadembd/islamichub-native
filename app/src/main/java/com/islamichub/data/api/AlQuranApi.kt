package com.islamichub.data.api

import com.islamichub.data.model.QuranSurahListResponse
import com.islamichub.data.model.QuranEditionResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * AlQuran Cloud API — full Quran data
 * API: https://api.alquran.cloud/v1/
 *
 * Per source quran-module.js:
 *   - GET /surah → list of 114 surahs (metadata)
 *   - GET /surah/{number}/editions/quran-uthmani,bn.bengali → Arabic + Bangla
 */
interface AlQuranApi {

    @GET("surah")
    suspend fun getSurahList(): QuranSurahListResponse

    @GET("surah/{number}/editions/quran-uthmani,bn.bengali")
    suspend fun getSurahWithTranslation(@Path("number") surahNumber: Int): QuranEditionResponse
}
