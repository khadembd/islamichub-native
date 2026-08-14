package com.islamichub.core.utils

import android.util.Log
import kotlin.system.exitProcess

/**
 * CrashHandler — global uncaught exception handler.
 *
 * Catches any uncaught exception, logs it, and prevents the app from
 * showing the system "App crashed" dialog where possible.
 *
 * পুরোপুরি নেটিভ — Thread.UncaughtExceptionHandler implementation.
 */
class CrashHandler : Thread.UncaughtExceptionHandler {

    private val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            Log.e("IslamicHubCrash", "Uncaught exception on ${t.name}", e)
            // Future: send crash report to Firebase Crashlytics / Firestore
        } catch (_: Throwable) {
            // even logging failed — just bail out silently
        }
        // Let the default handler show the standard crash dialog
        defaultHandler?.uncaughtException(t, e) ?: run {
            exitProcess(2)
        }
    }

    companion object {
        fun install() {
            try {
                Thread.setDefaultUncaughtExceptionHandler(CrashHandler())
            } catch (_: Throwable) {
                // Defensive: if installation fails, continue without it
            }
        }
    }
}
