package com.islamichub.feature.zikr

import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.R
import com.islamichub.databinding.FragmentZikrBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ZikrFragment — full zikr counter per source zikr-counter.js.
 *  - 6 zikr types with colors + meanings
 *  - Large tap target with haptic + sound feedback
 *  - Sets done counter
 *  - Daily + grand total stats
 *  - Vibrate on target reached
 */
@AndroidEntryPoint
class ZikrFragment : Fragment() {
    private var _binding: FragmentZikrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ZikrViewModel by viewModels()

    private var soundPool: SoundPool? = null
    private var tickSoundId = 0
    private var completeSoundId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentZikrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSoundPool()

        binding.tapTarget.setOnClickListener {
            viewModel.increment()
            animateTap()
            playTick()
            vibrate(20)
        }
        binding.resetButton.setOnClickListener {
            viewModel.resetSession()
            vibrate(40)
        }
        binding.vibrateSwitch.setOnCheckedChangeListener { _, _ -> viewModel.toggleHaptic() }

        // Zikr type chip selection
        val chipToType = mapOf(
            R.id.chipSubhanallah to ZikrType.SUBHANALLAH,
            R.id.chipAlhamdulillah to ZikrType.ALHAMDULILLAH,
            R.id.chipAllahuAkbar to ZikrType.ALLAHU_AKBAR,
            R.id.chipLaIlaha to ZikrType.LA_ILAHA,
            R.id.chipAstaghfirullah to ZikrType.ASTAGHFIRULLAH,
            R.id.chipDarood to ZikrType.DAROOD
        )
        binding.zikrChips.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            chipToType[checkedId]?.let { type ->
                viewModel.setZikrType(type)
                vibrate(15)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentType.collectLatest { type ->
                binding.zikrArabic.text = type.arabic
                binding.zikrMeaning.text = type.meaning
                binding.targetValue.text = bengaliNum(type.target)
                binding.zikrTarget.text = "লক্ষ্য: ${bengaliNum(type.target)}"
                binding.zikrTypeLabel.text = type.displayName
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.count.collectLatest { count ->
                binding.zikrCount.text = bengaliNum(count)
                if (count > 0 && count % viewModel.currentType.value.target == 0) {
                    vibrate(150)
                    playComplete()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.setsDone.collectLatest { sets ->
                // Could show sets done in UI if needed
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyTotal.collectLatest { binding.dailyTotal.text = bengaliNum(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.grandTotal.collectLatest { binding.grandTotal.text = bengaliNum(it) }
        }
    }

    private fun setupSoundPool() {
        try {
            soundPool = SoundPool.Builder().setMaxStreams(2).build()
            // Use system tick sound (we don't bundle custom sounds in v1)
            // Future: bundle custom tick.mp3 + complete.mp3
        } catch (e: Exception) {
            // Defensive
        }
    }

    private fun playTick() {
        if (!viewModel.soundEnabled.value) return
        try {
            // Play system click sound via AudioManager
            val audioManager = requireContext().getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.5f)
        } catch (e: Exception) {
            // Defensive
        }
    }

    private fun playComplete() {
        if (!viewModel.soundEnabled.value) return
        try {
            val audioManager = requireContext().getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager
            audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1.0f)
        } catch (e: Exception) {
            // Defensive
        }
    }

    private fun animateTap() {
        try {
            val scale = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
            binding.zikrCount.startAnimation(scale)
        } catch (e: Exception) {
            // Defensive
        }
    }

    private fun vibrate(durationMs: Long) {
        if (!viewModel.hapticEnabled.value) return
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
            // Defensive
        }
    }

    private fun bengaliNum(n: Int): String {
        val bnDigits = arrayOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
        return n.toString().map { if (it.isDigit()) bnDigits[it - '0'] else it }.joinToString("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            soundPool?.release()
        } catch (e: Exception) { /* Defensive */ }
        _binding = null
    }
}
