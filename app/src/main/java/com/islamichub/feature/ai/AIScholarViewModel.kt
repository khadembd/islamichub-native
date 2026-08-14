package com.islamichub.feature.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AIMessage(
    val id: String,
    val role: Role,
    val content: String
) {
    enum class Role { USER, ASSISTANT }
}

@HiltViewModel
class AIScholarViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AIMessage>>(emptyList())
    val messages: StateFlow<List<AIMessage>> = _messages.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    init {
        // Welcome message
        _messages.value = listOf(
            AIMessage(
                id = "welcome",
                role = AIMessage.Role.ASSISTANT,
                content = "আসসালামু আলাইকুম। আমি ইসলামিক স্কলার এআই। আপনার ইসলামিক প্রশ্ন করুন — আমি চেষ্টা করব সহজ বাংলায় উত্তর দিতে।\n\n⚠️ এআই উত্তর সর্বদা আলেমদের সাথে যাচাই করুন।"
            )
        )
    }

    fun ask(question: String) {
        // Add user message
        val userMsg = AIMessage(
            id = "user_${System.currentTimeMillis()}",
            role = AIMessage.Role.USER,
            content = question
        )
        _messages.value = _messages.value + userMsg

        _isThinking.value = true
        viewModelScope.launch {
            try {
                // v1: local answer lookup from ans-data.json
                // Future: Retrofit → backend AI proxy (no hardcoded keys in APK)
                val answer = findLocalAnswer(question)
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
                    content = "দুঃখিত, এই মুহূর্তে উত্তর দেওয়া সম্ভব হচ্ছে না। পরে আবার চেষ্টা করুন।"
                )
                _messages.value = _messages.value + errMsg
            } finally {
                _isThinking.value = false
            }
        }
    }

    private suspend fun findLocalAnswer(question: String): String {
        val answers = assets.answers()
        val q = question.lowercase().trim()
        // Search through all answer categories for best match
        for ((_, list) in answers.answers) {
            for (a in list) {
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
        }
        // Fallback: no direct match
        return "আপনার প্রশ্নটির সরাসরি উত্তর আমার লোকাল ডেটাবেসে নেই। আরও নির্দিষ্ট করে প্রশ্ন করুন বা অনুগ্রহ করে আলেমদের সাথে পরামর্শ করুন।"
    }
}
