package com.islamichub.feature.namaz

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
class NamazViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<ContentCardItem>>()
    val items: LiveData<List<ContentCardItem>> = _items

    init {
        viewModelScope.launch {
            // Per migration plan §15: namaz-data.js and namazshikkha-data.js both
            // define `window.namazData` but with different shapes. We expose them
            // as a single merged list of distinct cards (categories + prayers +
            // learning steps) but the underlying models stay separate.
            val data = assets.namaz()
            val shikkha = assets.namazShikkha()
            val combined = mutableListOf<ContentCardItem>()

            data.prayers.forEach { p ->
                combined += ContentCardItem(
                    id = "prayer_${p.id}",
                    title = "${p.name} • ${p.time}",
                    subtitle = p.rakat,
                    body = p.description
                )
            }
            shikkha.steps.forEach { step ->
                combined += ContentCardItem(
                    id = "step_${step.id.ifEmpty { step.title }}",
                    title = step.title,
                    arabic = step.arabic,
                    subtitle = step.transliteration,
                    body = step.bangla.ifEmpty { step.description }
                )
            }
            _items.value = combined
        }
    }
}
