package com.islamichub.feature.stories

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
class StoriesViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _stories = MutableLiveData<List<ContentCardItem>>()
    val stories: LiveData<List<ContentCardItem>> = _stories

    init {
        viewModelScope.launch {
            val data = assets.stories()
            // Per migration plan §14: prophets/khalifas are arrays, AND meraj
            // is included in story categories.
            val combined = data.prophets + data.khalifas + data.meraj + data.sirat
            _stories.value = combined.mapIndexed { idx, s ->
                ContentCardItem(
                    id = s.id.ifEmpty { "story_$idx" },
                    title = s.title,
                    arabic = s.arabic,
                    subtitle = s.period,
                    body = s.description.ifEmpty { s.bangla }
                )
            }
        }
    }
}
