package com.example.clipshift.ui.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExpertOptions(
    currentRes: String,
    onResSelected: (String) -> Unit,
    currentQuality: String,
    onQualitySelected: (String) -> Unit,
    currentFormat: String,
    contentColor: Color
) {
    when (currentFormat) {
        "MP4" -> {
            Text("Auflösung:", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            val resolutions = listOf("144p", "240p", "360p", "480p", "720p", "1080p", "1440p", "2160p")
            Box(modifier = Modifier.height(200.dp)) { // Box mit fester Höhe
                LazyColumn { // Scrollbare Liste
                    items(resolutions) { res ->
                        SelectableOption(
                            label = res,
                            selected = (res == currentRes),
                            onClick = { onResSelected(res) },
                            color = contentColor
                        )
                    }
                }
            }
        }
        "MP3" -> {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Qualität:", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            val qualities = listOf(
                "MP3 128 kBit/s (Standard)",
                "MP3 192 kBit/s (Gut)",
                "MP3 320 kBit/s (Beste)",
                "FLAC (Verlustfrei)"
            )
            qualities.forEach { qual ->
                SelectableOption(
                    label = qual,
                    selected = (qual == currentQuality),
                    onClick = { onQualitySelected(qual) },
                    color = contentColor
                )
            }
        }
    }
}
