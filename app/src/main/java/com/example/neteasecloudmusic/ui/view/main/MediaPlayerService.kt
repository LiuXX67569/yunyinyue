package com.example.neteasecloudmusic.ui.view.main

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder

class MediaPlayerService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return MediaPlayerBinder()
    }
    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService {
            return this@MediaPlayerService
        }
    }
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }
    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }


}