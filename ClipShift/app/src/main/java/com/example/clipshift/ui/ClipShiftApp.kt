package com.example.clipshift.ui

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clipshift.DownloadViewModel
import com.example.clipshift.R
import com.example.clipshift.ui.sections.*
import java.util.Locale

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
    var selectedQuality by remember { mutableStateOf("MP3 192 kBit/s (Good)") }
    var selectedFormat by remember { mutableStateOf("MP4") }

    /**
     * State of the view model
     */
    val statusMsg by viewModel.statusMsg.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    val progress by viewModel.progress.collectAsState()
    var isDarkMode by remember { mutableStateOf(false) }

    /**
     * State of the UI
     */
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
        if (permissions.values.all { it }) {
            //Check if all permissions are granted
            viewModel.startDownload(urlText, selectedFormat, selectedResolution, selectedQuality)
        } else {
            //if not granted, show message
            Toast.makeText(context, context.getString(R.string.no_storage_permission), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Sets the app language based on phone language
     */
    fun setAppLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    /**
     * Top bar for language, info and dark mode buttons
     */
    Scaffold(
        containerColor = backgroundColor,
        contentColor = contentColor,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { showInfoDialog = true },
                        modifier = Modifier.testTag("InfoButton")) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = contentColor,
                            modifier = Modifier
                                .size(38.dp)
                        )
                    }
                },
                title = {
                    LanguageToggleButton(
                        onLanguageChange = { setAppLanguage(it) },
                        contentColor = contentColor
                    )
                },
                actions = {
                    DarkMode(
                        isDarkMode = isDarkMode,
                        onDarkModeChange = { isDarkMode = it },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("DarkModeButton")
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
                    label = { Text(stringResource(R.string.simple_mode)) },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = selectedIconColor, unselectedIconColor = selectedIconColor, selectedTextColor = selectedTextColor, unselectedTextColor = selectedTextColor)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text(stringResource(R.string.expert_mode)) },
                    modifier = Modifier.testTag("ExpertModus"),
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = selectedIconColor, unselectedIconColor = selectedIconColor, selectedTextColor = selectedTextColor, unselectedTextColor = selectedTextColor)
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
                .background(backgroundColor)
                .testTag("MainContent")
                .semantics{
                    contentDescription = if (isDarkMode) "dark" else "light"
                }

        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LogoSection()
                Spacer(modifier = Modifier.height(32.dp))

                UrlInputSection(text = urlText, onTextChange = { urlText = it },modifier = Modifier.testTag("UrlInput"))
                Spacer(modifier = Modifier.height(16.dp))

                ActionButtonsSection(
                    currentFormat = selectedFormat,
                    onFormatSelected = { selectedFormat = it },
                    modifier = Modifier.testTag("DownloadButton"),
                    onDownloadClick = {
                        if (urlText.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.please_enter_url), Toast.LENGTH_SHORT).show()
                        } else {

                            val permissionsToRequest = mutableListOf<String>()

                            /**
                             * Different permission based on Android version
                             */
                            if (Build.VERSION.SDK_INT >= 33) {
                                //Android 13, 14, 15
                                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_AUDIO)
                                permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                //Android 10, 11 ,12
                                permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }

                            /**
                             * Pop up to request permissions
                             */
                            permissionLauncher.launch(permissionsToRequest.toTypedArray())
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                /**
                 * Progress bar and status message
                 */
                if (isDownloading) {
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val currentStatusText = statusMsg.asString()
                Text(
                    text = currentStatusText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("TextOutput"),
                    color = if (currentStatusText.contains("Error") || currentStatusText.contains("❌"))
                        MaterialTheme.colorScheme.error
                    else
                        contentColor
                )

                if (selectedTab == 1) {
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider()
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
         * Check if Info dialog needs to be displayed
         */
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = { Text(stringResource(R.string.about_app_title), color = contentColor) },
                text = { Text(stringResource(R.string.about_app_text), color = contentColor) },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text(stringResource(R.string.close_button), color = selectedIconColor)
                    }
                },
                containerColor = backgroundColor
            )
        }
    }
}

/**
 * Toggle between German and English
 */
@Composable
fun LanguageToggleButton(
    onLanguageChange: (String) -> Unit,
    contentColor: Color
) {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val firstLocale = if (!appLocales.isEmpty) appLocales.get(0) else Locale.getDefault()
    val languageCode = firstLocale?.language ?: "de"
    val isGerman = languageCode == "de"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("[ ", color = contentColor, fontWeight = FontWeight.Bold)
        Text(
            text = "DE",
            fontWeight = if (isGerman) FontWeight.Bold else FontWeight.Normal,
            color = if (isGerman) contentColor else contentColor.copy(alpha = 0.5f),
            modifier = Modifier.clickable { if (!isGerman) onLanguageChange("de") }.padding(horizontal = 4.dp)
        )
        Text("|", color = contentColor)
        Text(
            text = "EN",
            fontWeight = if (!isGerman) FontWeight.Bold else FontWeight.Normal,
            color = if (!isGerman) contentColor else contentColor.copy(alpha = 0.5f),
            modifier = Modifier.clickable { if (isGerman) onLanguageChange("en") }.padding(horizontal = 4.dp)
        )
        Text(" ]", color = contentColor, fontWeight = FontWeight.Bold)
    }
}