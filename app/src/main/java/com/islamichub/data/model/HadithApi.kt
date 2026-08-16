package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Hadith API response (fawazahmed0/hadith-api)
 * Editions: ben-bukhari, ben-muslim, ben-abudawud, etc.
 */
@Serializable
data class HadithApiResponse(
    val metadata: HadithMetadata = HadithMetadata(),
    val hadiths: List<HadithItem> = emptyList()
)

@Serializable
data class HadithMetadata(
    val collection: String = "",
    val language: String = "",
    val book: String = "",
    val totalHadith: Int = 0,
    val totalSections: Int = 0
)

@Serializable
data class HadithItem(
    val hadithnumber: Int = 0,
    val arabicnumber: Int = 0,
    val text: String = "",
    val grades: List<HadithGrade> = emptyList(),
    val reference: HadithReference = HadithReference()
)

@Serializable
data class HadithGrade(
    val grade: String = "",
    val grader: String = ""
)

@Serializable
data class HadithReference(
    val book: Int = 0,
    val hadith: Int = 0
)
