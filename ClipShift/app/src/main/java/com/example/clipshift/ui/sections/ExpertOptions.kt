package com.example.clipshift.ui.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
