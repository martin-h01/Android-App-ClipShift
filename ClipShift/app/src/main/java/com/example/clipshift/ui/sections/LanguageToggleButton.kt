package com.example.clipshift.ui.sections

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun LanguageToggleButton(
    onLanguageChange: (String) -> Unit,
    contentColor: Color
) {
    // Ermitteln der aktuellen Sprache
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
            modifier = Modifier
                .clickable { if (!isGerman) onLanguageChange("de") }
                .padding(horizontal = 4.dp)
        )
        Text("|", color = contentColor)
        Text(
            text = "EN",
            fontWeight = if (!isGerman) FontWeight.Bold else FontWeight.Normal,
            color = if (!isGerman) contentColor else contentColor.copy(alpha = 0.5f),
            modifier = Modifier
                .clickable { if (isGerman) onLanguageChange("en") }
                .padding(horizontal = 4.dp)
        )
        Text(" ]", color = contentColor, fontWeight = FontWeight.Bold)
    }
}