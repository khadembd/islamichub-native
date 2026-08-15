package com.islamichub.feature.dua

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
class DuaViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _duas = MutableLiveData<List<ContentCardItem>>()
    val duas: LiveData<List<ContentCardItem>> = _duas

    init {
        viewModelScope.launch {
            try {
                val collection = assets.duas()
                _duas.value = collection.duas.map { dua ->
                    ContentCardItem(
                        id = dua.id,
                        title = dua.title,
                        arabic = dua.arabic,
                        subtitle = dua.transliteration,
                        body = dua.bangla,
                        reference = dua.ref
                    )
                }
            } catch (e: Exception) {
                _duas.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "ত্রুটি",
                        body = "দোয়া লোড করা যায়নি: ${e.message}"
                    )
                )
            }
        }
    }
}
