package com.lalas.musicgpt.ui.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.ComponentName
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.lalas.musicgpt.data.MusicConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.guava.await

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == MusicConstants.ACTION_NOTIFICATION_DISMISSED) {
            // Stop the music service
            val serviceIntent = Intent(context, MusicService::class.java)
            context.stopService(serviceIntent)

            // Also try to stop the media controller
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
                    val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
                    val controller = controllerFuture.await()
                    controller.stop()
                    controller.release()
                } catch (e: Exception) {
                    // Handle any errors gracefully
                }
            }
        }
    }
}