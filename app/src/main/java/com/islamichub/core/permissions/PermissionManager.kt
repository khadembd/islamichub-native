package com.islamichub.core.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PermissionManager — per source permission-service.js
 * Handles rationale, denied, permanently denied, and settings navigation.
 */
@Singleton
class PermissionManager @Inject constructor() {

    fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun isLocationGranted(context: Context): Boolean {
        return isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
               isGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    fun isCameraGranted(context: Context): Boolean =
        isGranted(context, Manifest.permission.CAMERA)

    fun isMicrophoneGranted(context: Context): Boolean =
        isGranted(context, Manifest.permission.RECORD_AUDIO)

    fun isNotificationsGranted(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            isGranted(context, Manifest.permission.POST_NOTIFICATIONS)
        } else true
    }

    fun requestLocation(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    fun requestCamera(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    fun requestMicrophone(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.RECORD_AUDIO)
    }

    fun requestNotifications(launcher: ActivityResultLauncher<String>) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Defensive
        }
    }

    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return activity.shouldShowRequestPermissionRationale(permission)
    }
}
