package com.islamichub

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Islamic Hub — Application class
 * অ্যাপ্লিকেশন এন্ট্রি পয়েন্ট। Hilt DI + WorkManager initialization।
 */
@HiltAndroidApp
class IslamicHubApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Schedule prayer notifications + daily content refresh on app launch.
        // Rescheduling on every cold-start is idempotent — AlarmManager dedupes
        // via PendingIntent.FLAG_UPDATE_CURRENT.
        val entry = dagger.hilt.android.EntryPointAccessors.fromApplication(
            this, com.islamichub.services.PrayerSchedulerEntryPoint::class.java
        )
        entry.scheduler().rescheduleForToday()
    }
}
