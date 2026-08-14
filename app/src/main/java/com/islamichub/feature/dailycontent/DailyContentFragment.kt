package com.islamichub.feature.dailycontent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.databinding.FragmentDailyContentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * DailyContentFragment — today's ayah + hadith + dua.
 *
 * Per conversion plan §22: WorkManager schedules daily refresh of these cards.
 * The fragment reads from AssetRepository (deterministic day-of-year picker).
 */
@AndroidEntryPoint
class DailyContentFragment : Fragment() {
    private var _binding: FragmentDailyContentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DailyContentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDailyContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyAyah.collectLatest { ayah ->
                binding.ayahArabic.text = ayah?.arabic.orEmpty()
                binding.ayahTranslation.text = ayah?.meaning.orEmpty()
                binding.ayahReference.text = ayah?.transliteration.orEmpty()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyHadith.collectLatest { hadith ->
                binding.hadithArabic.text = hadith?.arabic.orEmpty()
                binding.hadithTranslation.text = hadith?.bangla.orEmpty()
                binding.hadithReference.text = hadith?.reference.orEmpty()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyDua.collectLatest { dua ->
                binding.duaArabic.text = dua?.arabic.orEmpty()
                binding.duaTranslation.text = dua?.bangla.orEmpty()
                binding.duaReference.text = dua?.ref.orEmpty()
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
