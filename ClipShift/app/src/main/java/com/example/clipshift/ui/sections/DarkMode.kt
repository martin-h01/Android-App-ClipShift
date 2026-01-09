package com.example.clipshift.ui.sections

import com.example.clipshift.R
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun DarkMode(isDarkMode: Boolean,
             onDarkModeChange: (Boolean) -> Unit,
             modifier: Modifier = Modifier) {

    val onIcon = painterResource(R.drawable.icon_mond)
    val offIcon = painterResource(R.drawable.icon_sonne)

    Switch(
        modifier = modifier,
        checked = isDarkMode,
        onCheckedChange = onDarkModeChange,
        thumbContent = {
            Icon(
                painter = if (isDarkMode) onIcon else offIcon,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize)
            )
        }
    )
}
