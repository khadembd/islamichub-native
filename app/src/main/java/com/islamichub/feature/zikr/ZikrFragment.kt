package com.islamichub.feature.zikr

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.R
import com.islamichub.databinding.FragmentZikrBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ZikrFragment : Fragment() {
    private var _binding: FragmentZikrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ZikrViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentZikrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tapTarget.setOnClickListener {
            viewModel.increment()
            vibrate(20)
        }
        binding.resetButton.setOnClickListener {
            viewModel.resetSession()
            vibrate(40)
        }

        // Zikr type toggle
        binding.zikrTypeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val type = when (checkedId) {
                R.id.zikrSubhanallah    -> ZikrType.SUBHANALLAH
                R.id.zikrAlhamdulillah  -> ZikrType.ALHAMDULILLAH
                R.id.zikrAllahuAkbar    -> ZikrType.ALLAHU_AKBAR
                else                    -> ZikrType.SUBHANALLAH
            }
            viewModel.setZikrType(type)
            vibrate(15)
        }

        binding.vibrateSwitch.setOnCheckedChangeListener { _, _ ->
            // Vibration preference is implicit — always vibrate if switch on
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentType.collectLatest { type ->
                binding.zikrArabic.text = type.arabic
                binding.targetValue.text = type.target.toString()
                binding.zikrTarget.text = "লক্ষ্য: ${bengaliNum(type.target)}"
                // Check matching toggle button
                val btnId = when (type) {
                    ZikrType.SUBHANALLAH   -> R.id.zikrSubhanallah
                    ZikrType.ALHAMDULILLAH -> R.id.zikrAlhamdulillah
                    ZikrType.ALLAHU_AKBAR  -> R.id.zikrAllahuAkbar
                    else                   -> R.id.zikrSubhanallah
                }
                if (binding.zikrTypeToggle.checkedButtonId != btnId) {
                    binding.zikrTypeToggle.check(btnId)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.count.collectLatest {
                binding.zikrCount.text = bengaliNum(it)
                if (it > 0 && it % viewModel.currentType.value.target == 0) {
                    vibrate(150)  // longer vibration on target reached
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyTotal.collectLatest { binding.dailyTotal.text = bengaliNum(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.grandTotal.collectLatest { binding.grandTotal.text = bengaliNum(it) }
        }
    }

    private fun vibrate(durationMs: Long) {
        if (!binding.vibrateSwitch.isChecked) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = requireContext().getSystemService(VibratorManager::class.java)
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                requireContext().getSystemService(Vibrator::class.java)
            } ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Defensive: vibration permission may be missing
        }
    }

    private fun bengaliNum(n: Int): String {
        val bnDigits = arrayOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
        return n.toString().map { if (it.isDigit()) bnDigits[it - '0'] else it }.joinToString("")
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
