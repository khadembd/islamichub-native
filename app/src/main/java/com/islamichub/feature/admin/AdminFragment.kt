package com.islamichub.feature.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.islamichub.core.ui.ContentCardAdapter
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.databinding.FragmentGenericListBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class AdminFragment : Fragment() {
    private var _binding: FragmentGenericListBinding? = null
    private val binding get() = _binding!!
    private val adapter = ContentCardAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGenericListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
            loadDiagnostics()
        } catch (e: Exception) {
            // Defensive
        }
    }

    private fun loadDiagnostics() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val db = Firebase.firestore
                val items = mutableListOf<ContentCardItem>()

                val collections = listOf("users", "bookmarks", "salah_tracker", "duas", "hadiths", "quran")
                for (name in collections) {
                    try {
                        val snap = db.collection(name).limit(1).get().await()
                        items += ContentCardItem(
                            id = "col_$name",
                            title = "কালেকশন: $name",
                            subtitle = if (snap.isEmpty) "খালি বা নেই" else "ডকুমেন্ট আছে (${snap.size()}+)",
                            body = "Firebase Firestore • project: islamic-9f925"
                        )
                    } catch (e: Exception) {
                        items += ContentCardItem(
                            id = "col_$name",
                            title = "কালেকশন: $name",
                            subtitle = "অ্যাক্সেস ত্রুটি",
                            body = e.message.orEmpty()
                        )
                    }
                }

                items += ContentCardItem(
                    id = "app_info",
                    title = "অ্যাপ তথ্য",
                    subtitle = "com.islamic.islam • v1.0.0",
                    body = "Firebase project: islamic-9f925\nStorage bucket: islamic-9f925.firebasestorage.app"
                )

                adapter.submitList(items)
            } catch (e: Exception) {
                adapter.submitList(listOf(
                    ContentCardItem(id = "error", title = "ত্রুটি", body = e.message.orEmpty())
                ))
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
