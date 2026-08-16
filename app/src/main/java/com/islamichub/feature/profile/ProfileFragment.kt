package com.islamichub.feature.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.R
import com.islamichub.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ProfileFragment — per source profile-service.js
 * Shows user profile, stats, and sync status.
 * Per source: Firebase Auth + Firestore + custom Jamaat + user stats.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.userName.collectLatest { name ->
                    binding.profileName.text = if (name.isBlank()) "Guest" else name
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.userEmail.collectLatest { email ->
                    binding.profileEmail.text = if (email.isBlank()) "লগইন করা নেই" else email
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.totalBookmarks.collectLatest { count ->
                    binding.bookmarkCount.text = bengaliNum(count)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.totalZikr.collectLatest { count ->
                    binding.zikrCount.text = bengaliNum(count)
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.salahStreak.collectLatest { streak ->
                    binding.streakCount.text = bengaliNum(streak)
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
