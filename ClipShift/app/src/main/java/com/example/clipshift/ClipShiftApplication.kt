package com.example.clipshift

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL

class ClipShiftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            // Wir initialisieren es SOFORT beim Start der App
            YoutubeDL.getInstance().init(this)
            FFmpeg.getInstance().init(this)
            Log.d("ClipShiftApp", "✅ YoutubeDL & FFmpeg erfolgreich initialisiert!")
        } catch (e: Exception) {
            Log.e("ClipShiftApp", "❌ Init fehlgeschlagen", e)
            Toast.makeText(this, "Fehler beim Start: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}