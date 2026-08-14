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
import com.islamichub.databinding.FragmentPrayerTimesBinding
import com.islamichub.databinding.ItemPrayerTimeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
        viewModel.methodText.observe(viewLifecycleOwner) { binding.methodText.text = it }

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setNotificationsEnabled(isChecked)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val enabled = viewModel.notificationsEnabled()
            binding.notificationsSwitch.isChecked = enabled
        }

        binding.reminderOffsetSlider.addOnChangeListener { _, value, _ ->
            val min = value.toInt()
            binding.reminderOffsetLabel.text = "আজানের ${min} মিনিট আগে"
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.setNotificationOffset(min)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val offset = viewModel.notificationOffset()
            binding.reminderOffsetSlider.value = offset.toFloat()
            binding.reminderOffsetLabel.text = "আজানের ${offset} মিনিট আগে"
        }
    }

    private fun renderPrayers(rows: List<PrayerRow>) {
        binding.prayerListContainer.removeAllViews()
        rows.forEach { row ->
            val itemBinding = ItemPrayerTimeBinding.inflate(layoutInflater, binding.prayerListContainer, false)
            itemBinding.prayerName.text = row.name
            itemBinding.prayerTime.text = row.timeText
            itemBinding.prayerColorDot.background.setTint(Color.parseColor(row.colorHex))

            // State chip styling
            val (chipText, chipBg, chipTextCol) = when (row.state) {
                PrayerState.CURRENT   -> Triple("চলমান",   R.drawable.bg_state_chip_current, R.color.on_primary)
                PrayerState.NEXT      -> Triple("পরবর্তী", R.drawable.bg_state_chip_next,    R.color.gold_dark)
                PrayerState.COMPLETED -> Triple("সম্পন্ন",  R.drawable.bg_state_chip_done,    R.color.status_success)
                PrayerState.UPCOMING  -> Triple("আসছে",     R.drawable.bg_state_chip_next,    R.color.text_secondary)
            }
            itemBinding.prayerStateChip.text = chipText
            itemBinding.prayerStateChip.setBackgroundResource(chipBg)
            itemBinding.prayerStateChip.setTextColor(requireContext().getColor(chipTextCol))

            binding.prayerListContainer.addView(itemBinding.root)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
