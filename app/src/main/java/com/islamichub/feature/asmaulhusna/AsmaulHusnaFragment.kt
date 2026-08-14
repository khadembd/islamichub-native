package com.islamichub.feature.asmaulhusna

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.databinding.FragmentAsmaulHusnaBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AsmaulHusnaFragment : Fragment() {
    private var _binding: FragmentAsmaulHusnaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AsmaulHusnaViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAsmaulHusnaBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.names.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
