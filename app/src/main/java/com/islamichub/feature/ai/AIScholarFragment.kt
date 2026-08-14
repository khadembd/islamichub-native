package com.islamichub.feature.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.islamichub.databinding.FragmentAiScholarBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * AIScholarFragment — Islamic Q&A with AI.
 *
 * Per conversion plan §20: API keys must NOT be hardcoded into APK.
 * Calls go through a backend proxy. The repository uses Retrofit + provider
 * abstraction. For v1, falls back to a local answer lookup from ans-data.json
 * if no network/proxy is configured.
 */
@AndroidEntryPoint
class AIScholarFragment : Fragment() {
    private var _binding: FragmentAiScholarBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AIScholarViewModel by viewModels()
    private val messageAdapter = AIMessageAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiScholarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messagesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.messagesRecycler.adapter = messageAdapter

        binding.sendButton.setOnClickListener {
            val q = binding.questionInput.text?.toString().orEmpty().trim()
            if (q.isNotEmpty()) {
                viewModel.ask(q)
                binding.questionInput.text?.clear()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messages.collectLatest { messages ->
                messageAdapter.submitList(messages)
                if (messages.isNotEmpty()) {
                    binding.messagesRecycler.scrollToPosition(messages.lastIndex)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isThinking.collectLatest { thinking ->
                binding.thinkingProgress.visibility = if (thinking) View.VISIBLE else View.GONE
                binding.sendButton.isEnabled = !thinking
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
