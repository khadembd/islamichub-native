package com.islamichub.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App preferences — DataStore-backed.
 * Stores: theme, prayer method, madhab, location, notification settings, app lock.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "islamichub_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")        // system | light | dark
        val PRAYER_METHOD = stringPreferencesKey("prayer_method")  // MWL | KARACHI | EGYPT | ...
        val MADHAB = stringPreferencesKey("madhab")                // shafi | hanafi
        val LATITUDE = stringPreferencesKey("latitude")
        val LONGITUDE = stringPreferencesKey("longitude")
        val LOCATION_NAME = stringPreferencesKey("location_name")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_OFFSET_MIN = intPreferencesKey("notif_offset_min")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LANGUAGE = stringPreferencesKey("app_language")    // en | bn
        val LAST_PRAYER_SCHEDULED_DATE = stringPreferencesKey("last_prayer_scheduled_date")
        // AI provider keys (user-supplied, never hardcoded)
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val GEMINI_MODEL = stringPreferencesKey("gemini_model")
        val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        val OPENROUTER_MODEL = stringPreferencesKey("openrouter_model")
        val AI_PROVIDER = stringPreferencesKey("ai_provider")      // gemini | openrouter | local
        // Custom Jamaat offsets (minutes)
        val JAMAAT_FAJR = intPreferencesKey("jamaat_fajr")
        val JAMAAT_DHUHR = intPreferencesKey("jamaat_dhuhr")
        val JAMAAT_ASR = intPreferencesKey("jamaat_asr")
        val JAMAAT_MAGHRIB = intPreferencesKey("jamaat_maghrib")
        val JAMAAT_ISHA = intPreferencesKey("jamaat_isha")
        // Reading tracker
        val READING_PROGRESS = stringPreferencesKey("reading_progress")
        // User profile
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_UID = stringPreferencesKey("user_uid")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { it[Keys.THEME_MODE] ?: "system" }
    val prayerMethod: Flow<String> = context.dataStore.data.map { it[Keys.PRAYER_METHOD] ?: "MWL" }
    val madhab: Flow<String> = context.dataStore.data.map { it[Keys.MADHAB] ?: "shafi" }
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }
    val notificationOffsetMin: Flow<Int> = context.dataStore.data.map { it[Keys.NOTIFICATION_OFFSET_MIN] ?: 0 }
    val appLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.APP_LOCK_ENABLED] ?: false }
    val appLanguage: Flow<String> = context.dataStore.data.map { it[Keys.APP_LANGUAGE] ?: "bn" }

    val locationName: Flow<String> = context.dataStore.data.map { it[Keys.LOCATION_NAME] ?: "Dhaka, Bangladesh" }
    val latitude: Flow<Double> = context.dataStore.data.map { (it[Keys.LATITUDE] ?: "23.8103").toDoubleOrNull() ?: 23.8103 }
    val longitude: Flow<Double> = context.dataStore.data.map { (it[Keys.LONGITUDE] ?: "90.4125").toDoubleOrNull() ?: 90.4125 }

    suspend fun setThemeMode(mode: String) = context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    suspend fun setPrayerMethod(method: String) = context.dataStore.edit { it[Keys.PRAYER_METHOD] = method }
    suspend fun setMadhab(madhab: String) = context.dataStore.edit { it[Keys.MADHAB] = madhab }
    suspend fun setNotificationsEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    suspend fun setNotificationOffsetMin(min: Int) = context.dataStore.edit { it[Keys.NOTIFICATION_OFFSET_MIN] = min }
    suspend fun setAppLockEnabled(enabled: Boolean) = context.dataStore.edit { it[Keys.APP_LOCK_ENABLED] = enabled }
    suspend fun setAppLanguage(lang: String) = context.dataStore.edit { it[Keys.APP_LANGUAGE] = lang }
    suspend fun setLocation(name: String, lat: Double, lon: Double) = context.dataStore.edit {
        it[Keys.LOCATION_NAME] = name
        it[Keys.LATITUDE] = lat.toString()
        it[Keys.LONGITUDE] = lon.toString()
    }
    suspend fun setLastPrayerScheduledDate(date: String) = context.dataStore.edit {
        it[Keys.LAST_PRAYER_SCHEDULED_DATE] = date
    }

    // AI keys (user-supplied)
    val geminiApiKey: Flow<String> = context.dataStore.data.map { it[Keys.GEMINI_API_KEY] ?: "" }
    val geminiModel: Flow<String> = context.dataStore.data.map { it[Keys.GEMINI_MODEL] ?: "gemini-2.5-flash-lite" }
    val openRouterApiKey: Flow<String> = context.dataStore.data.map { it[Keys.OPENROUTER_API_KEY] ?: "" }
    val openRouterModel: Flow<String> = context.dataStore.data.map { it[Keys.OPENROUTER_MODEL] ?: "openai/gpt-4o-mini" }
    val aiProvider: Flow<String> = context.dataStore.data.map { it[Keys.AI_PROVIDER] ?: "local" }

    suspend fun setGeminiApiKey(key: String) = context.dataStore.edit { it[Keys.GEMINI_API_KEY] = key }
    suspend fun setGeminiModel(model: String) = context.dataStore.edit { it[Keys.GEMINI_MODEL] = model }
    suspend fun setOpenRouterApiKey(key: String) = context.dataStore.edit { it[Keys.OPENROUTER_API_KEY] = key }
    suspend fun setOpenRouterModel(model: String) = context.dataStore.edit { it[Keys.OPENROUTER_MODEL] = model }
    suspend fun setAiProvider(provider: String) = context.dataStore.edit { it[Keys.AI_PROVIDER] = provider }

    // Custom Jamaat offsets
    val jamaatFajr: Flow<Int> = context.dataStore.data.map { it[Keys.JAMAAT_FAJR] ?: 15 }
    val jamaatDhuhr: Flow<Int> = context.dataStore.data.map { it[Keys.JAMAAT_DHUHR] ?: 15 }
    val jamaatAsr: Flow<Int> = context.dataStore.data.map { it[Keys.JAMAAT_ASR] ?: 15 }
    val jamaatMaghrib: Flow<Int> = context.dataStore.data.map { it[Keys.JAMAAT_MAGHRIB] ?: 10 }
    val jamaatIsha: Flow<Int> = context.dataStore.data.map { it[Keys.JAMAAT_ISHA] ?: 15 }
    suspend fun setJamaatFajr(min: Int) = context.dataStore.edit { it[Keys.JAMAAT_FAJR] = min }
    suspend fun setJamaatDhuhr(min: Int) = context.dataStore.edit { it[Keys.JAMAAT_DHUHR] = min }
    suspend fun setJamaatAsr(min: Int) = context.dataStore.edit { it[Keys.JAMAAT_ASR] = min }
    suspend fun setJamaatMaghrib(min: Int) = context.dataStore.edit { it[Keys.JAMAAT_MAGHRIB] = min }
    suspend fun setJamaatIsha(min: Int) = context.dataStore.edit { it[Keys.JAMAAT_ISHA] = min }

    // User profile
    val userName: Flow<String> = context.dataStore.data.map { it[Keys.USER_NAME] ?: "" }
    val userEmail: Flow<String> = context.dataStore.data.map { it[Keys.USER_EMAIL] ?: "" }
    val userUid: Flow<String> = context.dataStore.data.map { it[Keys.USER_UID] ?: "" }
    suspend fun setUserName(name: String) = context.dataStore.edit { it[Keys.USER_NAME] = name }
    suspend fun setUserEmail(email: String) = context.dataStore.edit { it[Keys.USER_EMAIL] = email }
    suspend fun setUserUid(uid: String) = context.dataStore.edit { it[Keys.USER_UID] = uid }

    // Reading progress (JSON string of logs)
    val readingProgress: Flow<String> = context.dataStore.data.map { it[Keys.READING_PROGRESS] ?: "" }
    suspend fun readingProgressSync(): String = readingProgress.first()
    suspend fun setReadingProgressSync(value: String) = context.dataStore.edit { it[Keys.READING_PROGRESS] = value }
}
