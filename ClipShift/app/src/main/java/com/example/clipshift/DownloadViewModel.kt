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

    /**
     * Handling of download with the different parameters
     */
    fun startDownload(url: String, format: String, resolution: String?, audioQuality: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isDownloading.value = true
            _progress.value = 0f
            var finalFilePath: String? = null
            val lowerCaseFormat = format.lowercase().trim()

            try {
                /**
                 * Initialising of ytdlp

                 */
                if (!isEngineReady) {
                    _statusMsg.value = "Initialisiere..."
                    try {
                        YoutubeDL.getInstance().init(getApplication())
                        // Update versuchen, Fehler ignorieren (damit Download nicht blockiert wird)
                        YoutubeDL.getInstance().updateYoutubeDL(getApplication(), YoutubeDL.UpdateChannel.STABLE)
                        isEngineReady = true
                    } catch (e: Exception) {
                        Log.e("DownloadVM", "Init/Update Fehler", e)
                    }
                }

                _statusMsg.value = "Starte Download..."
                val request = YoutubeDLRequest(url)

                /**
                 * Setting of the download folder
                 */
                val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val subDir = if (lowerCaseFormat == "mp3") "ClipShift-Audio" else "ClipShift-Video"
                val targetDir = File(baseDir, subDir)
                if (!targetDir.exists()) targetDir.mkdirs()

                /**
                 * Setting of the file name
                 */
                var fileNameSuffix = ""
                if (lowerCaseFormat == "mp4" && !resolution.isNullOrBlank()) {
                    fileNameSuffix = "_$resolution"
                }
                else if (lowerCaseFormat == "mp3" && !audioQuality.isNullOrBlank()) {
                    fileNameSuffix = when {
                        audioQuality.contains("320") -> "_320k"
                        audioQuality.contains("256") -> "_256k"
                        audioQuality.contains("192") -> "_192k"
                        audioQuality.contains("128") -> "_128k"
                        else -> ""
                    }
                }
                request.addOption("-o", "${targetDir.absolutePath}/%(title)s${fileNameSuffix}.%(ext)s")

                /**
                 * Path for ffmpeg, which is required for mp4 conversion
                 */
                val nativeDir = getApplication<Application>().applicationInfo.nativeLibraryDir
                request.addOption("--ffmpeg-location", "${nativeDir}/libffmpeg.so")

                /**
                 * options for ytdlp to setup the process
                 */
                request.addOption("--force-ipv4")         // forces ipv4
                request.addOption("--no-check-certificate") // ignores SSl for mobile devices
                request.addOption("--force-overwrites")    // overwrites files with the same name
                request.addOption("--no-playlist")         // no downloading of playlists
                request.addOption("--rm-cache-dir")       // clear cache
                request.addOption("--extractor-args", "youtube:player_client=android")

                /**
                 * Logic for setting selected Mp4 and Mp3 settings
                 */
                if (lowerCaseFormat == "mp3") {
                    request.addOption("-x")
                    request.addOption("--audio-format", "mp3")
                    request.addOption("-f", "bestaudio/best")

                    val bitrate = when (audioQuality) {
                        "MP3 320 kBit/s (Beste)" -> "320K"
                        "MP3 256 kBit/s (Hoch)" -> "256K"
                        "MP3 192 kBit/s (Gut)" -> "192K"
                        "MP3 128 kBit/s (Standard)" -> "128K"
                        else -> "192K"
                    }
                    request.addOption("--audio-quality", bitrate)

                } else if (lowerCaseFormat == "mp4") {
                    request.addOption("--merge-output-format", "mp4")

                    if (resolution.isNullOrBlank()) {
                        request.addOption("-f", "bestvideo[height<=1080][ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best")
                    } else {
                        val height = resolution.replace("p", "")
                        request.addOption("-f", "bv*[height<=${height}][ext=mp4]+ba[ext=m4a]/b[height<=${height}]/best")
                    }
                }

                /**
                 * Gives ytdlp the commands and starts the download
                 */
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

        if (format == "mp3" && file.exists()) {
            val correctName = file.absolutePath.substringBeforeLast(".") + ".mp3"
            val newFile = File(correctName)
            if (file.absolutePath != newFile.absolutePath) {
                file.renameTo(newFile)
            }
        }
        _statusMsg.value = "✅ Fertig!"
    }

    /**
     * Error handling
     */
    private fun handleError(e: Exception, filePath: String?) {
        val msg = e.message ?: ""
        Log.e("DownloadViewModel", "Fehler", e)

        if (msg.contains("Postprocessing") && filePath != null && File(filePath).exists()) {
            renameAndSetSuccess(filePath, "mp3")
            _progress.value = 1.0f
            return
        }

        val cleanMsg = msg.replace("WARNING:", "").replace("ERROR:", "")
        _statusMsg.value = "Fehler: ${cleanMsg.take(60)}"
    }
}