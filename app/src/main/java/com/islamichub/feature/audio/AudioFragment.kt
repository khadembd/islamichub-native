package com.islamichub.feature.audio

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.islamichub.R
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.databinding.FragmentGenericListBinding
import com.islamichub.services.AudioPlaybackService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

/**
 * AudioFragment — plays Namaz MP3 audio using Media3 + MediaSession.
 *
 * Audio list:
 *  - azan2, takbir_tahrimah, tasmiah, taawwuz, dua_al_istiftah, fatiha,
 *    ikhlas, ruku, sajdah, jalsah, qawamah, qunut, tashahud, salat_alan_nabi_darud, salam
 *
 * Background playback + media notification controls via AudioPlaybackService.
 */
@AndroidEntryPoint
class AudioFragment : Fragment() {
    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AudioViewModel by viewModels()
    private val adapter = ContentCardAdapter()
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        binding.recyclerView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.audioList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.map { item ->
                item.copy(onClick = { playAudio(item.id) })
            })
        }
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(requireContext(), ComponentName(requireContext(), AudioPlaybackService::class.java))
        controllerFuture = MediaController.Builder(requireContext(), sessionToken).buildAsync()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                controller = controllerFuture?.await()
            } catch (e: Exception) {
                // Defensive: controller init may fail in some edge cases
            }
        }
    }

    private fun playAudio(rawResName: String) {
        try {
            val controller = this.controller ?: return
            val resId = resources.getIdentifier(rawResName, "raw", requireContext().packageName)
            if (resId == 0) return
            val uri = "android.resource://${requireContext().packageName}/$resId"
            val item = MediaItem.Builder()
                .setUri(uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(rawResName.replace('_', ' '))
                        .setArtist("Islamic Hub")
                        .build()
                )
                .build()
            controller.setMediaItem(item)
            controller.prepare()
            controller.play()
        } catch (e: Exception) {
            // Defensive: playback errors must not crash app
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            controllerFuture?.let { MediaController.releaseFuture(it) }
        } catch (e: Exception) {
            // Defensive
        }
        controller = null
        _binding = null
    }
}
