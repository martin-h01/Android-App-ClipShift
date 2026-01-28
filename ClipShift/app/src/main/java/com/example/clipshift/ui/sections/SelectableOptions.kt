package com.example.clipshift.ui.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Bottom bar options for either easy mode or expert mode
 */
@Composable
fun SelectableOption(label: String, selected: Boolean, onClick: () -> Unit, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // Klickbar machen!
            .padding(vertical = 4.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick, // Auch der Radiobutton selbst reagiert
            colors = RadioButtonDefaults.colors(unselectedColor = color, selectedColor = color)
        )
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}