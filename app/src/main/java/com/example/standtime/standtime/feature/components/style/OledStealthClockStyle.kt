package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.standtime.R
import com.example.standtime.standtime.feature.utils.StandTimeLanguage
import com.example.standtime.standtime.feature.utils.localizedStringResource

@Composable
fun OledStealthClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box {
            Text("${parts.hours}${parts.minutes}", color = Color.White.copy(alpha = 0.9f), fontSize = 168.sp, fontWeight = FontWeight.Thin)
            Row(modifier = Modifier.align(Alignment.BottomCenter).padding(top = 240.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f).height(2.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.2f), Color.Transparent))))
                Text(localizedStringResource(R.string.gallery_stealth_mode, language), modifier = Modifier.padding(horizontal = 20.dp), color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp, letterSpacing = 6.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.weight(1f).height(2.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.2f), Color.Transparent))))
            }
        }
    }
}
