package com.islamichub.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * MediaModule — provides ExoPlayer singleton for fragments that need
 * direct access (e.g., QuranFragment for surah audio preview).
 *
 * AudioPlaybackService creates its own ExoPlayer internally for
 * background playback — this is a separate instance for UI-level use.
 */
@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return try {
            ExoPlayer.Builder(context)
                .setHandleAudioBecomingNoisy(true)
                .build()
        } catch (e: Exception) {
            // Fallback: return a basic ExoPlayer if config fails
            ExoPlayer.Builder(context).build()
        }
    }
}
