package com.islamichub.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.databinding.FragmentHijriDateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * HijriDateFragment — shows Islamic (Hijri) date via Aladhan API.
 * Per source islamic.html: hijri date displayed next to Gregorian date.
 */
@AndroidEntryPoint
class HijriDateFragment : Fragment() {
    private var _binding: FragmentHijriDateBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HijriDateViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHijriDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hijriDate.collectLatest { date ->
                binding.hijriText.text = date ?: "লোড হচ্ছে…"
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
