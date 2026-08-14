package com.islamichub.feature.quran

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.model.QuranEdition
import com.islamichub.data.repository.AyahPair
import com.islamichub.data.repository.QuranRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranReaderViewModel @Inject constructor(
    private val repo: QuranRepository
) : ViewModel() {

    private val _surahList = MutableStateFlow<List<com.islamichub.data.model.QuranSurah>>(emptyList())
    val surahList: StateFlow<List<com.islamichub.data.model.QuranSurah>> = _surahList.asStateFlow()

    private val _surahContent = MutableStateFlow<Pair<QuranEdition?, QuranEdition?>?>(null)
    val surahContent: StateFlow<Pair<QuranEdition?, QuranEdition?>?> = _surahContent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSurahList()
    }

    private fun loadSurahList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _surahList.value = repo.getSurahList()
            } catch (e: Exception) {
                _surahList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun openSurah(surahNumber: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _surahContent.value = repo.getSurahContent(surahNumber)
            } catch (e: Exception) {
                _surahContent.value = null to null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun combineAyahs(arabic: QuranEdition?, bangla: QuranEdition?): List<AyahPair> =
        repo.combineAyahs(arabic, bangla)
}
