package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.standtime.standtime.StandTimeLanguage

@Composable
fun GlassClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(horizontal = 28.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
        GlassTimeBlock(parts.hours)
        GlassTimeBlock(parts.minutes)
    }
}
