package com.islamichub.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.islamichub.MainActivity
import com.islamichub.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * AudioPlaybackService — background audio playback using Media3.
 *
 * পুরোপুরি নেটিভ implementation — browser `<audio>` নয়।
 * Used for:
 *  - Namaz MP3 audio (azan, surahs, dua)
 *  - Future Quran recitation
 *
 * Playback continues in background with media notification controls.
 */
@AndroidEntryPoint
class AudioPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    @Inject
    lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        try {
            val sessionActivityIntent = packageManager.getLaunchIntentForPackage(packageName)
            val pendingIntent = sessionActivityIntent?.let {
                PendingIntent.getActivity(
                    this, 0, it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
            val builder = MediaSession.Builder(this, player)
            if (pendingIntent != null) {
                builder.setSessionActivity(pendingIntent)
            }
            mediaSession = builder.build()
        } catch (e: Exception) {
            // Defensive: MediaSession creation should never crash app
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        try {
            if (!player.playWhenReady || player.mediaItemCount == 0) {
                stopSelf()
            }
        } catch (e: Exception) {
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        try {
            mediaSession?.run {
                player.release()
                release()
            }
            mediaSession = null
        } catch (e: Exception) {
            // Defensive cleanup
        }
        super.onDestroy()
    }
}
