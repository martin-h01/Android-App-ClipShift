package com.example.clipshift.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// NEU: Importe für ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clipshift.DownloadViewModel
import com.example.clipshift.ui.sections.ActionButtonsSection
import com.example.clipshift.ui.sections.ExpertOptions
import com.example.clipshift.ui.sections.LogoSection
import com.example.clipshift.ui.sections.UrlInputSection

@Composable
fun ClipShiftApp(
    // NEU 1: Wir holen uns hier das ViewModel (das Gehirn der App)
    viewModel: DownloadViewModel = viewModel()
) {
    // STATE: Lokaler UI State
    var selectedTab by remember { mutableIntStateOf(0) }
    var urlText by remember { mutableStateOf("") }

    // Experten-Einstellungen
    var selectedResolution by remember { mutableStateOf("720p") }
    var selectedQuality by remember { mutableStateOf("MP3 192 kBit/s") }
    var selectedFormat by remember { mutableStateOf("MP4") }

    // NEU 2: Wir beobachten die Daten aus dem ViewModel
    val statusMsg by viewModel.statusMsg.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    val progress by viewModel.progress.collectAsState()

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Einfacher Modus") },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Experten Modus") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoSection()

            Spacer(modifier = Modifier.height(32.dp))

            // URL Eingabe
            UrlInputSection(
                text = urlText,
                onTextChange = { urlText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionButtonsSection(
                currentFormat = selectedFormat,
                onFormatSelected = { selectedFormat = it },
                onDownloadClick = {
                    if (urlText.isBlank()) {
                        Toast.makeText(context, "Bitte erst eine URL eingeben!", Toast.LENGTH_SHORT).show()
                    } else {
                        // FIX: Das korrekt ausgewählte Format wird übergeben.
                        viewModel.startDownload(urlText, selectedFormat)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // NEU 4: Status-Anzeige (Ladebalken & Text)
            if (isDownloading) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Zeigt: "Bereit", "Update Engine...", "Lade 50%..." oder Fehler
            Text(
                text = statusMsg,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = if (statusMsg.contains("Fehler") || statusMsg.contains("❌"))
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )

            // Experten-Optionen nur anzeigen, wenn Tab 1 aktiv ist
            if (selectedTab == 1) {
                Spacer(modifier = Modifier.height(32.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                ExpertOptions(
                    currentRes = selectedResolution,
                    onResSelected = { selectedResolution = it },
                    currentQuality = selectedQuality,
                    onQualitySelected = { selectedQuality = it }
                )
            }
        }
    }
}