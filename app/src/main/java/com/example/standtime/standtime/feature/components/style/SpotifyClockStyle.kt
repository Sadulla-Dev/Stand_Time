package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.standtime.R
import com.example.standtime.standtime.feature.utils.StandTimeLanguage
import com.example.standtime.standtime.feature.utils.localizedStringResource

@Composable
fun SpotifyClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(28.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(240.dp).clip(RoundedCornerShape(28.dp)).background(Brush.linearGradient(listOf(Color(0xFF22C55E), Color(0xFF14532D)))),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "${parts.hours}:${parts.minutes}", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 52.sp), color = Color.White)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = localizedStringResource(R.string.gallery_spotify_title, language), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 38.sp), color = Color.White)
            Text(text = localizedStringResource(R.string.gallery_spotify_subtitle, language), modifier = Modifier.padding(top = 8.dp), style = TextStyle(fontSize = 22.sp), color = Color(0xFFA1A1AA))
            Box(modifier = Modifier.padding(top = 28.dp).fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(Color(0xFF27272A))) {
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(parts.seconds.toFloat() / 60f).background(Color(0xFF22C55E)))
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
                Text(text = "00:${parts.seconds}", color = Color(0xFF71717A))
                Text(text = "01:00", color = Color(0xFF71717A))
            }
        }
    }
}
