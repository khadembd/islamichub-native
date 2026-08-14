package com.islamichub.feature.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.data.local.entities.SalahTrackerEntity
import com.islamichub.databinding.FragmentSalahTrackerBinding
import com.islamichub.databinding.ItemSalahTodayBinding
import com.islamichub.databinding.ItemSalahWeekRowBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class SalahTrackerFragment : Fragment() {
    private var _binding: FragmentSalahTrackerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalahTrackerViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSalahTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWeekHeaders()
        observeTodayPrayers()
        observeWeeklyView()
        observeStreak()
    }

    private fun setupWeekHeaders() {
        val days = (0..6).map { offset ->
            val d = LocalDate.now().minusDays((6 - offset).toLong())
            d.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale("bn"))
        }
        binding.day1Header.text = days[0]
        binding.day2Header.text = days[1]
        binding.day3Header.text = days[2]
        binding.day4Header.text = days[3]
        binding.day5Header.text = days[4]
        binding.day6Header.text = days[5]
        binding.day7Header.text = days[6]
    }

    private fun observeTodayPrayers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todayEntries.collectLatest { entries ->
                renderTodayPrayers(entries)
            }
        }
    }

    private fun renderTodayPrayers(entries: List<SalahTrackerEntity>) {
        binding.todayPrayersContainer.removeAllViews()
        val today = LocalDate.now().toString()
        val prayerNames = listOf(
            "fajr" to "ফজর",
            "dhuhr" to "যোহর",
            "asr" to "আসর",
            "maghrib" to "মাগরিব",
            "isha" to "ইশা"
        )
        val completedCount = entries.count { it.completed }
        binding.completedTodayLabel.text = "আজ $completedCount/৫ নামাজ সম্পন্ন"

        prayerNames.forEach { (id, name) ->
            val itemBinding = ItemSalahTodayBinding.inflate(layoutInflater, binding.todayPrayersContainer, false)
            itemBinding.prayerName.text = name
            val entry = entries.find { it.prayerId == id }
            itemBinding.completedSwitch.isChecked = entry?.completed == true
            itemBinding.completedSwitch.setOnCheckedChangeListener { _, checked ->
                viewModel.togglePrayer(id, today, checked)
            }
            binding.todayPrayersContainer.addView(itemBinding.root)
        }
    }

    private fun observeWeeklyView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weeklyEntries.collectLatest { entries ->
                renderWeeklyRows(entries)
            }
        }
    }

    private fun renderWeeklyRows(entries: List<SalahTrackerEntity>) {
        binding.weeklyRowsContainer.removeAllViews()
        val startDate = LocalDate.now().minusDays(6).toString()
        val prayerNames = listOf("fajr", "dhuhr", "asr", "maghrib", "isha")
        prayerNames.forEach { prayerId ->
            val rowBinding = ItemSalahWeekRowBinding.inflate(layoutInflater, binding.weeklyRowsContainer, false)
            rowBinding.prayerLabel.text = when (prayerId) {
                "fajr"    -> "ফজর"
                "dhuhr"   -> "যোহর"
                "asr"     -> "আসর"
                "maghrib" -> "মাগরিব"
                "isha"    -> "ইশা"
                else      -> prayerId
            }
            // 7 day indicators
            val cells = listOf(rowBinding.d1, rowBinding.d2, rowBinding.d3, rowBinding.d4, rowBinding.d5, rowBinding.d6, rowBinding.d7)
            for (i in 0..6) {
                val day = LocalDate.now().minusDays((6 - i).toLong()).toString()
                val completed = entries.any { it.prayerId == prayerId && it.date == day && it.completed }
                cells[i].setImageResource(if (completed) android.R.drawable.checkbox_on_background else android.R.drawable.checkbox_off_background)
                cells[i].alpha = if (completed) 1f else 0.3f
            }
            binding.weeklyRowsContainer.addView(rowBinding.root)
        }
    }

    private fun observeStreak() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.streakDays.collectLatest { days ->
                binding.streakValue.text = bengaliNum(days)
                binding.streakLabel.text = "$days দিন ধরে ধারাবাহিক"
            }
        }
    }

    private fun bengaliNum(n: Int): String {
        val bnDigits = arrayOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
        return n.toString().map { if (it.isDigit()) bnDigits[it - '0'] else it }.joinToString("")
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
