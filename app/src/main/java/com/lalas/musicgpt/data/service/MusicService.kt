package com.lalas.musicgpt.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    private var player: ExoPlayer? = null
    private var mediaLibrarySession: MediaLibrarySession? = null

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "music_playback_channel"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        player = ExoPlayer.Builder(this).build()

        // Add listener to handle playback state changes
        player!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED, Player.STATE_IDLE -> {
                        // Stop service when playback ends or becomes idle
                        if (!player!!.playWhenReady) {
                            stopSelf()
                        }
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (!playWhenReady) {
                    // When paused, make notification dismissible
                    stopForeground(STOP_FOREGROUND_DETACH)
                } else {
                    // When playing, keep as foreground service
                    // The notification will be managed by DefaultMediaNotificationProvider
                }
            }
        })

        mediaLibrarySession = MediaLibrarySession.Builder(this, player!!, object : MediaLibrarySession.Callback {
            override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
                super.onDisconnected(session, controller)
                // When notification is dismissed or connection lost, stop playback
                player?.let {
                    it.stop()
                    it.clearMediaItems()
                }
                stopSelf()
            }
        })
            .build()

        // Set up media notification
        setMediaNotificationProvider(
            DefaultMediaNotificationProvider.Builder(this)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .build()
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        mediaLibrarySession?.release()
        player?.release()
        super.onDestroy()
    }
}