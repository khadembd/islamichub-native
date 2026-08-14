package com.islamichub.feature.applock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.R
import com.islamichub.databinding.FragmentAppLockBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * AppLockFragment — biometric-based app lock screen.
 *
 * পুরোপুরি নেটিভ implementation — browser/Capacitor Biometric নয়।
 * Uses AndroidX BiometricPrompt with PIN/password fallback.
 */
@AndroidEntryPoint
class AppLockFragment : Fragment() {

    private var _binding: FragmentAppLockBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppLockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.unlockButton.setOnClickListener { showBiometricPrompt() }
        // Auto-prompt on resume
        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.app_lock_prompt))
            .setSubtitle(getString(R.string.app_lock_subtitle))
            .setNegativeButtonText(getString(R.string.app_lock_cancel))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Notify app — proceed to main flow
                requireActivity().finish()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                binding.statusText.text = errString
            }
        })

        // Check biometric availability
        val bm = BiometricManager.from(requireContext())
        val canAuth = bm.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        if (canAuth == BiometricManager.BIOMETRIC_SUCCESS) {
            prompt.authenticate(promptInfo)
        } else {
            binding.statusText.text = "বায়োমেট্রিক সেটআপ করা নেই। সেটিংসে গিয়ে ফিঙ্গারপ্রিন্ট/পিন চালু করুন।"
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
