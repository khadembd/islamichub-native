package com.islamichub.feature.misconceptions

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
class MisconceptionsViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<ContentCardItem>>()
    val items: LiveData<List<ContentCardItem>> = _items

    init {
        viewModelScope.launch {
            try {
                val data = assets.misconceptions()
                // misconceptions.json has categories[] with misconceptions[] inside each
                val flat = mutableListOf<ContentCardItem>()
                data.categories.forEach { cat ->
                    cat.misconceptions.forEach { m ->
                        flat += ContentCardItem(
                            id = m.id.ifEmpty { "misc_${cat.id}_${flat.size}" },
                            title = m.title.ifEmpty { m.question },
                            body = m.answer,
                            reference = m.reference,
                            subtitle = cat.name.ifEmpty { cat.title }
                        )
                    }
                }
                _items.value = flat
            } catch (e: Exception) {
                _items.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "ত্রুটি",
                        body = "ভুল ধারণা লোড করা যায়নি: ${e.message}"
                    )
                )
            }
        }
    }
}
