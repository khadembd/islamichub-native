package com.islamichub.feature.prayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.preferences.AppPreferences
import com.islamichub.feature.home.PrayerState
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
    val timeMillis: Long,
    val state: PrayerState
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

    private val _methodText = MutableLiveData<String>()
    val methodText: LiveData<String> = _methodText

    init {
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        try {
            val lat = prefs.latitude.first()
            val lon = prefs.longitude.first()
            val name = prefs.locationName.first()
            val madhab = prefs.madhab.first()
            val method = prefs.prayerMethod.first()
            val tz = 6.0
            val asrFactor = if (madhab == "hanafi") 2 else 1
            val times = PrayerTimeCalculator.compute(LocalDate.now(), lat, lon, tz, PrayerTimeCalculator.MWL, asrFactor)

            val fmt = SimpleDateFormat("h:mm a", Locale("bn"))
            val now = System.currentTimeMillis()
            val prayers = listOf(
                Triple(times.fajr,    "ফজর",    "#FF6D45C7"),
                Triple(times.dhuhr,   "যোহর",   "#FF3B82F6"),
                Triple(times.asr,     "আসর",    "#FF8B5CF6"),
                Triple(times.maghrib, "মাগরিব", "#FFEC4899"),
                Triple(times.isha,    "ইশা",    "#FF6366F1")
            )

            // Determine next prayer
            val nextIndex = prayers.indexOfFirst { it.first > now }
            _prayerRows.value = prayers.mapIndexed { idx, (time, name, color) ->
                val state = when {
                    idx == nextIndex -> PrayerState.NEXT
                    nextIndex > 0 && idx == nextIndex - 1 -> PrayerState.CURRENT
                    time < now -> PrayerState.COMPLETED
                    else -> PrayerState.UPCOMING
                }
                PrayerRow(name, fmt.format(Date(time)), color, time, state)
            }

            // If no next prayer today, last one is current
            if (nextIndex == -1) {
                val updated = _prayerRows.value!!.toMutableList()
                updated[updated.lastIndex] = updated[updated.lastIndex].copy(state = PrayerState.CURRENT)
                _prayerRows.value = updated
            }

            _locationName.value = name
            _coordinates.value = "%.2f°N, %.2f°E".format(lat, lon)
            _methodText.value = "$method • ${if (madhab == "hanafi") "হানাফী" else "শাফেয়ী"}"
        } catch (e: Exception) {
            // Defensive: don't crash the prayer screen
            _locationName.value = "—"
            _coordinates.value = ""
            _methodText.value = ""
            _prayerRows.value = emptyList()
        }
    }

    suspend fun notificationsEnabled(): Boolean = prefs.notificationsEnabled.first()
    suspend fun notificationOffset(): Int = prefs.notificationOffsetMin.first()

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }

    fun setNotificationOffset(min: Int) {
        viewModelScope.launch { prefs.setNotificationOffsetMin(min) }
    }
}
