package com.islamichub.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.repository.PrayerTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HijriDateViewModel @Inject constructor(
    private val prayerRepo: PrayerTimeRepository
) : ViewModel() {

    private val _hijriDate = MutableStateFlow<String?>(null)
    val hijriDate: StateFlow<String?> = _hijriDate.asStateFlow()

    init {
        loadHijri()
    }

    private fun loadHijri() {
        viewModelScope.launch {
            try {
                val data = prayerRepo.fetchPrayerTimes()
                val hijri = data?.date?.hijri
                if (hijri != null) {
                    val day = hijri.day
                    val month = hijri.month?.en ?: ""
                    val year = hijri.year
                    _hijriDate.value = "$day $month $year হিজরি"
                } else {
                    _hijriDate.value = null
                }
            } catch (e: Exception) {
                _hijriDate.value = null
            }
        }
    }
}
