package com.islamichub.feature.zikr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.R
import com.islamichub.databinding.FragmentZikrBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@AndroidEntryPoint
class ZikrFragment : Fragment() {
    private var _binding: FragmentZikrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ZikrViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentZikrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tapTarget.setOnClickListener { viewModel.increment() }
        binding.resetButton.setOnClickListener { viewModel.resetSession() }
        binding.changeZikrButton.setOnClickListener { viewModel.cycleZikrType() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentType.collectLatest { type ->
                binding.zikrTypeLabel.text = type.displayName
                binding.zikrArabic.text = type.arabic
                binding.targetValue.text = type.target.toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.count.collectLatest { binding.zikrCount.text = it.toString() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dailyTotal.collectLatest { binding.dailyTotal.text = it.toString() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.grandTotal.collectLatest { binding.grandTotal.text = it.toString() }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
