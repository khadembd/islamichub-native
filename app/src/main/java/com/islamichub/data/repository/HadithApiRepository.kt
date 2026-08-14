package com.islamichub.data.repository

import com.islamichub.data.api.HadithApi
import com.islamichub.data.model.HadithItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HadithApiRepository — Hadith collection via fawazahmed0/hadith-api.
 *
 * Per source hadith-api.js:
 *   https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ben-{bookId}.min.json
 *
 * Available Bengali editions:
 *  - ben-bukhari   → সহিহ বুখারী
 *  - ben-muslim    → সহিহ মুসলিম
 *  - ben-abudawud  → সুনান আবু দাউদ
 *  - ben-tirmidhi  → জামে আত-তিরমিজী
 *  - ben-nasai     → সুনান আন-নাসায়ী
 *  - ben-ibnmajah  → সুনান ইবনে মাজাহ
 *  - ben-malik     → মুয়াত্তা মালিক
 *  - ben-ahmad     → মুসনাদ আহমাদ
 */
@Singleton
class HadithApiRepository @Inject constructor(
    private val api: HadithApi
) {

    private val cache = mutableMapOf<String, List<HadithItem>>()

    suspend fun getBookHadiths(bookId: String): List<HadithItem> {
        cache[bookId]?.let { return it }
        return try {
            withContext(Dispatchers.IO) {
                val resp = api.getBookHadiths(bookId)
                cache[bookId] = resp.hadiths
                resp.hadiths
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchHadiths(query: String, bookId: String? = null): List<HadithItem> {
        val q = query.lowercase().trim()
        if (q.isEmpty()) return emptyList()
        val books = if (bookId != null) listOf(bookId) else BOOK_IDS.keys
        val results = mutableListOf<HadithItem>()
        for (bid in books) {
            val hadiths = getBookHadiths(bid)
            results += hadiths.filter { it.text.contains(q, ignoreCase = true) }
            if (results.size >= 100) break
        }
        return results.take(100)
    }

    companion object {
        val BOOK_IDS = mapOf(
            "bukhari"  to "সহিহ বুখারী",
            "muslim"   to "সহিহ মুসলিম",
            "abudawud" to "সুনান আবু দাউদ",
            "tirmidhi" to "জামে আত-তিরমিজী",
            "nasai"    to "সুনান আন-নাসায়ী",
            "ibnmajah" to "সুনান ইবনে মাজাহ",
            "malik"    to "মুয়াত্তা মালিক",
            "ahmad"    to "মুসনাদ আহমাদ"
        )
    }
}
