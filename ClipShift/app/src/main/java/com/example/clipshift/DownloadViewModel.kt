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
                        handleError(e, finalFilePath, lowerCaseFormat, resolution)
                        return@launch
                    }
                }

                _statusMsg.value = "Starte Download..."
                val request = YoutubeDLRequest(url)

                val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val subDir = if (lowerCaseFormat == "mp3") "ClipShift-Audio" else "ClipShift-Video"
                val targetDir = File(baseDir, subDir)
                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }

                val resolutionSuffix = if (lowerCaseFormat == "mp4" && !resolution.isNullOrBlank()) {
                    "_${resolution}"
                } else {
                    ""
                }
                request.addOption("-o", "${targetDir.absolutePath}/%(title)s${resolutionSuffix}.%(ext)s")

                val nativeDir = getApplication<Application>().applicationInfo.nativeLibraryDir
                request.addOption("--ffmpeg-location", "${nativeDir}/libffmpeg.so")
                request.addOption("--rm-cache-dir")

                if (lowerCaseFormat == "mp3") {
                    request.addOption("-f", "bestaudio/best")
                    request.addOption("-x")
                    request.addOption("--audio-format", "mp3")
                } else if (lowerCaseFormat == "mp4") {
                    if (resolution.isNullOrBlank()) {
                        // Standard-MP4-Download (Einfacher Modus) in 1080p
                        val formatSelector = "bestvideo[height<=1080]+bestaudio/best"
                        request.addOption("-f", formatSelector)
                        request.addOption("--remux-video", "mp4")
                    } else {
//                        // Expertenmodus: Auflösung wird übergeben
                        val height = resolution.replace("p", "")
                        request.addOption(
                            "-f",
                            "bv*[ext=mp4][height<=${height}]+ba[ext=m4a]/b"
                        )
                        request.addOption(
                            "-S",
                            "res:${height},codec:h264,fps,br"
                        )

                        request.addOption("--merge-output-format", "mp4")
                    }
                }

                YoutubeDL.getInstance().execute(request) { progress, _, line ->
                    _progress.value = progress / 100f
                    _statusMsg.value = "Lade... ${progress.toInt()}%"
                    getFileNameFromLog(line)?.let { finalFilePath = it }
                }

                renameAndSetSuccess(finalFilePath, lowerCaseFormat, resolution)

            } catch (e: Exception) {
                handleError(e, finalFilePath, lowerCaseFormat, resolution)
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

    private fun renameAndSetSuccess(filePath: String?, format: String, resolution: String?) {
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

    private fun handleError(e: Exception, filePath: String?, format: String, resolution: String?) {
        val msg = e.message ?: ""
        Log.e("DownloadViewModel", "Download-Fehler", e)

        if (msg.contains("ffprobe", true) || msg.contains("Postprocessing", true)) {
            renameAndSetSuccess(filePath, format, resolution)
            _progress.value = 1.0f
            return
        }

        _statusMsg.value = when {
            msg.contains("403") -> "❌ YouTube blockt (Update fehlgeschlagen?)"
            msg.contains("No video formats found") || msg.contains("format not available") -> "❌ Gewähltes Format nicht verfügbar."
            else -> "Fehler: ${msg.take(100)}"
        }
    }
}