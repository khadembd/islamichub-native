package com.islamichub.feature.asmaulhusna

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
class AsmaulHusnaViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _names = MutableLiveData<List<ContentCardItem>>()
    val names: LiveData<List<ContentCardItem>> = _names

    init {
        viewModelScope.launch {
            try {
                _names.value = assets.asmaulHusna().map { n ->
                    ContentCardItem(
                        id = n.id.toString(),
                        title = "${n.id}. ${n.transliteration} — ${n.meaning}",
                        arabic = n.arabic,
                        body = n.explanation,
                        subtitle = "আমল: ${n.amal}"
                    )
                }
            } catch (e: Exception) {
                _names.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "ত্রুটি",
                        body = "আসমাউল হুসনা লোড করা যায়নি: ${e.message}"
                    )
                )
            }
        }
    }
}
