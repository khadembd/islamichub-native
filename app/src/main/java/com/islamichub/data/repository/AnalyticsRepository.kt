package com.islamichub.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AnalyticsRepository — per source analytics-service.js
 * Tracks events and pushes to Firestore collection 'analytics_events'.
 */
@Singleton
class AnalyticsRepository @Inject constructor() {

    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    suspend fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        withContext(Dispatchers.IO) {
            try {
                val event = hashMapOf(
                    "event" to eventName,
                    "params" to params,
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("analytics_events").add(event).await()
            } catch (e: Exception) {
                // Defensive: analytics should never crash app
            }
        }
    }

    suspend fun logScreenView(screenName: String) {
        logEvent("screen_view", mapOf("screen" to screenName))
    }

    suspend fun logFeatureUse(feature: String) {
        logEvent("feature_use", mapOf("feature" to feature))
    }

    suspend fun logBookmark(type: String, id: String) {
        logEvent("bookmark_added", mapOf("type" to type, "id" to id))
    }

    suspend fun logZikr(zikrType: String, count: Int) {
        logEvent("zikr_count", mapOf("type" to zikrType, "count" to count))
    }

    suspend fun logPrayerMarked(prayerId: String, completed: Boolean) {
        logEvent("prayer_marked", mapOf("prayer" to prayerId, "completed" to completed))
    }
}
