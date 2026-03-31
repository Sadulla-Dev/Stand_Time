package com.example.standtime.standtime

import androidx.compose.ui.graphics.Color
import com.example.standtime.ui.theme.CoralAccent
import com.example.standtime.ui.theme.LimeAccent
import com.example.standtime.ui.theme.SkyAccent

internal fun StandTimeUiState.remainingPomodoroText(): String {
    val minutes = pomodoroRemainingSeconds / 60
    val seconds = pomodoroRemainingSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

internal fun StandTimeUiState.accentColor(): Color = when (accentPalette) {
    AccentPalette.LIME -> LimeAccent
    AccentPalette.SKY -> SkyAccent
    AccentPalette.CORAL -> CoralAccent
}
