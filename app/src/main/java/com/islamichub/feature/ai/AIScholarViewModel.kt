package com.islamichub.feature.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.api.AiProvider
import com.islamichub.data.preferences.AppPreferences
import com.islamichub.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import javax.inject.Inject

data class AIMessage(
    val id: String,
    val role: Role,
    val content: String
) {
    enum class Role { USER, ASSISTANT }
}

/**
 * AIScholarViewModel — AI Scholar with user-supplied Gemini/OpenRouter keys.
 *
 * Per source ai-scholar.js:
 *  - Provider: gemini | openrouter
 *  - Default model: gemini-2.5-flash-lite
 *  - System prompt: Islamic scholar, answer in Bengali, advise verifying with scholars
 *
 * SECURITY: API keys are NEVER hardcoded. They come from AppPreferences
 * (user enters via Settings). If no key configured, falls back to local
 * answer lookup from ans-data.json.
 */
@HiltViewModel
class AIScholarViewModel @Inject constructor(
    private val assets: AssetRepository,
    private val prefs: AppPreferences,
    private val client: OkHttpClient
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AIMessage>>(emptyList())
    val messages: StateFlow<List<AIMessage>> = _messages.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val aiProvider = AiProvider(client)

    init {
        _messages.value = listOf(
            AIMessage(
                id = "welcome",
                role = AIMessage.Role.ASSISTANT,
                content = "আসসালামু আলাইকুম। আমি ইসলামিক স্কলার এআই। আপনার ইসলামিক প্রশ্ন করুন।\n\n⚠️ এআই উত্তর সর্বদা আলেমদের সাথে যাচাই করুন।\n\n💡 AI চালু করতে সেটিংসে গিয়ে Gemini/OpenRouter API key দিন। না হলে লোকাল উত্তর দেখাবে।"
            )
        )
    }

    fun ask(question: String) {
        val userMsg = AIMessage(
            id = "user_${System.currentTimeMillis()}",
            role = AIMessage.Role.USER,
            content = question
        )
        _messages.value = _messages.value + userMsg

        _isThinking.value = true
        viewModelScope.launch {
            try {
                val provider = prefs.aiProvider.first()
                val answer = when (provider) {
                    "gemini" -> {
                        val key = prefs.geminiApiKey.first()
                        val model = prefs.geminiModel.first()
                        if (key.isBlank()) findLocalAnswer(question)
                        else aiProvider.askGemini(key, model, buildPrompt(question))
                    }
                    "openrouter" -> {
                        val key = prefs.openRouterApiKey.first()
                        val model = prefs.openRouterModel.first()
                        if (key.isBlank()) findLocalAnswer(question)
                        else aiProvider.askOpenRouter(key, model, question)
                    }
                    else -> findLocalAnswer(question)
                }
                val aiMsg = AIMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    role = AIMessage.Role.ASSISTANT,
                    content = answer
                )
                _messages.value = _messages.value + aiMsg
            } catch (e: Exception) {
                val errMsg = AIMessage(
                    id = "err_${System.currentTimeMillis()}",
                    role = AIMessage.Role.ASSISTANT,
                    content = "ত্রুটি: ${e.message}\n\nলোকাল উত্তর খুঁজছি…\n\n${findLocalAnswerSync(question)}"
                )
                _messages.value = _messages.value + errMsg
            } finally {
                _isThinking.value = false
            }
        }
    }

    private fun buildPrompt(question: String): String {
        return """You are an Islamic scholar assistant. Answer the following question in Bengali (Bangla) language.
Be accurate, concise, and cite Quran/Hadith references where applicable.
Always advise the user to consult a qualified scholar (আলেম) for religious rulings (ফতোয়া).

User question: $question"""
    }

    private suspend fun findLocalAnswer(question: String): String {
        try {
            val answers = assets.answers()
            // AnswerData has explicit category fields (namaz, roza, hajj, etc.)
            val allAnswers = listOf(
                answers.namaz, answers.roza, answers.hajj, answers.zakat,
                answers.quran, answers.hadith, answers.iman, answers.ilm,
                answers.taharah, answers.nikah, answers.tijarah, answers.jihad
            ).flatten()

            for (a in allAnswers) {
                if (a.question.contains(question, true) || question.contains(a.question.take(20), true)) {
                    return buildString {
                        appendLine("📚 প্রশ্ন: ${a.question}")
                        appendLine()
                        appendLine("💬 উত্তর: ${a.answer}")
                        if (a.reference.isNotBlank()) {
                            appendLine()
                            appendLine("📎 সূত্র: ${a.reference}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Defensive
        }
        return "আপনার প্রশ্নটির সরাসরি উত্তর লোকাল ডেটাবেসে নেই। AI চালু করতে সেটিংসে API key দিন, অথবা আলেমদের সাথে পরামর্শ করুন।"
    }

    private fun findLocalAnswerSync(question: String): String {
        // Synchronous fallback for error path
        return "লোকাল উত্তর পাওয়া যায়নি। দয়া করে প্রশ্ন পরিবর্তন করে চেষ্টা করুন।"
    }
}
