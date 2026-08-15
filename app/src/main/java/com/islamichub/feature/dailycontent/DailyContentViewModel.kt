package com.islamichub.feature.dailycontent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.model.AsmaulHusna
import com.islamichub.data.model.Dua
import com.islamichub.data.model.Hadith
import com.islamichub.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DailyContentViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _dailyAyah = MutableStateFlow<AsmaulHusna?>(null)
    val dailyAyah: StateFlow<AsmaulHusna?> = _dailyAyah.asStateFlow()

    private val _dailyHadith = MutableStateFlow<Hadith?>(null)
    val dailyHadith: StateFlow<Hadith?> = _dailyHadith.asStateFlow()

    private val _dailyDua = MutableStateFlow<Dua?>(null)
    val dailyDua: StateFlow<Dua?> = _dailyDua.asStateFlow()

    init {
        viewModelScope.launch { loadAll() }
    }

    private suspend fun loadAll() {
        try {
            val asmaulHusna = assets.asmaulHusna()
            val dayOfYear = LocalDate.now().dayOfYear
            _dailyAyah.value = asmaulHusna.getOrNull(dayOfYear % asmaulHusna.size)
        } catch (e: Exception) { _dailyAyah.value = null }

        try {
            val hadiths = assets.hadith().hadiths
            val dayOfYear = LocalDate.now().dayOfYear
            _dailyHadith.value = hadiths.getOrNull(dayOfYear % hadiths.size)
        } catch (e: Exception) { _dailyHadith.value = null }

        try {
            val duas = assets.duas().duas
            val dayOfYear = LocalDate.now().dayOfYear
            _dailyDua.value = duas.getOrNull(dayOfYear % duas.size)
        } catch (e: Exception) { _dailyDua.value = null }
    }
}
