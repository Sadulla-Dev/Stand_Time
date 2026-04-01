package com.example.standtime.standtime

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.BatteryManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.standtime.standtime.feature.utils.CalendarDayCell
import com.example.standtime.standtime.feature.utils.StandTimeIntent
import com.example.standtime.standtime.feature.utils.StandTimeLanguage
import com.example.standtime.standtime.feature.utils.StandTimeMediaService
import com.example.standtime.standtime.feature.utils.StandTimeUiState
import com.example.standtime.standtime.feature.utils.ThemeMode
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import kotlin.coroutines.resume

class StandTimeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        StandTimeUiState(
            locationPermissionGranted = hasLocationPermission()
        )
    )
    val uiState: StateFlow<StandTimeUiState> = _uiState.asStateFlow()

    init {
        startClock()
        startPomodoroTicker()
        if (_uiState.value.locationPermissionGranted) {
            refreshWeather()
        }
    }

    fun onIntent(intent: StandTimeIntent) {
        when (intent) {
            StandTimeIntent.ToggleTheme -> _uiState.update { state ->
                state.copy(
                    themeMode = if (state.themeMode == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
                )
            }

            is StandTimeIntent.ChangeLanguage -> {
                _uiState.update { state -> state.copy(language = intent.language) }
                if (_uiState.value.locationPermissionGranted) {
                    refreshWeather()
                }
            }

            is StandTimeIntent.ChangeAccent -> _uiState.update { state ->
                state.copy(accentPalette = intent.palette)
            }

            is StandTimeIntent.ChangeClockStyle -> _uiState.update { state ->
                state.copy(clockStyle = intent.clockStyle)
            }

            StandTimeIntent.ToggleCalendar -> _uiState.update { state ->
                state.copy(showCalendar = !state.showCalendar)
            }

            StandTimeIntent.ToggleWeather -> _uiState.update { state ->
                state.copy(showWeather = !state.showWeather)
            }

            StandTimeIntent.ToggleBattery -> _uiState.update { state ->
                state.copy(showBattery = !state.showBattery)
            }

            StandTimeIntent.TogglePomodoro -> _uiState.update { state ->
                state.copy(showPomodoro = !state.showPomodoro)
            }

            StandTimeIntent.ToggleSeconds -> _uiState.update { state ->
                state.copy(showSeconds = !state.showSeconds)
            }

            is StandTimeIntent.LocationPermissionChanged -> {
                _uiState.update { state ->
                    state.copy(locationPermissionGranted = intent.granted)
                }
                if (intent.granted) {
                    refreshWeather()
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isWeatherLoading = false,
                            weatherSummary = "",
                            weatherTemperature = "",
                            weatherWind = "",
                            locationName = "",
                            latitudeText = "",
                            longitudeText = "",
                            weatherError = ""
                        )
                    }
                }
            }

            StandTimeIntent.RefreshWeather -> refreshWeather()

            is StandTimeIntent.SelectPomodoroPreset -> _uiState.update { state ->
                state.copy(
                    selectedPomodoroMinutes = intent.minutes,
                    pomodoroRemainingSeconds = intent.minutes * 60,
                    isPomodoroRunning = false
                )
            }

            StandTimeIntent.TogglePomodoroTimer -> _uiState.update { state ->
                state.copy(isPomodoroRunning = !state.isPomodoroRunning)
            }

            StandTimeIntent.ResetPomodoro -> _uiState.update { state ->
                state.copy(
                    pomodoroRemainingSeconds = state.selectedPomodoroMinutes * 60,
                    isPomodoroRunning = false
                )
            }

            StandTimeIntent.ToggleMediaPlayback -> StandTimeMediaService.togglePlayback()
            StandTimeIntent.SkipToNextTrack -> StandTimeMediaService.skipToNext()

            is StandTimeIntent.ChangeGalleryStyleIndex -> _uiState.update { state ->
                state.copy(selectedGalleryStyleIndex = intent.index)
            }
        }
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                val state = _uiState.value
                val locale = state.language.toLocale()
                val now = Date()
                val timePattern = if (state.showSeconds) "HH:mm:ss" else "HH:mm"
                val timeText = SimpleDateFormat(timePattern, locale).format(now)
                val dateText = SimpleDateFormat("d MMMM yyyy", locale).format(now)
                val dayText = SimpleDateFormat("EEEE", locale).format(now)
                val calendarState = buildCalendarState(locale)
                val batterySnapshot = readBatterySnapshot()
                val mediaSnapshot = StandTimeMediaService.snapshot(getApplication())

                _uiState.update {
                    it.copy(
                        timeText = timeText,
                        dateText = dateText,
                        dayText = dayText.replaceFirstChar { char ->
                            if (char.isLowerCase()) char.titlecase(locale) else char.toString()
                        },
                        monthTitle = calendarState.monthTitle,
                        weekDayLabels = calendarState.weekHeaders,
                        calendarCells = calendarState.cells,
                        batteryLevel = batterySnapshot.level,
                        isCharging = batterySnapshot.isCharging,
                        mediaPermissionGranted = mediaSnapshot.permissionGranted,
                        mediaSessionAvailable = mediaSnapshot.sessionAvailable,
                        mediaAppName = mediaSnapshot.appName,
                        mediaTitle = mediaSnapshot.title,
                        mediaSubtitle = mediaSnapshot.subtitle,
                        isMediaPlaying = mediaSnapshot.isPlaying
                    )
                }
                delay(1_000)
            }
        }
    }

    private fun startPomodoroTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1_000)
                _uiState.update { state ->
                    if (!state.isPomodoroRunning) {
                        state
                    } else if (state.pomodoroRemainingSeconds <= 1) {
                        state.copy(
                            pomodoroRemainingSeconds = state.selectedPomodoroMinutes * 60,
                            isPomodoroRunning = false
                        )
                    } else {
                        state.copy(pomodoroRemainingSeconds = state.pomodoroRemainingSeconds - 1)
                    }
                }
            }
        }
    }

    private fun refreshWeather() {
        if (!hasLocationPermission()) {
            _uiState.update { it.copy(locationPermissionGranted = false, isWeatherLoading = false) }
            return
        }

        viewModelScope.launch {
            val language = _uiState.value.language
            val locale = language.toLocale()
            _uiState.update {
                it.copy(
                    locationPermissionGranted = true,
                    isWeatherLoading = true,
                    weatherError = ""
                )
            }

            val snapshot = runCatching {
                loadWeatherSnapshot(locale)
            }.getOrElse {
                WeatherSnapshot(
                    locationName = "",
                    latitudeText = "",
                    longitudeText = "",
                    temperatureText = "",
                    weatherSummary = "",
                    windText = "",
                    errorMessage = weatherErrorMessage(language)
                )
            }

            _uiState.update { state ->
                state.copy(
                    isWeatherLoading = false,
                    locationPermissionGranted = true,
                    locationName = snapshot.locationName,
                    latitudeText = snapshot.latitudeText,
                    longitudeText = snapshot.longitudeText,
                    weatherTemperature = snapshot.temperatureText,
                    weatherSummary = snapshot.weatherSummary,
                    weatherWind = snapshot.windText,
                    weatherError = snapshot.errorMessage
                )
            }
        }
    }

    private suspend fun loadWeatherSnapshot(locale: Locale): WeatherSnapshot {
        val context = getApplication<Application>()
        val location = fetchCurrentLocation(context) ?: return WeatherSnapshot(
            locationName = "",
            latitudeText = "",
            longitudeText = "",
            temperatureText = "",
            weatherSummary = "",
            windText = "",
            errorMessage = locationUnavailableMessage(_uiState.value.language)
        )

        val placeName = withContext(Dispatchers.IO) {
            resolvePlaceName(location, locale)
        }
        val weather = withContext(Dispatchers.IO) {
            fetchWeather(location.latitude, location.longitude, _uiState.value.language)
        }

        return WeatherSnapshot(
            locationName = placeName.ifBlank { fallbackLocationLabel(location) },
            latitudeText = "%.4f".format(locale, location.latitude),
            longitudeText = "%.4f".format(locale, location.longitude),
            temperatureText = weather.temperatureText,
            weatherSummary = weather.summary,
            windText = weather.windText,
            errorMessage = weather.errorMessage
        )
    }

    private fun readBatterySnapshot(): BatterySnapshot {
        val context = getApplication<Application>()
        val batteryStatus = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) ?: 0
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val percentage = if (scale > 0) (level * 100) / scale else 0
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        return BatterySnapshot(level = percentage.coerceIn(0, 100), isCharging = charging)
    }

    private fun buildCalendarState(locale: Locale): CalendarState {
        val today = Calendar.getInstance(locale)
        val working = Calendar.getInstance(locale).apply { set(Calendar.DAY_OF_MONTH, 1) }
        val monthTitle = SimpleDateFormat("MMMM yyyy", locale).format(working.time)
        val firstDayOfWeek = working.firstDayOfWeek
        val weekHeaders = buildList {
            repeat(7) { index ->
                val headerCalendar = Calendar.getInstance(locale).apply {
                    set(Calendar.DAY_OF_WEEK, ((firstDayOfWeek - 1 + index) % 7) + 1)
                }
                add(
                    SimpleDateFormat("EE", locale).format(headerCalendar.time).replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase(locale) else char.toString()
                    }
                )
            }
        }

        val leadingBlanks = (7 + (working.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek)) % 7
        val daysInMonth = working.getActualMaximum(Calendar.DAY_OF_MONTH)
        val cells = buildList {
            repeat(leadingBlanks) {
                add(CalendarDayCell(label = "", isToday = false, isCurrentMonth = false))
            }
            for (day in 1..daysInMonth) {
                add(
                    CalendarDayCell(
                        label = day.toString(),
                        isToday = today.get(Calendar.YEAR) == working.get(Calendar.YEAR) &&
                                today.get(Calendar.MONTH) == working.get(Calendar.MONTH) &&
                                today.get(Calendar.DAY_OF_MONTH) == day,
                        isCurrentMonth = true
                    )
                )
            }
            while (size % 7 != 0) {
                add(CalendarDayCell(label = "", isToday = false, isCurrentMonth = false))
            }
        }

        return CalendarState(
            monthTitle = monthTitle.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(locale) else char.toString()
            },
            weekHeaders = weekHeaders,
            cells = cells
        )
    }

    private fun hasLocationPermission(): Boolean {
        val context = getApplication<Application>()
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private suspend fun fetchCurrentLocation(context: Application): Location? {
        val locationManager = context.getSystemService(LocationManager::class.java) ?: return null
        val provider = when {
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            else -> null
        }
        if (provider == null) {
            return bestLastKnownLocation(locationManager)
        }

        return withTimeoutOrNull(10_000) {
            suspendCancellableCoroutine { continuation ->
                val cancellationSignal = CancellationSignal()
                continuation.invokeOnCancellation { cancellationSignal.cancel() }
                LocationManagerCompat.getCurrentLocation(
                    locationManager,
                    provider,
                    cancellationSignal,
                    ContextCompat.getMainExecutor(context)
                ) { location ->
                    if (continuation.isActive) {
                        continuation.resume(location ?: bestLastKnownLocation(locationManager))
                    }
                }
            }
        } ?: bestLastKnownLocation(locationManager)
    }

    @SuppressLint("MissingPermission")
    private fun bestLastKnownLocation(locationManager: LocationManager): Location? {
        val providers = listOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        return providers
            .mapNotNull { provider -> runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull() }
            .minByOrNull { it.accuracy }
    }

    @Suppress("DEPRECATION")
    private fun resolvePlaceName(location: Location, locale: Locale): String {
        val context = getApplication<Application>()
        val geocoder = Geocoder(context, locale)
        val address = runCatching {
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
        }.getOrNull()
        val locality = address?.locality ?: address?.subAdminArea ?: address?.adminArea
        val country = address?.countryName
        return listOfNotNull(locality, country).joinToString(", ")
    }

    private fun fetchWeather(latitude: Double, longitude: Double, language: StandTimeLanguage): WeatherApiSnapshot {
        val url = URL(
            "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,weather_code,wind_speed_10m&timezone=auto"
        )
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout = 8_000
            requestMethod = "GET"
        }

        return try {
            val response = BufferedReader(connection.inputStream.reader()).use { it.readText() }
            val json = JSONObject(response)
            val current = json.getJSONObject("current")
            val units = json.getJSONObject("current_units")
            val temperature = current.optDouble("temperature_2m", Double.NaN)
            val windSpeed = current.optDouble("wind_speed_10m", Double.NaN)
            val weatherCode = current.optInt("weather_code", -1)
            WeatherApiSnapshot(
                temperatureText = if (temperature.isNaN()) "" else "${temperature.toInt()}${units.optString("temperature_2m", "°C")}",
                summary = weatherSummaryForCode(weatherCode, language),
                windText = if (windSpeed.isNaN()) "" else "${windSpeed.toInt()} ${units.optString("wind_speed_10m", "km/h")}",
                errorMessage = ""
            )
        } catch (_: Exception) {
            WeatherApiSnapshot(
                temperatureText = "",
                summary = "",
                windText = "",
                errorMessage = weatherErrorMessage(language)
            )
        } finally {
            connection.disconnect()
        }
    }

    private fun weatherSummaryForCode(code: Int, language: StandTimeLanguage): String {
        return when (language) {
            StandTimeLanguage.UZBEK -> when (code) {
                0 -> "Ochiq osmon"
                1, 2 -> "Qisman bulutli"
                3 -> "Bulutli"
                45, 48 -> "Tuman"
                51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Yomg'ir"
                71, 73, 75, 77, 85, 86 -> "Qor"
                95, 96, 99 -> "Momaqaldiroq"
                else -> "Ob-havo"
            }

            StandTimeLanguage.RUSSIAN -> when (code) {
                0 -> "Ясно"
                1, 2 -> "Малооблачно"
                3 -> "Облачно"
                45, 48 -> "Туман"
                51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Дождь"
                71, 73, 75, 77, 85, 86 -> "Снег"
                95, 96, 99 -> "Гроза"
                else -> "Погода"
            }

            StandTimeLanguage.ENGLISH -> when (code) {
                0 -> "Clear sky"
                1, 2 -> "Partly cloudy"
                3 -> "Cloudy"
                45, 48 -> "Fog"
                51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Rain"
                71, 73, 75, 77, 85, 86 -> "Snow"
                95, 96, 99 -> "Thunderstorm"
                else -> "Weather"
            }
        }
    }

    private fun weatherErrorMessage(language: StandTimeLanguage): String = when (language) {
        StandTimeLanguage.UZBEK -> "Ob-havo ma'lumotini yuklab bo'lmadi"
        StandTimeLanguage.RUSSIAN -> "Не удалось загрузить погоду"
        StandTimeLanguage.ENGLISH -> "Could not load weather data"
    }

    private fun locationUnavailableMessage(language: StandTimeLanguage): String = when (language) {
        StandTimeLanguage.UZBEK -> "Joylashuv aniqlanmadi"
        StandTimeLanguage.RUSSIAN -> "Местоположение не найдено"
        StandTimeLanguage.ENGLISH -> "Location unavailable"
    }

    private fun fallbackLocationLabel(location: Location): String {
        return "${location.latitude.formatCoordinate()}, ${location.longitude.formatCoordinate()}"
    }

    private fun Double.formatCoordinate(): String = String.format(Locale.US, "%.4f", this)

    private fun StandTimeLanguage.toLocale(): Locale = when (this) {
        StandTimeLanguage.ENGLISH -> Locale.ENGLISH
        StandTimeLanguage.UZBEK -> Locale.forLanguageTag("uz")
        StandTimeLanguage.RUSSIAN -> Locale.forLanguageTag("ru")
    }
}

private data class BatterySnapshot(
    val level: Int,
    val isCharging: Boolean
)

private data class CalendarState(
    val monthTitle: String,
    val weekHeaders: List<String>,
    val cells: List<CalendarDayCell>
)

private data class WeatherSnapshot(
    val locationName: String,
    val latitudeText: String,
    val longitudeText: String,
    val temperatureText: String,
    val weatherSummary: String,
    val windText: String,
    val errorMessage: String
)

private data class WeatherApiSnapshot(
    val temperatureText: String,
    val summary: String,
    val windText: String,
    val errorMessage: String
)