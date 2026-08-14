package com.islamichub.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.model.AsmaulHusna
import com.islamichub.data.model.Hadith
import com.islamichub.data.repository.AssetRepository
import com.islamichub.services.PrayerTimeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class NextPrayerInfo(
    val name: String,
    val timeText: String,
    val timeMillis: Long
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _nextPrayerInfo = MutableLiveData<NextPrayerInfo>()
    val nextPrayerInfo: LiveData<NextPrayerInfo> = _nextPrayerInfo

    private val _prayerProgress = MutableLiveData<List<PrayerProgressRow>>()
    val prayerProgress: LiveData<List<PrayerProgressRow>> = _prayerProgress

    private val _dailyAyah = MutableLiveData<AsmaulHusna?>()
    val dailyAyah: LiveData<AsmaulHusna?> = _dailyAyah

    private val _dailyHadith = MutableLiveData<Hadith?>()
    val dailyHadith: LiveData<Hadith?> = _dailyHadith

    init {
        loadNextPrayer()
        loadDailyContent()
    }

    private fun loadNextPrayer() {
        viewModelScope.launch {
            try {
                val lat = 23.8103
                val lon = 90.4125
                val tz = 6.0
                val times = PrayerTimeCalculator.compute(
                    LocalDate.now(), lat, lon, tz, PrayerTimeCalculator.MWL, 1
                )
                val now = System.currentTimeMillis()
                val prayers = listOf(
                    times.fajr to "ফজর",
                    times.dhuhr to "যোহর",
                    times.asr to "আসর",
                    times.maghrib to "মাগরিব",
                    times.isha to "ইশা"
                )

                val fmt = SimpleDateFormat("h:mm a", Locale("bn"))
                // Determine next prayer
                val next = prayers.firstOrNull { it.first > now }
                    ?: (prayers.first().first + 24 * 60 * 60 * 1000L to prayers.first().second)
                _nextPrayerInfo.value = NextPrayerInfo(
                    name = next.second,
                    timeText = fmt.format(Date(next.first)),
                    timeMillis = next.first
                )

                // Build progress rows with state
                val rows = prayers.mapIndexed { idx, (time, name) ->
                    val state = when {
                        time < now && (idx == prayers.lastIndex || prayers[idx + 1].first > now) ->
                            // Last passed prayer = current prayer in progress
                            if (idx == prayers.lastIndex || prayers[idx + 1].first > now) PrayerState.CURRENT else PrayerState.COMPLETED
                        time < now -> PrayerState.COMPLETED
                        next.first == time -> PrayerState.NEXT
                        else -> PrayerState.UPCOMING
                    }
                    PrayerProgressRow(
                        name = name,
                        timeText = fmt.format(Date(time)),
                        state = state
                    )
                }
                _prayerProgress.value = rows
            } catch (e: Exception) {
                // Defensive: prayer calculation should not crash home screen
                _nextPrayerInfo.value = NextPrayerInfo("—", "—", System.currentTimeMillis() + 60_000)
                _prayerProgress.value = emptyList()
            }
        }
    }

    private fun loadDailyContent() {
        viewModelScope.launch {
            try {
                val list = assets.asmaulHusna()
                val dayOfYear = LocalDate.now().dayOfYear
                _dailyAyah.value = list.getOrNull(dayOfYear % list.size)
            } catch (e: Exception) {
                _dailyAyah.value = null
            }

            try {
                val collection = assets.hadith()
                val hadiths = collection.hadiths
                val dayOfYear = LocalDate.now().dayOfYear
                _dailyHadith.value = hadiths.getOrNull(dayOfYear % hadiths.size)
            } catch (e: Exception) {
                _dailyHadith.value = null
            }
        }
    }
}
