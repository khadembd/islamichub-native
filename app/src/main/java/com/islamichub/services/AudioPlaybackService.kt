package com.islamichub.services

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * AudioPlaybackService — background audio playback using Media3.
 *
 * পুরোপুরি নেটিভ implementation — browser `<audio>` নয়।
 * Used for:
 *  - Namaz MP3 audio (azan, surahs, dua)
 *  - Future Quran recitation
 *
 * Playback continues in background with media notification controls.
 *
 * NOTE: Does NOT use @AndroidEntryPoint to avoid Hilt initialization
 * timing issues. ExoPlayer is created lazily when session is requested.
 */
class AudioPlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        try {
            player = ExoPlayer.Builder(this)
                .setHandleAudioBecomingNoisy(true)
                .build()
            val sessionActivityIntent = packageManager.getLaunchIntentForPackage(packageName)
            val pendingIntent = sessionActivityIntent?.let {
                PendingIntent.getActivity(
                    this, 0, it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
            val builder = MediaSession.Builder(this, player!!)
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
            val p = player
            if (p != null && (!p.playWhenReady || p.mediaItemCount == 0)) {
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
            player = null
        } catch (e: Exception) {
            // Defensive cleanup
        }
        super.onDestroy()
    }
}
