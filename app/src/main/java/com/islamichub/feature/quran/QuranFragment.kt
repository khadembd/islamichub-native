package com.islamichub.feature.quran

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.databinding.FragmentQuranBinding
import com.islamichub.services.AudioPlaybackService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * QuranFragment — short surahs from namaz-extras-data + audio playback.
 * Per source: quran-module.js + namaz-extras-data.js
 * Shows: Bismillah header + surah list with Arabic + Bangla + audio play button.
 *
 * Premium background: bg_premium_quran (image + gradient overlay)
 */
@AndroidEntryPoint
class QuranFragment : Fragment() {
    private var _binding: FragmentQuranBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuranViewModel by viewModels()
    private val adapter = ContentCardAdapter()
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuranBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeController()
        viewModel.surahs.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.map { item ->
                item.copy(onClick = { playSurahAudio(item.id) })
            })
        }
    }

    private fun initializeController() {
        try {
            val sessionToken = SessionToken(
                requireContext(),
                ComponentName(requireContext(), AudioPlaybackService::class.java)
            )
            controllerFuture = MediaController.Builder(requireContext(), sessionToken).buildAsync()
            controllerFuture?.addListener({
                try { controller = controllerFuture?.get() } catch (_: Exception) {}
            }, ContextCompat.getMainExecutor(requireContext()))
        } catch (e: Exception) {
            // Defensive
        }
    }

    private fun playSurahAudio(surahId: String) {
        try {
            val resName = surahId.lowercase().replace("-", "_").replace(" ", "_")
            val resId = resources.getIdentifier(resName, "raw", requireContext().packageName)
            if (resId == 0) return
            val uri = "android.resource://${requireContext().packageName}/$resId"
            val item = MediaItem.Builder().setUri(uri).build()
            controller?.setMediaItem(item)
            controller?.prepare()
            controller?.play()
        } catch (e: Exception) {
            // Defensive
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try { controllerFuture?.let { MediaController.releaseFuture(it) } } catch (_: Exception) {}
        controller = null
        _binding = null
    }
}
