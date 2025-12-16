package com.example.clipshift

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val _statusMsg = MutableStateFlow("Bereit")
    val statusMsg = _statusMsg.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    fun startDownload(url: String, format: String) {
        if (url.isBlank()) {
            _statusMsg.value = "Bitte URL eingeben"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isDownloading.value = true
            _progress.value = 0f

            try {
                // Update versuchen (aber nicht abstürzen wenns nicht klappt)
                try {
                    YoutubeDL.getInstance().updateYoutubeDL(getApplication())
                } catch (e: Exception) {
                    Log.w("Update", "Update ging nicht: ${e.message}")
                }

                _statusMsg.value = "Starte $format Download..."

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val request = YoutubeDLRequest(url)
                request.addOption("-o", downloadsDir.absolutePath + "/%(title)s.%(ext)s")

                if (format == "MP3") {
                    request.addOption("-f", "bestaudio/best")
                    request.addOption("-x")
                    request.addOption("--audio-format", "mp3")
                } else {
                    request.addOption("-f", "bestvideo+bestaudio/best")
                    request.addOption("--merge-output-format", "mp4")
                }

                YoutubeDL.getInstance().execute(request) { progress, _, _ ->
                    _progress.value = progress / 100f
                    _statusMsg.value = "Lade... ${progress.toInt()}%"
                }

                _statusMsg.value = "✅ Fertig!"

            } catch (e: Exception) {
                // Hier sehen wir den echten Fehler
                _statusMsg.value = "❌ Fehler: ${e.message}"
                Log.e("Download", "Fehler", e)
            } finally {
                _isDownloading.value = false
            }
        }
    }
}