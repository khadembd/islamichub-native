package com.islamichub.feature.hadith

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.databinding.FragmentHadithBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HadithFragment : Fragment() {
    private var _binding: FragmentHadithBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HadithViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHadithBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.hadiths.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
