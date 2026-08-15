package com.islamichub.feature.hadith

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
import com.islamichub.databinding.FragmentGenericListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HadithApiFragment : Fragment() {
    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HadithApiViewModel by viewModels()
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.books.collectLatest { books ->
                    try {
                        if (books.isNotEmpty()) {
                            adapter.submitList(books.map { (id, name) ->
                                ContentCardItem(
                                    id = id,
                                    title = name,
                                    subtitle = "হাদিস সংগ্রহ",
                                    body = ""
                                ) { viewModel.openBook(id) }
                            })
                        }
                    } catch (_: Exception) {}
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.hadiths.collectLatest { hadiths ->
                    try {
                        if (hadiths.isNotEmpty()) {
                            adapter.submitList(hadiths.take(50).map { h ->
                                val grade = h.grades.firstOrNull()?.grade.orEmpty()
                                ContentCardItem(
                                    id = "h_${h.hadithnumber}",
                                    title = "হাদিস নং ${bengaliNum(h.hadithnumber)}",
                                    body = h.text,
                                    reference = if (grade.isNotBlank()) "গ্রেড: $grade" else "",
                                    subtitle = "সনদ নং ${h.hadithnumber}"
                                )
                            })
                        }
                    } catch (_: Exception) {}
                }
            }
        } catch (e: Exception) {
            // Defensive
        }
    }

    private fun bengaliNum(n: Int): String {
        val bn = arrayOf('০','১','২','৩','৪','৫','৬','৭','৮','৯')
        return n.toString().map { if (it.isDigit()) bn[it - '0'] else it }.joinToString("")
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
