package com.islamichub.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.islamichub.R
import com.islamichub.data.model.PrayerTimes
import com.islamichub.data.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PrayerNotificationScheduler — schedules prayer time notifications
 * using AlarmManager (exact alarm for time-critical prayer reminders).
 *
 * পুরোপুরি নেটিভ — browser Notification API ব্যবহৃত হয় না।
 */
@Singleton
class PrayerNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: AppPreferences
) {
    companion object {
        const val CHANNEL_PRAYER = "prayer_notifications"
        const val CHANNEL_DAILY = "daily_content"

        // Notification IDs — stable so we can cancel/update individual prayers
        const val NOTIF_ID_FAJR = 1001
        const val NOTIF_ID_DHUHR = 1002
        const val NOTIF_ID_ASR = 1003
        const val NOTIF_ID_MAGHRIB = 1004
        const val NOTIF_ID_ISHA = 1005
        const val NOTIF_ID_DAILY = 1100

        fun scheduleAll(context: Context) {
            // Trigger scheduling via Hilt-injected singleton.
            // This companion function is called from Application.onCreate() —
            // it does NOT directly access Hilt; instead it sends an intent
            // that the work manager picks up.
            PrayerBootReceiver.scheduleAll(context)
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun ensureChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)
            nm?.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_PRAYER,
                    context.getString(R.string.channel_prayer_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = context.getString(R.string.channel_prayer_desc) }
            )
            nm?.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_DAILY,
                    context.getString(R.string.channel_daily_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = context.getString(R.string.channel_daily_desc) }
            )
        }
    }

    fun rescheduleForToday() {
        scope.launch {
            val enabled = preferences.notificationsEnabled.first()
            if (!enabled) return@launch
            val lat = preferences.latitude.first()
            val lon = preferences.longitude.first()
            val tz = zoneOffsetHours()
            val method = PrayerTimeCalculator.MWL
            val madhab = preferences.madhab.first()
            val asrFactor = if (madhab == "hanafi") 2 else 1
            val offsetMin = preferences.notificationOffsetMin.first()
            val times = PrayerTimeCalculator.compute(
                LocalDate.now(), lat, lon, tz, method, asrFactor
            )
            ensureChannels()
            schedulePrayerNotification(times.fajr,    "Fajr",    NOTIF_ID_FAJR,    offsetMin)
            schedulePrayerNotification(times.dhuhr,   "Dhuhr",   NOTIF_ID_DHUHR,   offsetMin)
            schedulePrayerNotification(times.asr,     "Asr",     NOTIF_ID_ASR,     offsetMin)
            schedulePrayerNotification(times.maghrib, "Maghrib", NOTIF_ID_MAGHRIB, offsetMin)
            schedulePrayerNotification(times.isha,    "Isha",    NOTIF_ID_ISHA,    offsetMin)

            preferences.setLastPrayerScheduledDate(LocalDate.now().toString())
        }
    }

    private fun schedulePrayerNotification(timeMillis: Long, name: String, notifId: Int, offsetMin: Int) {
        val triggerAt = timeMillis - offsetMin * 60_000L
        if (triggerAt < System.currentTimeMillis()) return  // skip past times

        val intent = Intent(context, PrayerNotificationReceiver::class.java).apply {
            action = "PRAYER_TIME"
            putExtra("prayer_name", name)
            putExtra("notif_id", notifId)
        }
        val pi = PendingIntent.getBroadcast(
            context, notifId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            // Fall back to inexact alarm if exact alarms not permitted
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    fun cancelAll() {
        val am = context.getSystemService(AlarmManager::class.java) ?: return
        listOf(NOTIF_ID_FAJR, NOTIF_ID_DHUHR, NOTIF_ID_ASR, NOTIF_ID_MAGHRIB, NOTIF_ID_ISHA, NOTIF_ID_DAILY)
            .forEach { id ->
                val intent = Intent(context, PrayerNotificationReceiver::class.java).apply { action = "PRAYER_TIME" }
                val pi = PendingIntent.getBroadcast(
                    context, id, intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pi != null) am.cancel(pi)
            }
    }

    private fun zoneOffsetHours(): Double {
        val offset = java.time.ZoneId.systemDefault().rules.getOffset(java.time.Instant.now())
        return offset.totalSeconds / 3600.0
    }
}
