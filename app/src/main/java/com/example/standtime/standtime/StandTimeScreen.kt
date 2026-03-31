package com.example.standtime.standtime

import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.standtime.R
import com.example.standtime.standtime.feature.components.style.GalleryClockContent as StyleGalleryClockContent
import com.example.standtime.standtime.feature.components.style.galleryParts
import com.example.standtime.standtime.feature.components.style.galleryStyleAt
import com.example.standtime.standtime.feature.components.style.galleryStyleCount
import com.example.standtime.ui.theme.CoralAccent
import com.example.standtime.ui.theme.LimeAccent
import com.example.standtime.ui.theme.SkyAccent

@Composable
fun StandTimeRoute(
    state: StandTimeUiState,
    onIntent: (StandTimeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val language = state.language
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val accentColor = state.accentColor()
    val background = Brush.radialGradient(
        colors = listOf(
            accentColor.copy(alpha = if (state.themeMode == ThemeMode.DARK) 0.32f else 0.20f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )
    val rootPagerState = rememberPagerState(pageCount = { 3 })

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
        ) {
            HorizontalPager(
                state = rootPagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = if (page == 0) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    }
                ) {
                    when (page) {
                        0 -> ClockStylesPage(
                            state = state,
                            language = language,
                            accentColor = accentColor,
                            onIntent = onIntent
                        )
                        1 -> DashboardPage(
                            state = state,
                            language = language,
                            accentColor = accentColor,
                            onIntent = onIntent,
                            isLandscape = isLandscape
                        )
                        else -> SetupPage(
                            state = state,
                            language = language,
                            accentColor = accentColor,
                            onIntent = onIntent
                        )
                    }
                }
            }

            if (rootPagerState.currentPage != 0) {
                PagerHeader(
                    currentPage = rootPagerState.currentPage,
                    language = language,
                    accentColor = accentColor,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ClockStylesPage(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit
) {
    val stylesCount = galleryStyleCount
    val parts = state.galleryParts()
    val galleryPagerState = rememberPagerState(pageCount = { stylesCount })
    val currentStyleIndex = galleryPagerState.currentPage
    val currentStyle = galleryStyleAt(currentStyleIndex)
    val styleName = localizedStringResource(currentStyle.nameRes, language)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(0.dp))
            .background(currentStyle.background)
    ) {
        VerticalPager(
            state = galleryPagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageStyle = galleryStyleAt(page)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(pageStyle.background)
            ) {
                StyleGalleryClockContent(
                    index = page,
                    parts = parts,
                    language = language,
                    accentColor = accentColor,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 24.dp, vertical = 18.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = localizedStringResource(
                    R.string.gallery_charging_status,
                    language,
                    state.batteryLevel
                ),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                ),
                color = currentStyle.overlayColor
            )
            Text(
                text = localizedStringResource(
                    R.string.gallery_style_counter,
                    language,
                    styleName,
                    currentStyleIndex + 1,
                    stylesCount
                ),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                ),
                color = currentStyle.overlayColor
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(stylesCount) { index ->
                Box(
                    modifier = Modifier
                        .width(if (index == currentStyleIndex) 24.dp else 8.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(currentStyle.overlayColor.copy(alpha = if (index == currentStyleIndex) 1f else 0.3f))
                )
            }
        }
    }
}

@Composable
private fun DashboardPage(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit,
    isLandscape: Boolean
) {
    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ClockPanel(
                state = state,
                language = language,
                accentColor = accentColor,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            SwipePanel(
                state = state,
                language = language,
                accentColor = accentColor,
                onIntent = onIntent,
                modifier = Modifier
                    .weight(1.08f)
                    .fillMaxHeight()
            )
        }
    } else {
            PortraitFallback(
                state = state,
                language = language,
            accentColor = accentColor,
            onIntent = onIntent,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 44.dp)
        )
    }
}

@Composable
private fun SetupPage(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 44.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PanelCard(accentColor = accentColor, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = localizedStringResource(R.string.setup_label, language),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = localizedStringResource(R.string.swipe_styles_hint, language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        CustomizeCard(
            state = state,
            language = language,
            accentColor = accentColor,
            onIntent = onIntent
        )
    }
}

@Composable
private fun SwipePanel(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = buildList<DashboardPanel> {
        if (state.showCalendar) add(DashboardPanel.Calendar)
        if (state.showPomodoro) add(DashboardPanel.Pomodoro)
        add(DashboardPanel.Media)
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = localizedStringResource(R.string.swipe_vertical_hint, language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (pages[page]) {
                DashboardPanel.Calendar -> CalendarCard(
                    state = state,
                    language = language,
                    accentColor = accentColor
                )
                DashboardPanel.Pomodoro -> PomodoroCard(
                    state = state,
                    language = language,
                    accentColor = accentColor,
                    onIntent = onIntent
                )
                DashboardPanel.Media -> MediaCard(
                    state = state,
                    language = language,
                    accentColor = accentColor,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Composable
private fun ClockPanel(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val statusText = if (state.isCharging) {
        localizedStringResource(R.string.charging_label, language)
    } else {
        localizedStringResource(R.string.battery_idle_label, language)
    }
    val batteryProgress = state.batteryLevel / 100f

    PanelCard(modifier = modifier, accentColor = accentColor) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccentDot(accentColor = accentColor)
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                ClockFace(
                    style = state.clockStyle,
                    state = state,
                    accentColor = accentColor,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = localizedStringResource(R.string.swipe_setup_hint, language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = accentColor
                )
            }

            if (state.showBattery) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = localizedStringResource(R.string.battery_status, language, state.batteryLevel),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LinearProgressIndicator(
                        progress = { batteryProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(50)),
                        color = accentColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = localizedStringResource(R.string.orientation_hint, language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ClockFace(
    style: ClockStyle,
    state: StandTimeUiState,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    when (style) {
        ClockStyle.NOTHING -> Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = state.timeText,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 68.sp,
                    letterSpacing = (-2).sp,
                    lineHeight = 70.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AccentDot(accentColor = accentColor)
                Text(
                    text = state.dayText,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = state.dateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ClockStyle.PIXEL -> Column(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(accentColor.copy(alpha = 0.10f))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = state.timeText,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp,
                    letterSpacing = 1.sp
                ),
                color = accentColor
            )
            Text(
                text = state.dayText.uppercase(),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = state.dateText,
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ClockStyle.IPHONE -> Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = state.timeText,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    fontSize = 82.sp,
                    lineHeight = 84.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${state.dayText}, ${state.dateText}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ClockStyle.MINIMAL -> Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = state.timeText,
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 60.sp,
                    lineHeight = 62.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.dayText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.titleMedium,
                    color = accentColor
                )
                Text(
                    text = state.dateText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PortraitFallback(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        PanelCard(accentColor = accentColor, modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = localizedStringResource(R.string.portrait_title, language),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = localizedStringResource(R.string.portrait_body, language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ClockFace(
                    style = state.clockStyle,
                    state = state,
                    accentColor = accentColor,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        CalendarCard(state = state, language = language, accentColor = accentColor)
        if (state.showPomodoro) {
            PomodoroCard(
                state = state,
                language = language,
                accentColor = accentColor,
                onIntent = onIntent
            )
        }
        MediaCard(
            state = state,
            language = language,
            accentColor = accentColor,
            onIntent = onIntent
        )
    }
}

@Composable
private fun CalendarCard(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color
) {
    PanelCard(accentColor = accentColor, modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = localizedStringResource(R.string.calendar_title, language),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = state.monthTitle,
                style = MaterialTheme.typography.titleLarge,
                color = accentColor
            )
            CalendarGrid(
                weekDayLabels = state.weekDayLabels,
                cells = state.calendarCells,
                accentColor = accentColor
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    weekDayLabels: List<String>,
    cells: List<CalendarDayCell>,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        CalendarRow {
            weekDayLabels.forEach { label ->
                CalendarTextCell(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        cells.chunked(7).forEach { week ->
            CalendarRow {
                week.forEach { cell ->
                    CalendarTextCell(
                        text = cell.label,
                        color = if (cell.isCurrentMonth) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                        },
                        backgroundColor = if (cell.isToday) accentColor.copy(alpha = 0.20f) else Color.Transparent
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun RowScope.CalendarTextCell(
    text: String,
    color: Color,
    backgroundColor: Color = Color.Transparent
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PomodoroCard(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit
) {
    PanelCard(accentColor = accentColor, modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = localizedStringResource(R.string.pomodoro_title, language),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = state.remainingPomodoroText(),
                style = MaterialTheme.typography.displayMedium,
                color = accentColor
            )
            Text(
                text = if (state.isPomodoroRunning) {
                    localizedStringResource(R.string.pomodoro_running, language)
                } else {
                    localizedStringResource(R.string.pomodoro_paused, language)
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = localizedStringResource(R.string.presets_title, language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            ChipRow {
                state.pomodoroPresets.forEach { preset ->
                    FilterChip(
                        selected = state.selectedPomodoroMinutes == preset.minutes,
                        onClick = { onIntent(StandTimeIntent.SelectPomodoroPreset(preset.minutes)) },
                        label = {
                            Text(
                                localizedStringResource(
                                    R.string.pomodoro_preset_label,
                                    language,
                                    preset.label
                                )
                            )
                        }
                    )
                }
            }
            ChipRow {
                FilterChip(
                    selected = state.isPomodoroRunning,
                    onClick = { onIntent(StandTimeIntent.TogglePomodoroTimer) },
                    label = {
                        Text(
                            if (state.isPomodoroRunning) {
                                localizedStringResource(R.string.pomodoro_pause, language)
                            } else {
                                localizedStringResource(R.string.pomodoro_start, language)
                            }
                        )
                    }
                )
                FilterChip(
                    selected = false,
                    onClick = { onIntent(StandTimeIntent.ResetPomodoro) },
                    label = { Text(localizedStringResource(R.string.pomodoro_reset, language)) }
                )
            }
        }
    }
}

@Composable
private fun MediaCard(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit
) {
    val context = LocalContext.current

    PanelCard(accentColor = accentColor, modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = localizedStringResource(R.string.media_title, language),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!state.mediaPermissionGranted) {
                Text(
                    text = localizedStringResource(R.string.media_permission_body, language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                ) {
                    Text(localizedStringResource(R.string.media_enable_access, language))
                }
            } else if (!state.mediaSessionAvailable) {
                Text(
                    text = localizedStringResource(R.string.media_unavailable, language),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = state.mediaTitle.ifBlank {
                        localizedStringResource(R.string.media_title, language)
                    },
                    style = MaterialTheme.typography.displayMedium,
                    color = accentColor
                )
                Text(
                    text = state.mediaSubtitle.ifBlank {
                        localizedStringResource(R.string.media_source_value, language, state.mediaAppName)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = localizedStringResource(R.string.media_source_value, language, state.mediaAppName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ChipRow {
                    FilterChip(
                        selected = state.isMediaPlaying,
                        onClick = { onIntent(StandTimeIntent.ToggleMediaPlayback) },
                        label = {
                            Text(
                                if (state.isMediaPlaying) {
                                    localizedStringResource(R.string.media_pause, language)
                                } else {
                                    localizedStringResource(R.string.media_play, language)
                                }
                            )
                        }
                    )
                    FilterChip(
                        selected = false,
                        onClick = { onIntent(StandTimeIntent.SkipToNextTrack) },
                        label = { Text(localizedStringResource(R.string.media_next, language)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomizeCard(
    state: StandTimeUiState,
    language: StandTimeLanguage,
    accentColor: Color,
    onIntent: (StandTimeIntent) -> Unit
) {
    PanelCard(accentColor = accentColor, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = localizedStringResource(R.string.customize_title, language),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = localizedStringResource(R.string.language_title, language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            ChipRow {
                LanguageChip(
                    selected = state.language == StandTimeLanguage.ENGLISH,
                    label = "English",
                    onClick = { onIntent(StandTimeIntent.ChangeLanguage(StandTimeLanguage.ENGLISH)) }
                )
                LanguageChip(
                    selected = state.language == StandTimeLanguage.UZBEK,
                    label = "O'zbek",
                    onClick = { onIntent(StandTimeIntent.ChangeLanguage(StandTimeLanguage.UZBEK)) }
                )
                LanguageChip(
                    selected = state.language == StandTimeLanguage.RUSSIAN,
                    label = "Русский",
                    onClick = { onIntent(StandTimeIntent.ChangeLanguage(StandTimeLanguage.RUSSIAN)) }
                )
            }

            Text(
                text = localizedStringResource(R.string.theme_title, language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            ChipRow {
                FilterChip(
                    selected = state.themeMode == ThemeMode.DARK,
                    onClick = { if (state.themeMode != ThemeMode.DARK) onIntent(StandTimeIntent.ToggleTheme) },
                    label = { Text(localizedStringResource(R.string.dark_theme_label, language)) }
                )
                FilterChip(
                    selected = state.themeMode == ThemeMode.LIGHT,
                    onClick = { if (state.themeMode != ThemeMode.LIGHT) onIntent(StandTimeIntent.ToggleTheme) },
                    label = { Text(localizedStringResource(R.string.light_theme_label, language)) }
                )
            }

            Text(
                text = localizedStringResource(R.string.accent_title, language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            ChipRow {
                AccentChip(
                    label = localizedStringResource(R.string.accent_lime_label, language),
                    color = LimeAccent,
                    selected = state.accentPalette == AccentPalette.LIME,
                    onClick = { onIntent(StandTimeIntent.ChangeAccent(AccentPalette.LIME)) }
                )
                AccentChip(
                    label = localizedStringResource(R.string.accent_sky_label, language),
                    color = SkyAccent,
                    selected = state.accentPalette == AccentPalette.SKY,
                    onClick = { onIntent(StandTimeIntent.ChangeAccent(AccentPalette.SKY)) }
                )
                AccentChip(
                    label = localizedStringResource(R.string.accent_coral_label, language),
                    color = CoralAccent,
                    selected = state.accentPalette == AccentPalette.CORAL,
                    onClick = { onIntent(StandTimeIntent.ChangeAccent(AccentPalette.CORAL)) }
                )
            }

            Text(
                text = localizedStringResource(R.string.clock_style_title, language),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            ChipRow {
                FilterChip(
                    selected = state.clockStyle == ClockStyle.NOTHING,
                    onClick = { onIntent(StandTimeIntent.ChangeClockStyle(ClockStyle.NOTHING)) },
                    label = { Text(localizedStringResource(R.string.nothing_style_label, language)) }
                )
                FilterChip(
                    selected = state.clockStyle == ClockStyle.PIXEL,
                    onClick = { onIntent(StandTimeIntent.ChangeClockStyle(ClockStyle.PIXEL)) },
                    label = { Text(localizedStringResource(R.string.pixel_style_label, language)) }
                )
                FilterChip(
                    selected = state.clockStyle == ClockStyle.IPHONE,
                    onClick = { onIntent(StandTimeIntent.ChangeClockStyle(ClockStyle.IPHONE)) },
                    label = { Text(localizedStringResource(R.string.iphone_style_label, language)) }
                )
                FilterChip(
                    selected = state.clockStyle == ClockStyle.MINIMAL,
                    onClick = { onIntent(StandTimeIntent.ChangeClockStyle(ClockStyle.MINIMAL)) },
                    label = { Text(localizedStringResource(R.string.minimal_style_label, language)) }
                )
            }

            SettingRow(
                label = localizedStringResource(R.string.show_calendar_label, language),
                checked = state.showCalendar,
                onCheckedChange = { onIntent(StandTimeIntent.ToggleCalendar) }
            )
            SettingRow(
                label = localizedStringResource(R.string.show_battery_label, language),
                checked = state.showBattery,
                onCheckedChange = { onIntent(StandTimeIntent.ToggleBattery) }
            )
            SettingRow(
                label = localizedStringResource(R.string.show_pomodoro_label, language),
                checked = state.showPomodoro,
                onCheckedChange = { onIntent(StandTimeIntent.TogglePomodoro) }
            )
            SettingRow(
                label = localizedStringResource(R.string.show_seconds_label, language),
                checked = state.showSeconds,
                onCheckedChange = { onIntent(StandTimeIntent.ToggleSeconds) }
            )
        }
    }
}

@Composable
private fun PagerHeader(
    currentPage: Int,
    language: StandTimeLanguage,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val label = when (currentPage) {
        0 -> localizedStringResource(R.string.clock_styles_label, language)
        1 -> localizedStringResource(R.string.dashboard_label, language)
        else -> localizedStringResource(R.string.setup_label, language)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (index == currentPage) accentColor else MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PanelCard(
    accentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        tonalElevation = 10.dp,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun AccentChip(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(label)
            }
        }
    )
}

@Composable
private fun LanguageChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

@Composable
private fun AccentDot(accentColor: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(accentColor)
    )
}

@Composable
private fun ChipRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

private enum class DashboardPanel {
    Calendar,
    Pomodoro,
    Media
}
