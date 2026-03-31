package com.example.standtime.standtime.feature.components.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.standtime.standtime.StandTimeLanguage

@Composable
fun TypographyFocusClockStyle(parts: GalleryClockParts, language: StandTimeLanguage, accentColor: Color, modifier: Modifier = Modifier) {
    val hourInt = parts.hours.toIntOrNull() ?: 0
    val hourWord = when (language) {
        StandTimeLanguage.UZBEK -> getUzbekHourWord(hourInt)
        StandTimeLanguage.RUSSIAN -> getRussianHourWord(hourInt)
        else -> getEnglishHourWord(hourInt)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.padding(64.dp)
        ) {
            Text(
                text = if (language == StandTimeLanguage.UZBEK) "HOZIR" else "IT IS",
                color = Color.Black,
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 100.sp
            )
            Text(
                text = hourWord.uppercase(),
                color = Color.Black,
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 100.sp
            )
            Text(
                text = when (language) {
                    StandTimeLanguage.UZBEK -> "DAN ${parts.minutes} DAQIQA"
                    StandTimeLanguage.RUSSIAN -> "${parts.minutes} МИНУТ"
                    else -> "PAST ${parts.minutes}"
                },
                color = Color(0xFFD4D4D8),
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 100.sp
            )
            Text(
                text = if (language == StandTimeLanguage.UZBEK) "O'TDI." else "NOW.",
                color = Color.Black,
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                lineHeight = 100.sp
            )

            Box(
                modifier = Modifier
                    .padding(top = 48.dp)
                    .width(128.dp)
                    .height(4.dp)
                    .background(Color.Black)
            )
        }
    }
}

private fun getUzbekHourWord(hour: Int): String {
    val words = listOf("O'n ikki", "Bir", "Ikki", "Uch", "To'rt", "Besh", "Olti", "Yetti", "Sakkiz", "To'qqiz", "O'n", "O'n bir")
    return words[hour % 12]
}

private fun getEnglishHourWord(hour: Int): String {
    val words = listOf("Twelve", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven")
    return words[hour % 12]
}

private fun getRussianHourWord(hour: Int): String {
    val words = listOf("Двенадцать", "Один", "Два", "Три", "Четыре", "Пять", "Шесть", "Семь", "Восемь", "Девять", "Десять", "Одиннадцать")
    return words[hour % 12]
}
