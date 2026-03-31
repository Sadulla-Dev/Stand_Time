package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.standtime.R
import com.example.standtime.standtime.StandTimeLanguage
import com.example.standtime.standtime.localizedStringResource

@Composable
fun WordsClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    Text(
        text = localizedStringResource(R.string.gallery_words_clock, language, parts.hours, parts.minutes),
        modifier = modifier.fillMaxSize().padding(horizontal = 36.dp),
        color = Color.White,
        fontSize = 42.sp,
        fontWeight = FontWeight.Black,
        lineHeight = 50.sp
    )
}
