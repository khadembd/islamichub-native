package com.islamichub.feature.questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.databinding.FragmentQuestionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionsFragment : Fragment() {
    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuestionsViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
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
            // Defensive
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
