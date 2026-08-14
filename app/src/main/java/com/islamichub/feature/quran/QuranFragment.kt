package com.islamichub.feature.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.databinding.FragmentGenericListBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * QuranFragment — lists 114 surahs (orNamazExtras short surahs bundled for Salah recitation).
 *
 * For v1 we surface the short surahs from namaz-extras-data.js (these include
 * Arabic + transliteration + Bangla translation + audio reference). A full
 * 114-surah Quran reader is on the v2 roadmap and will pull from a dedicated
 * bundled quran.json asset.
 */
@AndroidEntryPoint
class QuranFragment : Fragment() {
    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuranViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.surahs.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
