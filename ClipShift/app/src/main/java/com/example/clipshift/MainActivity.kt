package com.example.clipshift

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- FARBEN (aus dem Design) ---
val ClipShiftBlue = Color(0xFF0056D2)
val ClipShiftGreen = Color(0xFF4CAF50)
val ButtonRed = Color(0xFFD32F2F)
val ButtonBlue = Color(0xFF1976D2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ClipShiftApp()
            }
        }
    }
}

@Composable
fun ClipShiftApp() {
    // STATE: Hier speichern wir, was der Nutzer gerade macht
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Einfach, 1 = Experte
    var urlText by remember { mutableStateOf("") }

    // Experten-Einstellungen (State muss hier oben liegen, damit wir beim Download darauf zugreifen können)
    var selectedResolution by remember { mutableStateOf("720p") }
    var selectedQuality by remember { mutableStateOf("MP3 192 kBit/s") }
    var selectedFormat by remember { mutableStateOf("MP4") } // Für das Dropdown

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

            // URL Eingabe ist immer sichtbar
            UrlInputSection(
                text = urlText,
                onTextChange = { urlText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons sind immer sichtbar
            ActionButtonsSection(
                currentFormat = selectedFormat,
                onFormatSelected = { selectedFormat = it },
                onDownloadClick = {
                    // Logik für den OK Button
                    if (urlText.isBlank()) {
                        Toast.makeText(context, "Bitte erst eine URL eingeben!", Toast.LENGTH_SHORT).show()
                    } else {
                        val message = if (selectedTab == 0) {
                            "Starte einfachen Download: $selectedFormat"
                        } else {
                            "Starte Experten-Download:\n$selectedResolution | $selectedQuality"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }
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

// --- UI KOMPONENTEN ---
@Composable
fun LogoSection() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
        // Hier wird jetzt das echte Bild geladen
        Image(
            painter = painterResource(id = R.drawable.clipshift_logo), // Der Name muss exakt deiner Datei entsprechen (ohne .png)
            contentDescription = "ClipShift Logo",
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape) // Schneidet das Bild rund aus, falls es Ecken hat
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "ClipShift",
        style = TextStyle(
            fontSize = 40.sp, fontWeight = FontWeight.Bold,
            brush = Brush.horizontalGradient(colors = listOf(ClipShiftBlue, ClipShiftGreen))
        )
    )
}


@Composable
fun UrlInputSection(text: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text("Enter your URL", color = Color.Gray) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 18.sp),
        singleLine = true
    )
}

@Composable
fun ActionButtonsSection(
    currentFormat: String,
    onFormatSelected: (String) -> Unit,
    onDownloadClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Dropdown Button
        Box(modifier = Modifier.weight(1f)) {
            Button(
                onClick = { menuExpanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(currentFormat, fontSize = 16.sp) // Zeigt aktuelles Format an
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            // Das aufklappbare Menü
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                listOf("MP4", "MP3", "AVI", "MKV").forEach { format ->
                    DropdownMenuItem(
                        text = { Text(format) },
                        onClick = {
                            onFormatSelected(format)
                            menuExpanded = false
                        }
                    )
                }
            }
        }

        // OK Button
        Button(
            onClick = onDownloadClick,
            colors = ButtonDefaults.buttonColors(containerColor = ButtonRed),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        ) {
            Text("OK", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ExpertOptions(
    currentRes: String,
    onResSelected: (String) -> Unit,
    currentQuality: String,
    onQualitySelected: (String) -> Unit
) {
    Text("Auflösung:", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
    val resolutions = listOf("360p", "480p", "720p", "1080p")
    resolutions.forEach { res ->
        SelectableOption(label = res, selected = (res == currentRes)) { onResSelected(res) }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text("Qualität:", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
    val qualities = listOf(
        "MP3 128 kBit/s (Standard)",
        "MP3 192 kBit/s (Gut)",
        "MP3 320 kBit/s (Beste)",
        "FLAC (Verlustfrei)"
    )
    qualities.forEach { qual ->
        SelectableOption(label = qual, selected = (qual == currentQuality)) { onQualitySelected(qual) }
    }
}

@Composable
fun SelectableOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Klickbar machen!
            .padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick // Auch der Radiobutton selbst reagiert
        )
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}