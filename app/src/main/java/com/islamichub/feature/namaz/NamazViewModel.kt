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
            try {
                val data = assets.namaz()
                val shikkha = assets.namazShikkha()
                val combined = mutableListOf<ContentCardItem>()

                // Add prayer times (Fajr, Dhuhr, Asr, Maghrib, Isha)
                data.prayers.forEach { p ->
                    combined += ContentCardItem(
                        id = "prayer_${p.id}",
                        title = "${p.name} • ${p.time}",
                        subtitle = p.rakat,
                        body = p.description
                    )
                }

                // Add namaz shikkha categories + steps
                shikkha.categories.forEach { cat ->
                    combined += ContentCardItem(
                        id = "cat_${cat.id}",
                        title = cat.name,
                        subtitle = "নামাজ শিক্ষা",
                        body = ""
                    )
                    cat.steps.forEach { step ->
                        combined += ContentCardItem(
                            id = "step_${step.id.ifEmpty { step.title }}",
                            title = step.title,
                            arabic = step.arabic,
                            subtitle = step.transliteration,
                            body = step.bangla.ifEmpty { step.description }
                        )
                    }
                }

                // Add common steps (niyyah etc)
                shikkha.common_steps.forEach { (key, step) ->
                    combined += ContentCardItem(
                        id = "common_${key}",
                        title = step.title.ifEmpty { key },
                        arabic = step.arabic,
                        subtitle = step.transliteration,
                        body = step.bangla.ifEmpty { step.description }
                    )
                }

                _items.value = combined
            } catch (e: Exception) {
                // Defensive: loading failure should show error, not crash
                _items.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "ত্রুটি",
                        body = "নামাজ ডেটা লোড করা যায়নি: ${e.message}"
                    )
                )
            }
        }
    }
}
