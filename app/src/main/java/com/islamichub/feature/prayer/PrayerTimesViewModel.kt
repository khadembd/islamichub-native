package com.islamichub.feature.prayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.preferences.AppPreferences
import com.islamichub.services.PrayerTimeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class PrayerRow(
    val name: String,
    val timeText: String,
    val colorHex: String,
    val timeMillis: Long
)

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val prefs: AppPreferences
) : ViewModel() {

    private val _prayerRows = MutableLiveData<List<PrayerRow>>()
    val prayerRows: LiveData<List<PrayerRow>> = _prayerRows

    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String> = _locationName

    private val _coordinates = MutableLiveData<String>()
    val coordinates: LiveData<String> = _coordinates

    init {
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        val lat = prefs.latitude.first()
        val lon = prefs.longitude.first()
        val name = prefs.locationName.first()
        val madhab = prefs.madhab.first()
        val tz = 6.0 // Bangladesh; future: derive from LocationManager timezone
        val asrFactor = if (madhab == "hanafi") 2 else 1
        val times = PrayerTimeCalculator.compute(LocalDate.now(), lat, lon, tz, PrayerTimeCalculator.MWL, asrFactor)

        val fmt = SimpleDateFormat("h:mm a", Locale("bn"))
        _prayerRows.value = listOf(
            PrayerRow("ফজর",    fmt.format(Date(times.fajr)),    "#F59E0B", times.fajr),
            PrayerRow("যোহর",   fmt.format(Date(times.dhuhr)),   "#3B82F6", times.dhuhr),
            PrayerRow("আসর",    fmt.format(Date(times.asr)),     "#10B981", times.asr),
            PrayerRow("মাগরিব", fmt.format(Date(times.maghrib)), "#8B5CF6", times.maghrib),
            PrayerRow("ইশা",    fmt.format(Date(times.isha)),    "#6366F1", times.isha)
        )
        _locationName.value = name
        _coordinates.value = "%.2f°N, %.2f°E".format(lat, lon)
    }

    suspend fun notificationsEnabled(): Boolean = prefs.notificationsEnabled.first()

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            prefs.setNotificationsEnabled(enabled)
        }
    }
}
