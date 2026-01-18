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

    fun startDownload(url: String, format: String, resolution: String?, audioQuality: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDownloading.value = true
            _progress.value = 0f
            var finalFilePath: String? = null
            val lowerCaseFormat = format.lowercase().trim()

            try {
                // 1. Initialisierung
                if (!isEngineReady) {
                    _statusMsg.value = "Update Engine..."
                    try {
                        YoutubeDL.getInstance().init(getApplication())
                        YoutubeDL.getInstance().updateYoutubeDL(getApplication(), YoutubeDL.UpdateChannel.STABLE)
                        isEngineReady = true
                    } catch (e: Exception) {
                        Log.e("DownloadVM", "Init Fehler (ignorierbar)", e)
                    }
                }

                _statusMsg.value = "Starte Download..."
                val request = YoutubeDLRequest(url)

                // 2. Speicherort
                val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val subDir = if (lowerCaseFormat == "mp3") "ClipShift-Audio" else "ClipShift-Video"
                val targetDir = File(baseDir, subDir)
                if (!targetDir.exists()) targetDir.mkdirs()

                // -----------------------------------------------------------------
                // DATEINAME GENEIEREN (Mit Suffix für Quali/Auflösung)
                // -----------------------------------------------------------------
                var fileNameSuffix = ""

                if (lowerCaseFormat == "mp4" && !resolution.isNullOrBlank()) {
                    // MP4: Auflösung anhängen (z.B. "_1080p")
                    fileNameSuffix = "_$resolution"
                }
                else if (lowerCaseFormat == "mp3" && !audioQuality.isNullOrBlank()) {
                    // MP3: Bitrate aus dem String filtern (z.B. "_320k")
                    fileNameSuffix = when {
                        audioQuality.contains("320") -> "_320k"
                        audioQuality.contains("256") -> "_256k"
                        audioQuality.contains("192") -> "_192k"
                        audioQuality.contains("128") -> "_128k"
                        else -> ""
                    }
                }

                // Output-Template setzen: Titel + Suffix + Endung
                request.addOption("-o", "${targetDir.absolutePath}/%(title)s${fileNameSuffix}.%(ext)s")


                // 3. FFmpeg Pfad (Deine 150MB Datei)
                val nativeDir = getApplication<Application>().applicationInfo.nativeLibraryDir
                request.addOption("--ffmpeg-location", "${nativeDir}/libffmpeg.so")
                request.addOption("--rm-cache-dir")


                // 4. FORMAT-LOGIK
                if (lowerCaseFormat == "mp3") {
                    // --- MP3 ---
                    request.addOption("-x")
                    request.addOption("--audio-format", "mp3")
                    request.addOption("--prefer-ffmpeg")
                    request.addOption("-f", "bestaudio/best")

                    // Qualität an yt-dlp übergeben
                    val bitrate = when (audioQuality) {
                        "MP3 320 kBit/s (Beste)" -> "320K"
                        "MP3 256 kBit/s (Hoch)" -> "256K"
                        "MP3 192 kBit/s (Gut)" -> "192K"
                        "MP3 128 kBit/s (Standard)" -> "128K"
                        else -> "192K" // Fallback
                    }
                    request.addOption("--audio-quality", bitrate)

                } else if (lowerCaseFormat == "mp4") {
                    // --- MP4 ---
                    request.addOption("--merge-output-format", "mp4")

                    if (resolution.isNullOrBlank()) {
                        // Einfacher Modus
                        request.addOption("-f", "bestvideo[height<=1080][ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
                    } else {
                        // Expertenmodus
                        val height = resolution.replace("p", "")
                        request.addOption("-f", "bv*[height<=${height}][ext=mp4]+ba[ext=m4a]/b[height<=${height}]/best")
                    }
                }

                // 5. Ausführen
                YoutubeDL.getInstance().execute(request) { progress, _, line ->
                    _progress.value = progress / 100f
                    _statusMsg.value = "Lade... ${progress.toInt()}%"
                    getFileNameFromLog(line)?.let { finalFilePath = it }
                }

                renameAndSetSuccess(finalFilePath, lowerCaseFormat)

            } catch (e: Exception) {
                handleError(e, finalFilePath)
            } finally {
                _isDownloading.value = false
            }
        }
    }

    private fun getFileNameFromLog(line: String): String? {
        return when {
            line.startsWith("[Merger] Merging formats into \"") -> line.substringAfter("[Merger] Merging formats into \"").trimEnd('"')
            line.contains("Destination: ") && line.contains("ClipShift") -> line.substringAfter("Destination: ").trim()
            line.startsWith("[download] Destination: ") -> line.substringAfter("[download] Destination: ").trim()
            else -> null
        }
    }

    private fun renameAndSetSuccess(filePath: String?, format: String) {
        val file = if (filePath != null) File(filePath) else return

        // MP3 Endung sicherstellen
        if (format == "mp3" && file.exists()) {
            val correctName = file.absolutePath.substringBeforeLast(".") + ".mp3"
            val newFile = File(correctName)
            if (file.absolutePath != newFile.absolutePath) {
                file.renameTo(newFile)
            }
        }
        _statusMsg.value = "✅ Fertig!"
    }

    private fun handleError(e: Exception, filePath: String?) {
        val msg = e.message ?: ""
        Log.e("DownloadViewModel", "Fehler", e)

        if (msg.contains("Postprocessing") && filePath != null && File(filePath).exists()) {
            renameAndSetSuccess(filePath, "mp3")
            _progress.value = 1.0f
            return
        }
        _statusMsg.value = "Fehler: ${msg.take(50)}"
    }
}