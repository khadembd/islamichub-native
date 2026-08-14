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
            val questions = assets.questions()
            val answers = assets.answers()
            // Per migration plan §14: source globals use `questionData` and
            // `ansData` (not `QUESTION_DATA`). Match each question with its
            // answer from ansData.answers (keyed by category).
            val flat = mutableListOf<ContentCardItem>()
            questions.categories.forEach { (catId, cat) ->
                val catAnswers = answers.answers[catId].orEmpty()
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
        }
    }
}
