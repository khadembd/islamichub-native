package com.islamichub.feature.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.local.dao.SalahTrackerDao
import com.islamichub.data.local.entities.SalahTrackerEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SalahTrackerViewModel @Inject constructor(
    private val dao: SalahTrackerDao
) : ViewModel() {

    private val today = LocalDate.now().toString()
    private val weekStart = LocalDate.now().minusDays(6).toString()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todayEntries: StateFlow<List<SalahTrackerEntity>> = dao.observeForDate(today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val weeklyEntries: StateFlow<List<SalahTrackerEntity>> = dao.observeForRange(weekStart, today)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val streakDays: StateFlow<Int> = dao.observeStreakDates()
        .map { dates ->
            // Compute current streak — consecutive days ending today or yesterday
            val sorted = dates.sortedDescending()
            var streak = 0
            var expected = LocalDate.now()
            for (dateStr in sorted) {
                val d = runCatching { LocalDate.parse(dateStr) }.getOrNull() ?: continue
                if (d == expected) {
                    streak++
                    expected = expected.minusDays(1)
                } else if (d == expected.minusDays(1)) {
                    // today not yet completed, but yesterday did — streak continues
                    expected = d.minusDays(1)
                    streak++
                } else break
            }
            streak
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun togglePrayer(prayerId: String, date: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                dao.upsert(
                    SalahTrackerEntity(
                        date = date,
                        prayerId = prayerId,
                        completed = completed,
                        completedAt = if (completed) System.currentTimeMillis() else null
                    )
                )
            } catch (e: Exception) {
                // Defensive: database errors must not crash tracker UI
            }
        }
    }
}
