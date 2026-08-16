package com.islamichub.feature.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.databinding.FragmentReadingTrackerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ReadingTrackerFragment — tracks Quran reading progress.
 * Per source trackers.js: READING_LOG = 'ih_reading_tracker_progress'
 * User can log pages/surahs read, set daily targets, view streak.
 */
@AndroidEntryPoint
class ReadingTrackerFragment : Fragment() {
    private var _binding: FragmentReadingTrackerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReadingTrackerViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReadingTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logButton.setOnClickListener {
            val surah = binding.surahInput.text?.toString().orEmpty().trim()
            val page = binding.pageInput.text?.toString().orEmpty().trim()
            if (surah.isNotEmpty() || page.isNotEmpty()) {
                viewModel.logReading(surah, page)
                binding.surahInput.text?.clear()
                binding.pageInput.text?.clear()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todayCount.collectLatest { count ->
                binding.todayCount.text = bengaliNum(count)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weeklyCount.collectLatest { count ->
                binding.weeklyCount.text = bengaliNum(count)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recentLogs.collectLatest { logs ->
                binding.logsText.text = if (logs.isEmpty()) {
                    "এখনো কোনো পড়া লগ করা হয়নি"
                } else {
                    logs.takeLast(10).reversed().joinToString("\n") { entry ->
                        "• ${entry.date} — সূরা ${entry.surah} • পৃষ্ঠা ${entry.page}"
                    }
                }
            }
        }
    }

    private fun bengaliNum(n: Int): String {
        val bn = arrayOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
        return n.toString().map { if (it.isDigit()) bn[it - '0'] else it }.joinToString("")
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
