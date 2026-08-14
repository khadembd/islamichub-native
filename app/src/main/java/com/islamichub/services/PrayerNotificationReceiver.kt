package com.islamichub.services

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.islamichub.MainActivity
import com.islamichub.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * PrayerNotificationReceiver — fires when a prayer time alarm triggers.
 * Posts the actual notification. Triggered by AlarmManager.
 */
@AndroidEntryPoint
class PrayerNotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var scheduler: PrayerNotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra("prayer_name") ?: return
        val notifId = intent.getIntExtra("notif_id", 0)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            data = android.net.Uri.parse("islamichub://prayer/$prayerName")
        }
        val pi = PendingIntent.getActivity(
            context, notifId, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, PrayerNotificationScheduler.CHANNEL_PRAYER)
            .setSmallIcon(R.drawable.ic_prayer)
            .setContentTitle(context.getString(R.string.channel_prayer_name))
            .setContentText("$prayerName নামাজের সময় হয়েছে")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        if (hasNotificationPermission(context)) {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        }

        // Schedule tomorrow's set after firing today's
        scheduler.rescheduleForToday()
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED
        } else true
    }
}
