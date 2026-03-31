package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.standtime.R
import com.example.standtime.standtime.StandTimeLanguage
import com.example.standtime.standtime.localizedStringResource

@Composable
fun IosStackClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(32.dp)).background(Color(0xFF27272A)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${parts.hours}:${parts.minutes}", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Bold)
                Text("${parts.dayText}, ${parts.dateText}", color = Color(0xFFA1A1AA), fontSize = 18.sp)
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)) {
            GalleryWidgetCard(Color(0x332565FF), localizedStringResource(R.string.gallery_ios_weather_value, language), localizedStringResource(R.string.gallery_ios_weather_label, language))
            GalleryWidgetCard(Color(0x33EF4444), localizedStringResource(R.string.gallery_ios_calendar_value, language), localizedStringResource(R.string.gallery_ios_calendar_label, language))
            GalleryWidgetCard(Color(0x3322C55E), localizedStringResource(R.string.gallery_ios_finance_value, language), localizedStringResource(R.string.gallery_ios_finance_label, language))
        }
    }
}
