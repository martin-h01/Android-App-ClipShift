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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clipshift.R

/**
 * Section for the Expert Mode with various options
 */
@Composable
fun ExpertOptions(
    currentRes: String,
    onResSelected: (String) -> Unit,
    currentQuality: String,
    onQualitySelected: (String) -> Unit,
    currentFormat: String,
    contentColor: Color,
) {
    when (currentFormat) {
        /**
         * List of MP4 resolutions to select from
         */
        "MP4" -> {
            Text(stringResource(R.string.resolution), fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())

            val resolutions = listOf("1080p", "720p", "480p", "360p", "240p", "144p")

            Box(modifier = Modifier.height(250.dp)) {
                LazyColumn(modifier = Modifier.testTag("SelectableVideo")) {
                    items(resolutions) { res ->
                        val isSelected = (res == currentRes)
                        SelectableOption(
                            label = res,
                            selected = isSelected,
                            modifier = Modifier,
                            onClick = {
                                /**
                                 * Logic for selecting and deselecting of options
                                 */
                                if (isSelected) {
                                    onResSelected("") // if already selected -> delete selection
                                } else {
                                    onResSelected(res) // otherwise -> select
                                }
                            },
                            color = contentColor
                        )
                    }
                }
            }
        }

        /**
         * List of MP3 qualities to select from
         */
        "MP3" -> {
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.quality), fontWeight = FontWeight.Bold, modifier = Modifier
                .fillMaxWidth()
                .testTag("ExpertModusAudioOptionen")
            )

            val qualities = listOf(
                "MP3 320 kBit/s (Beste)",
                "MP3 256 kBit/s (Hoch)",
                "MP3 192 kBit/s (Gut)",
                "MP3 128 kBit/s (Standard)"
            )

            qualities.forEach { qual ->
                val isSelected = (qual == currentQuality)
                SelectableOption(
                    label = qual,
                    selected = isSelected,
                    onClick = {
                        /**
                         * Logic for selecting and deselecting of options
                         */
                        if (isSelected) {
                            onQualitySelected("") // if already selected -> delete selection
                        } else {
                            onQualitySelected(qual) // otherwise -> select
                        }
                    },
                    modifier = Modifier.testTag("SelectableAudio"),
                    color = contentColor
                )
            }
        }
    }
}