package com.islamichub.feature.prayer

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.R
import com.islamichub.data.preferences.AppPreferences
import com.islamichub.databinding.FragmentPrayerTimesBinding
import com.islamichub.databinding.ItemPrayerTimeBinding
import com.islamichub.services.PrayerNotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PrayerTimesFragment : Fragment() {
    private var _binding: FragmentPrayerTimesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrayerTimesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPrayerTimesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.prayerRows.observe(viewLifecycleOwner) { rows -> renderPrayers(rows) }
        viewModel.locationName.observe(viewLifecycleOwner) { binding.locationName.text = it }
        viewModel.coordinates.observe(viewLifecycleOwner) { binding.coordinatesText.text = it }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setNotificationsEnabled(isChecked)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val enabled = viewModel.notificationsEnabled()
            binding.notificationsSwitch.isChecked = enabled
        }
    }

    private fun renderPrayers(rows: List<PrayerRow>) {
        binding.prayerListContainer.removeAllViews()
        rows.forEach { row ->
            val itemBinding = ItemPrayerTimeBinding.inflate(layoutInflater, binding.prayerListContainer, false)
            itemBinding.prayerName.text = row.name
            itemBinding.prayerTime.text = row.timeText
            itemBinding.prayerColorDot.background.setTint(Color.parseColor(row.colorHex))
            binding.prayerListContainer.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
