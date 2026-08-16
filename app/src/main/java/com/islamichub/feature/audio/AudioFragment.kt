package com.islamichub.feature.audio

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
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.islamichub.R
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.databinding.FragmentGenericListBinding
import com.islamichub.services.AudioPlaybackService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
            viewModel.audioList.observe(viewLifecycleOwner) { list ->
                try {
                    adapter.submitList(list.map { item ->
                        item.copy(onClick = { playAudio(item.id) })
                    })
                } catch (_: Exception) {}
            }
            initializeController()
        } catch (e: Exception) {
            // Defensive
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
        try { controllerFuture?.let { MediaController.releaseFuture(it) } } catch (_: Exception) {}
        controller = null
        _binding = null
    }
}
