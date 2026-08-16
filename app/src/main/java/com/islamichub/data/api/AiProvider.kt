package com.islamichub.data.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * AiProvider — abstraction over Gemini + OpenRouter.
 *
 * Per source ai-scholar.js + tajbeed-checker.js + vision-scanner.js:
 *  - Gemini: https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}
 *  - OpenRouter: https://openrouter.ai/api/v1/chat/completions
 *
 * SECURITY: API keys are NEVER hardcoded. They are read from AppPreferences
 * (user-supplied via Settings screen). The app ships with empty keys.
 */
class AiProvider(private val client: OkHttpClient) {

    suspend fun askGemini(apiKey: String, model: String, prompt: String): String = suspendCoroutine { cont ->
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
            val body = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }.toString()

            val req = Request.Builder()
                .url(url)
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    cont.resumeWithException(Exception("Gemini API ${resp.code}"))
                    return@use
                }
                val json = resp.body?.string().orEmpty()
                val text = try {
                    JSONObject(json)
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                } catch (e: Exception) { "" }
                cont.resume(text)
            }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }

    suspend fun askOpenRouter(apiKey: String, model: String, prompt: String): String = suspendCoroutine { cont ->
        try {
            val body = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are an Islamic scholar assistant. Answer in Bengali. Always advise consulting a qualified scholar for religious rulings.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }.toString()

            val req = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .post(body.toRequestBody("application/json".toMediaType()))
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("HTTP-Referer", "https://jubosongho.com")
                .addHeader("X-Title", "Islamic Hub")
                .build()

            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) {
                    cont.resumeWithException(Exception("OpenRouter ${resp.code}"))
                    return@use
                }
                val json = resp.body?.string().orEmpty()
                val text = try {
                    JSONObject(json)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                } catch (e: Exception) { "" }
                cont.resume(text)
            }
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }
}
