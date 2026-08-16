package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Q&A — Islamic Questions and Answers (Bangla)
 * Sources:
 *   - question-data.js → questions.json (categorized questions)
 *   - ans-data.js      → answers.json   (categorized answers)
 *
 * JSON structure:
 *   questions.json = { "namaz": { name, icon, color, questions[] }, "roza": {...}, ... }
 *   answers.json   = { "namaz": [ {question, answer, reference, category} ], ... }
 *
 * Per migration plan §14: source globals use `questionData` / `ansData`
 * (not `QUESTION_DATA`).
 */
@Serializable
data class QuestionCategory(
    val name: String = "",
    val icon: String = "",
    val color: String = "",
    val questions: List<String> = emptyList()
)

@Serializable
data class QuestionData(
    val namaz: QuestionCategory = QuestionCategory(),
    val roza: QuestionCategory = QuestionCategory(),
    val hajj: QuestionCategory = QuestionCategory(),
    val zakat: QuestionCategory = QuestionCategory(),
    val quran: QuestionCategory = QuestionCategory(),
    val hadith: QuestionCategory = QuestionCategory(),
    val iman: QuestionCategory = QuestionCategory(),
    val ilm: QuestionCategory = QuestionCategory(),
    val taharah: QuestionCategory = QuestionCategory(),
    val nikah: QuestionCategory = QuestionCategory(),
    val tijarah: QuestionCategory = QuestionCategory(),
    val jihad: QuestionCategory = QuestionCategory()
)

@Serializable
data class Answer(
    val question: String = "",
    val answer: String = "",
    val reference: String = "",
    val category: String = ""
)

@Serializable
data class AnswerData(
    val namaz: List<Answer> = emptyList(),
    val roza: List<Answer> = emptyList(),
    val hajj: List<Answer> = emptyList(),
    val zakat: List<Answer> = emptyList(),
    val quran: List<Answer> = emptyList(),
    val hadith: List<Answer> = emptyList(),
    val iman: List<Answer> = emptyList(),
    val ilm: List<Answer> = emptyList(),
    val taharah: List<Answer> = emptyList(),
    val nikah: List<Answer> = emptyList(),
    val tijarah: List<Answer> = emptyList(),
    val jihad: List<Answer> = emptyList()
)
