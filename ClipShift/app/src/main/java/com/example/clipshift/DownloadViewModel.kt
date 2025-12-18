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
import java.io.File

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

    private val _statusMsg = MutableStateFlow("Bereit")
    val statusMsg = _statusMsg.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    private var isEngineReady = false

    fun startDownload(url: String, format: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDownloading.value = true
            var finalFilePath: String? = null
            val lowerCaseFormat = format.lowercase().trim()

            try {
                if (!isEngineReady) {
                    _statusMsg.value = "Update Engine..."
                    try {
                        YoutubeDL.getInstance().init(getApplication())
                        YoutubeDL.getInstance().updateYoutubeDL(getApplication(), YoutubeDL.UpdateChannel.STABLE)
                        isEngineReady = true
                    } catch (e: Exception) {
                        handleError(e, finalFilePath, lowerCaseFormat)
                        return@launch
                    }
                }

                _statusMsg.value = "Starte Download..."
                val request = YoutubeDLRequest(url)

                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                request.addOption("-o", "${downloadsDir.absolutePath}/%(title)s.%(ext)s")

                val nativeDir = getApplication<Application>().applicationInfo.nativeLibraryDir
                request.addOption("--ffmpeg-location", "${nativeDir}/libffmpeg.so")
                request.addOption("--rm-cache-dir")
                request.addOption("--extractor-args", "youtube:player_client=android")

                if (lowerCaseFormat == "mp3") {
                    request.addOption("-f", "bestaudio/best")
                    request.addOption("-x")
                    request.addOption("--audio-format", "mp3")
                } else {
                    request.addOption("-f", "bestvideo+bestaudio/best")
                    request.addOption("--remux-video", lowerCaseFormat)
                }

                YoutubeDL.getInstance().execute(request) { progress, _, line ->
                    _progress.value = progress / 100f
                    _statusMsg.value = "Lade... ${progress.toInt()}%"
                    // Finde den finalen Dateinamen aus den Logs
                    getFileNameFromLog(line)?.let { finalFilePath = it }
                }

                // Erfolgsfall: Versuche umzubenennen und setze die Nachricht.
                renameAndSetSuccess(finalFilePath, lowerCaseFormat)

            } catch (e: Exception) {
                handleError(e, finalFilePath, lowerCaseFormat)
            } finally {
                _isDownloading.value = false
            }
        }
    }

    private fun getFileNameFromLog(line: String): String? {
        return when {
            line.startsWith("[Merger] Merging formats into \"") -> line.substringAfter("[Merger] Merging formats into \"").trimEnd('"')
            line.startsWith("[download] Destination: ") -> line.substringAfter("[download] Destination: ")
            line.startsWith("[ExtractAudio] Destination: ") -> line.substringAfter("[ExtractAudio] Destination: ")
            else -> null
        }
    }

    private fun renameAndSetSuccess(filePath: String?, format: String) {
        val file = if (filePath != null) File(filePath) else return

        if (format == "mp3" && file.exists() && !file.name.endsWith(".mp3", true)) {
            val newFile = File(file.parent, "${file.nameWithoutExtension}.mp3")
            if (file.renameTo(newFile)) {
                _statusMsg.value = "✅ Fertig! (Umbenannt zu MP3)"
            } else {
                _statusMsg.value = "✅ Fertig! (Umbenennen fehlgeschlagen)"
            }
        } else {
            _statusMsg.value = "✅ Fertig!"
        }
    }

    private fun handleError(e: Exception, filePath: String?, format: String) {
        val msg = e.message ?: ""
        Log.e("DownloadViewModel", "Download-Fehler", e)

        // DEINE LOGIK: Auch bei Post-Processing-Fehlern umbenennen!
        if (msg.contains("ffprobe", true) || msg.contains("Postprocessing", true)) {
            renameAndSetSuccess(filePath, format)
            _progress.value = 1.0f
            return
        }

        _statusMsg.value = when {
            msg.contains("403") -> "❌ YouTube blockt (Update fehlgeschlagen?)"
            else -> "Fehler: ${msg.take(100)}"
        }
    }
}