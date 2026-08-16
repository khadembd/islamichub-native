package com.islamichub.feature.namaz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.databinding.FragmentNamazBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NamazFragment : Fragment() {
    private var _binding: FragmentNamazBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NamazViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNamazBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
            viewModel.items.observe(viewLifecycleOwner) { items ->
                try { adapter.submitList(items) } catch (_: Exception) {}
            }
        } catch (e: Exception) {
            // Defensive: RecyclerView setup must not crash
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
