package com.islamichub.feature.namaz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.databinding.FragmentGenericListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NamazFragment : Fragment() {
    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NamazViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.items.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
