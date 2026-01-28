package com.example.clipshift.ui.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clipshift.R

val ClipShiftBlue = Color(0xFF0056D2)
val ClipShiftGreen = Color(0xFF4CAF50)

/**
 * Section with the ClipShift Logo
 */
@Composable
fun LogoSection() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
        /**
         * Laoding image file of the ClipShift Logo
         */
        Image(
            painter = painterResource(id = R.drawable.clipshift_logo),
            contentDescription = "ClipShift Logo",
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "ClipShift",
        style = TextStyle(
            fontSize = 40.sp, fontWeight = FontWeight.Bold,
            brush = Brush.horizontalGradient(colors = listOf(ClipShiftBlue, ClipShiftGreen))
        )
    )
}

