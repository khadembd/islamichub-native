package com.islamichub.data.api

import com.islamichub.data.model.HadithApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Hadith API — fawazahmed0/hadith-api (free, CORS, no key)
 * Per source hadith-api.js:
 *   https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/ben-{bookId}.min.json
 *   https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/info/{book}/bn.json
 *
 * Available books: bukhari, muslim, abudawud, tirmidhi, nasai, ibnmajah, malik, ahmad
 */
interface HadithApi {

    @GET("fawazahmed0/hadith-api@1/editions/ben-{bookId}.min.json")
    suspend fun getBookHadiths(@Path("bookId") bookId: String): HadithApiResponse

    @GET("fawazahmed0/hadith-api@1/info/{book}/bn.json")
    suspend fun getBookInfo(@Path("book") book: String): Map<String, String>
}
