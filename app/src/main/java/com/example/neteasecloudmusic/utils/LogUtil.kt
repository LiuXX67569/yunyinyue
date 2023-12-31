package com.example.neteasecloudmusic.utils

import android.util.Log

object LogUtil {
    fun log(message: String) {
        Log.e("my error", message)
    }
    fun debug(message: String) {
        Log.d("show me", message)
    }
}