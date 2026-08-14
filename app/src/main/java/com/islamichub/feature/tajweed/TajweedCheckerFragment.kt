package com.islamichub.feature.tajweed

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.islamichub.R
import com.islamichub.databinding.FragmentTajweedBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * TajweedCheckerFragment — records user's recitation for tajweed analysis.
 *
 * পুরোপুরি নেটিভ — browser getUserMedia() নয়।
 * Per conversion plan §19: RECORD_AUDIO permission + native audio capture
 * + AI/backend analysis (v2 roadmap).
 *
 * v1: record + playback + visual feedback. AI analysis on v2.
 */
@AndroidEntryPoint
class TajweedCheckerFragment : Fragment() {

    private var _binding: FragmentTajweedBinding? = null
    private val binding get() = _binding!!

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false
    private var mediaPlayer: MediaPlayer? = null

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) binding.statusText.text = "মাইক্রোফোন অনুমতি প্রয়োজন"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTajweedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recordButton.setOnClickListener {
            if (!hasMicPermission()) {
                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                return@setOnClickListener
            }
            if (isRecording) stopRecording() else startRecording()
        }
        binding.playbackButton.setOnClickListener { playRecording() }

        // Display reference ayah
        binding.referenceAyah.text = "قُلْ هُوَ اللَّهُ أَحَدٌ"
        binding.referenceText.text = "সূরা আল-ইখলাস, আয়াত ১"
    }

    private fun hasMicPermission() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED

    private fun startRecording() {
        try {
            outputFile = File(requireContext().cacheDir, "tajweed_${System.currentTimeMillis()}.3gp")
            @Suppress("DEPRECATION")
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            binding.recordButton.text = getString(R.string.tajweed_stop)
            binding.recordButton.setBackgroundColor(requireContext().getColor(R.color.status_error))
            binding.statusText.text = getString(R.string.tajweed_listening)
            binding.playbackButton.isEnabled = false
        } catch (e: Exception) {
            binding.statusText.text = "রেকর্ড করা যায়নি: ${e.message}"
        }
    }

    private fun stopRecording() {
        try {
            recorder?.apply { stop(); release() }
            recorder = null
            isRecording = false
            binding.recordButton.text = getString(R.string.tajweed_record)
            binding.recordButton.setBackgroundColor(requireContext().getColor(R.color.primary))
            binding.statusText.text = "রেকর্ড সম্পন্ন। প্লে করুন বা পুনরায় রেকর্ড করুন।"
            binding.playbackButton.isEnabled = true
        } catch (e: Exception) {
            binding.statusText.text = "রেকর্ড থামানো যায়নি"
        }
    }

    private fun playRecording() {
        try {
            val file = outputFile ?: return
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener { it.release() }
                start()
            }
            binding.statusText.text = "প্লেব্যাক চলছে…"
        } catch (e: Exception) {
            binding.statusText.text = "প্লে করা যায়নি"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            recorder?.release()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Defensive
        }
        _binding = null
    }
}
