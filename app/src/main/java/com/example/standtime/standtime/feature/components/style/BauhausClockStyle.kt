package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.standtime.standtime.StandTimeLanguage

@Composable
fun BauhausClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        BauhausColumn(parts.hours[0], parts.hours[1], Color(0xFF254184), Color(0xFFE84A27), true)
        BauhausColumn(parts.minutes[0], parts.minutes[1], Color(0xFFF9C32E), Color(0xFF231F20), false)
    }
}
