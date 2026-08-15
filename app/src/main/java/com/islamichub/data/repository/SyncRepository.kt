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
 * SyncRepository — per source sync-service.js
 * Synchronizes bookmarks, salah logs, reading progress, and settings
 * with Firestore collections.
 */
@Singleton
class SyncRepository @Inject constructor() {

    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    suspend fun pushBookmarks(uid: String, bookmarks: List<Map<String, Any>>) {
        withContext(Dispatchers.IO) {
            try {
                db.collection("users").document(uid)
                    .collection("bookmarks")
                    .document("sync")
                    .set(mapOf("items" to bookmarks, "syncedAt" to System.currentTimeMillis()))
                    .await()
            } catch (e: Exception) {
                // Defensive: sync failure should not crash app
            }
        }
    }

    suspend fun pushSalahLogs(uid: String, logs: List<Map<String, Any>>) {
        withContext(Dispatchers.IO) {
            try {
                db.collection("users").document(uid)
                    .collection("salah_logs")
                    .document("sync")
                    .set(mapOf("logs" to logs, "syncedAt" to System.currentTimeMillis()))
                    .await()
            } catch (e: Exception) {
                // Defensive
            }
        }
    }

    suspend fun pushReadingProgress(uid: String, progress: List<Map<String, Any>>) {
        withContext(Dispatchers.IO) {
            try {
                db.collection("users").document(uid)
                    .collection("reading_progress")
                    .document("sync")
                    .set(mapOf("progress" to progress, "syncedAt" to System.currentTimeMillis()))
                    .await()
            } catch (e: Exception) {
                // Defensive
            }
        }
    }

    suspend fun pushSettings(uid: String, settings: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            try {
                db.collection("users").document(uid)
                    .collection("settings")
                    .document("sync")
                    .set(settings)
                    .await()
            } catch (e: Exception) {
                // Defensive
            }
        }
    }

    suspend fun pullBookmarks(uid: String): List<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val doc = db.collection("users").document(uid)
                    .collection("bookmarks").document("sync").get().await()
                @Suppress("UNCHECKED_CAST")
                (doc.get("items") as? List<Map<String, Any>>) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun pullSalahLogs(uid: String): List<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                val doc = db.collection("users").document(uid)
                    .collection("salah_logs").document("sync").get().await()
                @Suppress("UNCHECKED_CAST")
                (doc.get("logs") as? List<Map<String, Any>>) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
