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
}
