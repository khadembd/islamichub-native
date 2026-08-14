package com.islamichub.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.EntryPointAccessors

/**
 * PrayerBootReceiver — reschedules all prayer notifications after device
 * reboot, app update, timezone change, or time set.
 *
 * ডিভাইস রিস্টার্ট বা timezone পরিবর্তনের পর সব prayer alarm পুনরায় সেট হয়।
 */
class PrayerBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        Thread {
            try {
                val entry = EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    PrayerSchedulerEntryPoint::class.java
                )
                entry.scheduler().rescheduleForToday()
            } finally {
                pending.finish()
            }
        }.start()
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface PrayerSchedulerEntryPoint {
    fun scheduler(): PrayerNotificationScheduler
}
