package com.islamichub.feature.quran

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
class QuranViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _surahs = MutableLiveData<List<ContentCardItem>>()
    val surahs: LiveData<List<ContentCardItem>> = _surahs

    init {
        viewModelScope.launch {
            try {
                val extras = assets.namazExtras()
                _surahs.value = extras.namazSurahs.map { s ->
                    ContentCardItem(
                        id = s.id.toString(),
                        title = s.name,
                        arabic = s.arabic,
                        subtitle = s.transliteration,
                        body = s.bangla
                    )
                }
            } catch (e: Exception) {
                _surahs.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "ত্রুটি",
                        body = "কুরআন ডেটা লোড করা যায়নি: ${e.message}"
                    )
                )
            }
        }
    }
}
