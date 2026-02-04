package com.example.clipshift.ui

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clipshift.DownloadViewModel
import com.example.clipshift.R
import com.example.clipshift.ui.sections.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClipShiftApp(
    viewModel: DownloadViewModel = viewModel()
) {
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var urlText by remember { mutableStateOf("") }
    var showInfoDialog by remember { mutableStateOf(false) }

    var selectedResolution by remember { mutableStateOf("") }
    var selectedQuality by remember { mutableStateOf("MP3 192 kBit/s (Good)") }
    var selectedFormat by remember { mutableStateOf("MP4") }

    val statusMsg by viewModel.statusMsg.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    val progress by viewModel.progress.collectAsState()
    var isDarkMode by remember { mutableStateOf(false) }

    val backgroundColor = if (isDarkMode) Color.DarkGray else Color.White
    val contentColor = if (isDarkMode) Color.White else Color.Black
    val selectedTextColor = if (isDarkMode) Color.White else Color.Black
    val selectedIconColor = if (isDarkMode) Color(0xFF2196F3) else Color(0xFFFF0000)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.startDownload(urlText, selectedFormat, selectedResolution, selectedQuality)
        } else {
            Toast.makeText(context, context.getString(R.string.no_storage_permission), Toast.LENGTH_LONG).show()
        }
    }

    fun setAppLanguage(languageCode: String) {
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    Scaffold(
        containerColor = backgroundColor,
        contentColor = contentColor,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = backgroundColor),
                navigationIcon = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = contentColor,
                            modifier = Modifier.size(38.dp)
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
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
        },
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
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = selectedIconColor, unselectedIconColor = selectedIconColor, selectedTextColor = selectedTextColor, unselectedTextColor = selectedTextColor)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize().background(backgroundColor)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LogoSection()
                Spacer(modifier = Modifier.height(32.dp))

                UrlInputSection(text = urlText, onTextChange = { urlText = it })
                Spacer(modifier = Modifier.height(16.dp))

                ActionButtonsSection(
                    currentFormat = selectedFormat,
                    onFormatSelected = { selectedFormat = it },
                    onDownloadClick = {
                        if (urlText.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.please_enter_url), Toast.LENGTH_SHORT).show()
                        } else {
                            val permissionsToRequest = mutableListOf<String>()
                            if (Build.VERSION.SDK_INT >= 33) {
                                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_AUDIO)
                                permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                            permissionLauncher.launch(permissionsToRequest.toTypedArray())
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isDownloading) {
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val currentStatusText = statusMsg.asString()
                Text(
                    text = currentStatusText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
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
