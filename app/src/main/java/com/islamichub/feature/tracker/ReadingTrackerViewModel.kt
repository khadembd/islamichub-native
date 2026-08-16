package com.islamichub.feature.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import javax.inject.Inject

data class ReadingLogEntry(
    val date: String,
    val surah: String,
    val page: String
)

@HiltViewModel
class ReadingTrackerViewModel @Inject constructor(
    private val prefs: AppPreferences
) : ViewModel() {

    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()

    private val _weeklyCount = MutableStateFlow(0)
    val weeklyCount: StateFlow<Int> = _weeklyCount.asStateFlow()

    private val _recentLogs = MutableStateFlow<List<ReadingLogEntry>>(emptyList())
    val recentLogs: StateFlow<List<ReadingLogEntry>> = _recentLogs.asStateFlow()

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch {
            try {
                val raw = prefs.readingProgressSync()
                val logs = parseLogs(raw)
                val today = LocalDate.now().toString()
                _todayCount.value = logs.count { it.date == today }
                val weekAgo = LocalDate.now().minusDays(7).toString()
                _weeklyCount.value = logs.count { it.date >= weekAgo }
                _recentLogs.value = logs
            } catch (e: Exception) {
                _recentLogs.value = emptyList()
            }
        }
    }

    fun logReading(surah: String, page: String) {
        viewModelScope.launch {
            try {
                val raw = prefs.readingProgressSync()
                val arr = if (raw.isBlank()) JSONArray() else JSONArray(raw)
                val entry = JSONObject().apply {
                    put("date", LocalDate.now().toString())
                    put("surah", surah)
                    put("page", page)
                }
                arr.put(entry)
                prefs.setReadingProgressSync(arr.toString())
                loadLogs()
            } catch (e: Exception) {
                // Defensive
            }
        }
    }

    private fun parseLogs(raw: String): List<ReadingLogEntry> {
        if (raw.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                ReadingLogEntry(
                    date = obj.optString("date"),
                    surah = obj.optString("surah"),
                    page = obj.optString("page")
                )
            }
        } catch (e: Exception) { emptyList() }
    }
}
