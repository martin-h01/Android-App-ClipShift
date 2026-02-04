package com.example.clipshift

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL

/**
 * Initialization of ytdlp for conversion
 */
class ClipShiftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            YoutubeDL.getInstance().init(this)
        } catch (e: Exception) {
            Log.e("ClipShiftApp", "Failed to initialize YoutubeDL", e)
        }
    }
}