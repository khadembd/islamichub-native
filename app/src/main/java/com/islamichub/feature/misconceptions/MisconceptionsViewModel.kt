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
            val data = assets.misconceptions()
            // Per migration plan §14: misconceptions data has BOTH a `categories`
            // map and an `items` array — we flatten both for v1.
            val flat = data.items.toMutableList()
            data.categories.values.forEach { flat += it }
            _items.value = flat.distinctBy { it.id.ifEmpty { it.title } }.mapIndexed { idx, m ->
                ContentCardItem(
                    id = m.id.ifEmpty { "misc_$idx" },
                    title = m.title.ifEmpty { m.question },
                    body = m.answer,
                    reference = m.reference,
                    subtitle = m.category
                )
            }
        }
    }
}
