package com.islamichub.data.model

import kotlinx.serialization.Serializable

/**
 * Q&A — Islamic Questions and Answers (Bangla)
 * Sources:
 *   - question-data.js → questions.json (categorized questions)
 *   - ans-data.js      → answers.json   (categorized answers)
 *
 * Per migration plan §14: source globals use `questionData` / `ansData`
 * (not `QUESTION_DATA`).
 */
@Serializable
data class QuestionCategory(
    val name: String,
    val icon: String,
    val color: String,
    val questions: List<String> = emptyList()
)

@Serializable
data class QuestionData(
    val categories: Map<String, QuestionCategory> = emptyMap()
)

@Serializable
data class Answer(
    val question: String,
    val answer: String,
    val reference: String = "",
    val category: String = ""
)

@Serializable
data class AnswerData(
    val answers: Map<String, List<Answer>> = emptyMap()
)
