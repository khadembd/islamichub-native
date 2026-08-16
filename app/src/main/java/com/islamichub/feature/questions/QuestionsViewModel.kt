package com.islamichub.feature.questions

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
class QuestionsViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<ContentCardItem>>()
    val items: LiveData<List<ContentCardItem>> = _items

    init {
        viewModelScope.launch {
            try {
                val questions = assets.questions()
                val answers = assets.answers()
                val flat = mutableListOf<ContentCardItem>()

                // questions.json has category keys: namaz, roza, hajj, etc.
                // Each category has { name, icon, color, questions[] }
                val categories = listOf(
                    "namaz" to questions.namaz,
                    "roza" to questions.roza,
                    "hajj" to questions.hajj,
                    "zakat" to questions.zakat,
                    "quran" to questions.quran,
                    "hadith" to questions.hadith,
                    "iman" to questions.iman,
                    "ilm" to questions.ilm,
                    "taharah" to questions.taharah,
                    "nikah" to questions.nikah,
                    "tijarah" to questions.tijarah,
                    "jihad" to questions.jihad
                )

                categories.forEach { (catId, cat) ->
                    // Get answers for this category
                    val catAnswers = when (catId) {
                        "namaz" -> answers.namaz
                        "roza" -> answers.roza
                        "hajj" -> answers.hajj
                        "zakat" -> answers.zakat
                        "quran" -> answers.quran
                        "hadith" -> answers.hadith
                        "iman" -> answers.iman
                        "ilm" -> answers.ilm
                        "taharah" -> answers.taharah
                        "nikah" -> answers.nikah
                        "tijarah" -> answers.tijarah
                        "jihad" -> answers.jihad
                        else -> emptyList()
                    }

                    cat.questions.forEachIndexed { idx, q ->
                        val ans = catAnswers.getOrNull(idx)
                        flat += ContentCardItem(
                            id = "${catId}_$idx",
                            title = q,
                            body = ans?.answer.orEmpty(),
                            subtitle = cat.name,
                            reference = ans?.reference.orEmpty()
                        )
                    }
                }

                _items.value = flat
            } catch (e: Exception) {
                _items.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "ত্রুটি",
                        body = "প্রশ্নোত্তর লোড করা যায়নি: ${e.message}"
                    )
                )
            }
        }
    }
}
