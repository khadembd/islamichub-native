package com.islamichub.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.model.AsmaulHusna
import com.islamichub.data.repository.AssetRepository
import com.islamichub.services.PrayerTimeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * HomeViewModel — loads next prayer + daily ayah.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _nextPrayerInfo = MutableLiveData<String>()
    val nextPrayerInfo: LiveData<String> = _nextPrayerInfo

    private val _dailyAyah = MutableLiveData<AsmaulHusna?>()
    val dailyAyah: LiveData<AsmaulHusna?> = _dailyAyah

    init {
        loadNextPrayer()
        loadDailyAyah()
    }

    private fun loadNextPrayer() {
        viewModelScope.launch {
            // Use Dhaka as default; the prayer screen will refine with user's location.
            val lat = 23.8103
            val lon = 90.4125
            val tz = 6.0
            val times = PrayerTimeCalculator.compute(
                LocalDate.now(), lat, lon, tz, PrayerTimeCalculator.MWL, 1
            )
            val now = System.currentTimeMillis()
            val nextPrayer = listOf(
                times.fajr to "ফজর",
                times.dhuhr to "যোহর",
                times.asr to "আসর",
                times.maghrib to "মাগরিব",
                times.isha to "ইশা"
            ).firstOrNull { it.first > now } ?: (times.fajr to "ফজর (আগামীকাল)")

            val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale("bn"))
            val timeStr = formatter.format(java.util.Date(nextPrayer.first))
            _nextPrayerInfo.value = "${nextPrayer.second} • $timeStr"
        }
    }

    private fun loadDailyAyah() {
        viewModelScope.launch {
            // Pick a deterministic "ayah of the day" from Asmaul Husna
            // (used here as a daily devotional snippet for v1; future versions
            // will use a curated ayah database).
            val list = assets.asmaulHusna()
            val dayOfYear = LocalDate.now().dayOfYear
            val picked = list.getOrNull(dayOfYear % list.size)
            _dailyAyah.value = picked
        }
    }
}
