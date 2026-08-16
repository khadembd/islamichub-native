package com.islamichub.feature.hadith

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _hadiths = MutableLiveData<List<ContentCardItem>>()
    val hadiths: LiveData<List<ContentCardItem>> = _hadiths

    init {
        viewModelScope.launch {
            try {
                val primary = assets.hadith()
                val extended = assets.extendedHadith()
                // Primary hadiths
                val combined = primary.hadiths.map { h ->
                    ContentCardItem(
                        id = h.id.toString(),
                        title = h.title,
                        arabic = h.arabic,
                        subtitle = h.narrator,
                        body = h.bangla,
                        reference = h.reference
                    )
                }.toMutableList()
                // Extended hadith topics (hadith_topics, NOT items)
                extended.hadith_topics.forEach { topic ->
                    combined += ContentCardItem(
                        id = "ext_${topic.id}",
                        title = topic.name,
                        arabic = topic.arabic,
                        body = topic.description,
                        subtitle = "${topic.category} • ${topic.subcategory}"
                    )
                }
                _hadiths.value = combined
            } catch (e: Exception) {
                _hadiths.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "ত্রুটি",
                        body = "হাদিস লোড করা যায়নি: ${e.message}"
                    )
                )
            }
        }
    }
}
