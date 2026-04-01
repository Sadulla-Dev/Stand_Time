package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.standtime.standtime.feature.utils.StandTimeLanguage

@Composable
fun ArcadeClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = "${parts.hours}:${parts.minutes}", color = Color(0xFFFACC15), fontSize = 112.sp, fontWeight = FontWeight.Black)
        Row(modifier = Modifier.align(Alignment.Center).padding(top = 130.dp).fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly) {
            Text("\uD83C\uDF52", fontSize = 56.sp)
            Text("\uD83D\uDC7B", fontSize = 72.sp)
            Text("\uD83D\uDFE1", fontSize = 62.sp)
        }
    }
}
