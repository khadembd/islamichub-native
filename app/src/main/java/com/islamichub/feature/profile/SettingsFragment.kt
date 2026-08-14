package com.islamichub.feature.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.BuildConfig
import com.islamichub.R
import com.islamichub.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.versionLabel.text = getString(R.string.version_label, BuildConfig.VERSION_NAME)

        setupToggles()
        observeState()
    }

    private fun setupToggles() {
        binding.languageToggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val lang = if (checkedId == R.id.langBn) "bn" else "en"
                viewLifecycleOwner.lifecycleScope.launch { viewModel.setLanguage(lang) }
            }
        }
        binding.themeToggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val mode = when (checkedId) {
                    R.id.themeSystem -> "system"
                    R.id.themeLight  -> "light"
                    R.id.themeDark   -> "dark"
                    else -> "system"
                }
                viewLifecycleOwner.lifecycleScope.launch { viewModel.setThemeMode(mode) }
            }
        }
        binding.madhabToggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                val m = if (checkedId == R.id.madhabHanafi) "hanafi" else "shafi"
                viewLifecycleOwner.lifecycleScope.launch { viewModel.setMadhab(m) }
            }
        }
        binding.appLockSwitch.setOnCheckedChangeListener { _, checked ->
            viewLifecycleOwner.lifecycleScope.launch { viewModel.setAppLock(checked) }
        }
        binding.notificationsSwitch.setOnCheckedChangeListener { _, checked ->
            viewLifecycleOwner.lifecycleScope.launch { viewModel.setNotifications(checked) }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.themeMode.collectLatest { mode ->
                val id = when (mode) {
                    "light" -> R.id.themeLight
                    "dark"  -> R.id.themeDark
                    else    -> R.id.themeSystem
                }
                if (binding.themeToggle.checkedButtonId != id) binding.themeToggle.check(id)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.madhab.collectLatest { m ->
                val id = if (m == "hanafi") R.id.madhabHanafi else R.id.madhabShafi
                if (binding.madhabToggle.checkedButtonId != id) binding.madhabToggle.check(id)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appLanguage.collectLatest { lang ->
                val id = if (lang == "bn") R.id.langBn else R.id.langEn
                if (binding.languageToggle.checkedButtonId != id) binding.languageToggle.check(id)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appLockEnabled.collectLatest { binding.appLockSwitch.isChecked = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notificationsEnabled.collectLatest { binding.notificationsSwitch.isChecked = it }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
