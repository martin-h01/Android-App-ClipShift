package com.example.clipshift

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL

class ClipShiftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Wir initialisieren NUR NOCH YoutubeDL.
            // FFmpeg machen wir jetzt manuell im ViewModel.
            YoutubeDL.getInstance().init(this)
        } catch (e: Exception) {
            Log.e("ClipShiftApp", "Fehler beim Init", e)
        }
    }
}