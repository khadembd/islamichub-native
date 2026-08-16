package com.islamichub.feature.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.data.repository.AyahPair
import com.islamichub.databinding.FragmentQuranReaderBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * QuranReaderFragment — full 114-surah reader.
 *
 * Opens with surah list; tap to view ayah-by-ayah content.
 * Per source quran-module.js: AlQuran Cloud API + audio CDN.
 */
@AndroidEntryPoint
class QuranReaderFragment : Fragment() {
    private var _binding: FragmentQuranReaderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuranReaderViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuranReaderBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.surahList.collectLatest { surahs ->
                if (surahs.isNotEmpty()) {
                    binding.surahHeaderCard.visibility = View.GONE
                    adapter.submitList(surahs.map { s ->
                        ContentCardItem(
                            id = s.number.toString(),
                            title = "${s.number}. ${s.englishName} — ${s.englishNameTranslation}",
                            subtitle = "${s.numberOfAyahs} আয়াত • ${if (s.revelationType == "Meccan") "মক্কী" else "মাদানী"}",
                            arabic = s.name
                        ) {
                            viewModel.openSurah(s.number)
                        }
                    })
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.surahContent.collectLatest { pair ->
                if (pair != null) {
                    binding.surahHeaderCard.visibility = View.VISIBLE
                    binding.surahNameArabic.text = pair.first?.surahs?.name.orEmpty()
                    val meta = pair.first?.surahs
                    binding.surahNameBangla.text = "${meta?.englishNameTranslation.orEmpty()} • ${meta?.numberOfAyahs ?: 0} আয়াত • ${if (meta?.revelationType == "Meccan") "মক্কী" else "মাদানী"}"
                    val ayahs = viewModel.combineAyahs(pair.first, pair.second)
                    adapter.submitList(ayahs.map { ayah ->
                        ContentCardItem(
                            id = "ayah_${ayah.numberInSurah}",
                            title = "আয়াত ${bengaliNum(ayah.numberInSurah)}",
                            arabic = ayah.arabic,
                            body = ayah.bangla,
                            reference = "পৃষ্ঠা ${ayah.page} • জুজ ${ayah.juz}" + if (ayah.sajda) " • সিজদা" else ""
                        )
                    })
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.loading.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun bengaliNum(n: Int): String {
        val bn = arrayOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
        return n.toString().map { if (it.isDigit()) bn[it - '0'] else it }.joinToString("")
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
