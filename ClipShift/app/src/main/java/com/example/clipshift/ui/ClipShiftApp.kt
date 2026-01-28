package com.example.clipshift.ui

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clipshift.DownloadViewModel
import com.example.clipshift.ui.sections.ActionButtonsSection
import com.example.clipshift.ui.sections.ExpertOptions
import com.example.clipshift.ui.sections.LogoSection
import com.example.clipshift.ui.sections.UrlInputSection
import com.example.clipshift.ui.sections.DarkMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipShiftApp(
    viewModel: DownloadViewModel = viewModel()
) {
    val context = LocalContext.current

    /**
     * State of the bottom bar menu
     */
    var selectedTab by remember { mutableIntStateOf(0) }
    var urlText by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }

    /**
     * State of the expert mode
     */
    var selectedResolution by remember { mutableStateOf("") }
    var selectedQuality by remember { mutableStateOf("MP3 192 kBit/s") }
    var selectedFormat by remember { mutableStateOf("MP4") }

    /**
     * State of the view model
     */
    val statusMsg by viewModel.statusMsg.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    val progress by viewModel.progress.collectAsState()

    /**
     * State of the UI
     */
    var isDarkMode by remember { mutableStateOf(false) }
    val backgroundColor = if (isDarkMode) Color.DarkGray else Color.White
    val contentColor = if (isDarkMode) Color.White else Color.Black
    val selectedTextColor = if (isDarkMode) Color.White else Color.Black
    val selectedIconColor = if (isDarkMode) Color(0xFF2196F3) else Color(0xFFFF0000)

    /**
     * Permission handling
     */
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all permissions are granted
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // if all are granted start download
            viewModel.startDownload(urlText, selectedFormat, selectedResolution, selectedQuality)
        } else {
            // if not granted, show message
            Toast.makeText(context, "Ohne Speicherzugriff kein Download möglich!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Top bar for info button and dark mode
     */
    Scaffold(
        containerColor = backgroundColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = contentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    DarkMode(
                        isDarkMode = isDarkMode,
                        onDarkModeChange = { isDarkMode = it },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },

        /**
         * Bottom bar with easy mode and expert mode
         */
        bottomBar = {
            NavigationBar(containerColor = backgroundColor, contentColor = contentColor) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Einfacher Modus") },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedIconColor,
                        unselectedIconColor = selectedIconColor,
                        selectedTextColor = selectedTextColor,
                        unselectedTextColor = selectedTextColor
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Experten Modus") },
                    modifier = Modifier.testTag("ExpertModus"),
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedIconColor,
                        unselectedIconColor = selectedIconColor,
                        selectedTextColor = selectedTextColor,
                        unselectedTextColor = selectedTextColor
                    )
                )
            }
        }

        /**
         * Middle Section with Url input and the action buttons
         */
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .testTag("MainContent")
                        .semantics{
                            contentDescription = if (isDarkMode) "dark" else "light"
                        }
                        .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .background(backgroundColor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoSection()

                Spacer(modifier = Modifier.height(32.dp))

                UrlInputSection(
                    text = urlText,
                    onTextChange = { urlText = it },
                modifier = Modifier.testTag("UrlInput")
            )

                Spacer(modifier = Modifier.height(16.dp))

                ActionButtonsSection(
                    currentFormat = selectedFormat,
                    onFormatSelected = { selectedFormat = it },
                    modifier = Modifier.testTag("DownloadButton"),onDownloadClick = {
                        if (urlText.isBlank()) {
                            Toast.makeText(context, "Bitte erst eine URL eingeben!", Toast.LENGTH_SHORT).show()
                        } else {

                            /**
                             * Permission check
                             */
                            val permissionsToRequest = mutableListOf<String>()

                            /**
                             * Different permission based on Android version
                             */
                            if (Build.VERSION.SDK_INT >= 33) {
                                // Android 13, 14, 15
                                permissionsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO)
                                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
                                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                // Android 10, 11, 12
                                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                            /**
                             * Pop to ask for permissions
                             */
                            permissionLauncher.launch(permissionsToRequest.toTypedArray())
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                /**
                 * Progressbar to show progress of conversion
                 */
                if (isDownloading) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = statusMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("TextOutput"),color = if (statusMsg.contains("Fehler") || statusMsg.contains("❌"))
                        MaterialTheme.colorScheme.error
                    else
                        contentColor
                )

                if (selectedTab == 1) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    ExpertOptions(
                        currentRes = selectedResolution,
                        onResSelected = { selectedResolution = it },
                        currentQuality = selectedQuality,
                        onQualitySelected = { selectedQuality = it },
                        currentFormat = selectedFormat,
                        contentColor = contentColor
                    )
                }
            }
        }

        /**
         * Text for the Info button to give user a explanation of the App
         */
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text("Über die App", color = contentColor) },
                text = {
                    Text(
                        text = """
                        Willkommen bei ClipShift!
                        
                        Dein All-in-One Downloader für die meisten Social Media Apps:
                        ✅ YouTube
                        ✅ Instagram
                        ✅ Twitter (X)
                        ... und viele mehr!
                        
                        🚀 Einfacher Modus:
                        Kopiere einfach den Link (egal ob Video, Reel oder Story), füge ihn ein und drücke Download. Die App liefert dir automatisch die beste Qualität.
                        
                        🛠️ Experten Modus:
                        Du willst die volle Kontrolle? Wähle hier manuell zwischen MP4 (Video) und MP3 (Audio). Bestimme selbst die Auflösung (z.B. 1080p) oder die Audio-Bitrate für deine Musik.
                        
                        📂 Speicherort:
                        Deine Downloads landen direkt im Ordner "Downloads/ClipShift" auf deinem Handy.
                    """.trimIndent(),
                        color = contentColor
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("Schließen", color = selectedIconColor)
                    }
                },
                containerColor = backgroundColor
            )
        }
    }
}
