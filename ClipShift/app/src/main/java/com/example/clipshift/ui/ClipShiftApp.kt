package com.example.clipshift.ui

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.clipshift.ui.sections.ActionButtonsSection
import com.example.clipshift.ui.sections.ExpertOptions
import com.example.clipshift.ui.sections.LogoSection
import com.example.clipshift.ui.sections.UrlInputSection
import com.example.clipshift.ui.sections.DarkMode


@Composable
fun ClipShiftApp() {
    // STATE: Hier speichern wir, was der Nutzer gerade macht
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Einfach, 1 = Experte
    var urlText by remember { mutableStateOf("") }

    // Experten-Einstellungen (State muss hier oben liegen, damit wir beim Download darauf zugreifen können)
    var selectedResolution by remember { mutableStateOf("720p") }
    var selectedQuality by remember { mutableStateOf("MP3 192 kBit/s") }
    var selectedFormat by remember { mutableStateOf("MP4") } // Für das Dropdown
    var isDarkMode by remember { mutableStateOf(false) }
    val backgroundColor = if (isDarkMode) Color.DarkGray else Color.White
    val contentColor = if (isDarkMode) Color.White else Color.Black
    val selectedTextColor = if (isDarkMode) Color.White else Color.Black
    val selectedIconColor = if (isDarkMode) Color(0xFF2196F3) else Color(0xFFFF0000)




    val context = LocalContext.current

    Scaffold(containerColor = backgroundColor , contentColor = contentColor,
        bottomBar = {
            NavigationBar(containerColor = backgroundColor , contentColor = contentColor) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Einfacher Modus") },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null)},
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
    ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
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
                            onTextChange = { urlText = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ActionButtonsSection(
                            currentFormat = selectedFormat,
                            onFormatSelected = { selectedFormat = it },
                            onDownloadClick = {
                                // ... (deine Klick-Logik bleibt gleich)
                            }
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
                                contentColor = contentColor
                            )
                        }
                    }

                    DarkMode(
                        isDarkMode = isDarkMode,
                        onDarkModeChange = { isDarkMode = it },
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Jetzt funktioniert die Ausrichtung
                            .padding(16.dp)             // Abstand zum Rand der Box
                    )


                }

            }
        }