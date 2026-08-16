package com.islamichub.data.repository

import com.islamichub.data.api.AlQuranApi
import com.islamichub.data.model.QuranAyah
import com.islamichub.data.model.QuranEdition
import com.islamichub.data.model.QuranSurah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * QuranRepository — full 114-surah Quran via AlQuran Cloud API.
 *
 * Per source quran-module.js:
 *  - GET /surah → surah metadata list (114 surahs)
 *  - GET /surah/{n}/editions/quran-uthmani,bn.bengali → Arabic + Bangla
 *
 * Audio: https://cdn.islamic.network/quran/audio/128/{qari}/{globalAyahNumber}.mp3
 */
@Singleton
class QuranRepository @Inject constructor(
    private val api: AlQuranApi
) {

    private var surahListCache: List<QuranSurah>? = null

    suspend fun getSurahList(): List<QuranSurah> {
        surahListCache?.let { return it }
        return try {
            withContext(Dispatchers.IO) {
                val resp = api.getSurahList()
                surahListCache = resp.data
                resp.data
            }
        } catch (e: Exception) {
            // Fallback: minimal hardcoded surah metadata (first + last)
            emptyList()
        }
    }

    suspend fun getSurahContent(surahNumber: Int): Pair<QuranEdition?, QuranEdition?> {
        return try {
            withContext(Dispatchers.IO) {
                val resp = api.getSurahWithTranslation(surahNumber)
                val arabic = resp.data.getOrNull(0)
                val bangla = resp.data.getOrNull(1)
                arabic to bangla
            }
        } catch (e: Exception) {
            null to null
        }
    }

    /**
     * Audio URL for a specific ayah.
     * Qari options: ar.alafasy, ar.husary, ar.minshawi, ar.parhizgar
     */
    fun getAyahAudioUrl(qariId: String, globalAyahNumber: Int): String {
        return "https://cdn.islamic.network/quran/audio/128/$qariId/$globalAyahNumber.mp3"
    }

    /**
     * Combine Arabic + Bangla ayahs into pairs for display.
     */
    fun combineAyahs(arabic: QuranEdition?, bangla: QuranEdition?): List<AyahPair> {
        val arabicAyahs = arabic?.surahs?.ayahs.orEmpty()
        val banglaAyahs = bangla?.surahs?.ayahs.orEmpty()
        return arabicAyahs.mapIndexed { idx, ar ->
            AyahPair(
                numberInSurah = ar.numberInSurah,
                arabic = ar.text,
                bangla = banglaAyahs.getOrNull(idx)?.text.orEmpty(),
                globalNumber = ar.number,
                juz = ar.juz,
                page = ar.page,
                sajda = ar.sajda
            )
        }
    }
}

data class AyahPair(
    val numberInSurah: Int,
    val arabic: String,
    val bangla: String,
    val globalNumber: Int,
    val juz: Int,
    val page: Int,
    val sajda: Boolean
)
