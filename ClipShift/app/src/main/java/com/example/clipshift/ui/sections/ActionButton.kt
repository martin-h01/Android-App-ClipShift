package com.example.clipshift.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
val ButtonRed = Color(0xFFD32F2F)
val ButtonBlue = Color(0xFF1976D2)

/**
 * Section with Dropdown menu and button to start converting
 */
@Composable
fun ActionButtonsSection(
    currentFormat: String,
    onFormatSelected: (String) -> Unit,
    onDownloadClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        /**
         * Button for the dropdown menu for selecting format.
         */
        Box(modifier = Modifier.weight(1f)) {
            Button(
                onClick = { menuExpanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                /**
                 * Shows currently selected Format
                 */
                Text(currentFormat, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            /**
             * The dropdown enu itself
             */
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                /**
                 * List of available formats
                 */
                listOf("MP4", "MP3" ).forEach { format ->
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

        /**
         * "Ok" Button to start the converting process
         */
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
