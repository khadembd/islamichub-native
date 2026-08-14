package com.islamichub.feature.hadith

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.model.HadithItem
import com.islamichub.data.repository.HadithApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HadithApiViewModel @Inject constructor(
    private val repo: HadithApiRepository
) : ViewModel() {

    val books: StateFlow<Map<String, String>> = MutableStateFlow(HadithApiRepository.BOOK_IDS).asStateFlow()

    private val _hadiths = MutableStateFlow<List<HadithItem>>(emptyList())
    val hadiths: StateFlow<List<HadithItem>> = _hadiths.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun openBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _hadiths.value = repo.getBookHadiths(bookId)
            } catch (e: Exception) {
                _hadiths.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String, bookId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _hadiths.value = repo.searchHadiths(query, bookId)
            } catch (e: Exception) {
                _hadiths.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
