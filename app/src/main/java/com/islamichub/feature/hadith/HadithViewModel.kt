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
            val primary = assets.hadith()
            val extended = assets.extendedHadith()
            // Per migration plan §14: extended hadith uses `items` (not `hadiths`).
            val combined = (primary.hadiths + extended.items).map { h ->
                ContentCardItem(
                    id = h.id.toString(),
                    title = h.title,
                    arabic = h.arabic,
                    subtitle = h.narrator,
                    body = h.bangla,
                    reference = h.reference
                )
            }
            _hadiths.value = combined
        }
    }
}
