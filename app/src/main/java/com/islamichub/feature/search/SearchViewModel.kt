package com.islamichub.feature.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.data.repository.AssetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SearchViewModel — searches across Quran, Hadith, Dua, Asmaul Husna, etc.
 *
 * Per migration plan §14: fixes known search bugs:
 *  - Asmaul Husna search uses `transliteration` (not `bangla`)
 *  - Extended Hadith search uses `items` (not `hadiths`)
 *  - Stories search treats prophets/khalifas as arrays
 *  - Misconceptions search uses both `items` and `categories` fields
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val assets: AssetRepository
) : ViewModel() {

    private val _results = MutableLiveData<List<ContentCardItem>>()
    val results: LiveData<List<ContentCardItem>> = _results

    private var searchJob: Job? = null

    fun query(q: String) {
        searchJob?.cancel()
        if (q.isBlank()) {
            _results.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(180)  // debounce
            val needle = q.lowercase()
            val out = mutableListOf<ContentCardItem>()

            // 1. Asmaul Husna — search transliteration / meaning / explanation
            assets.asmaulHusna().forEach { n ->
                if (n.transliteration.contains(q, true) ||
                    n.meaning.contains(q, true) ||
                    n.explanation.contains(q, true)
                ) {
                    out += ContentCardItem(
                        id = "ah_${n.id}",
                        title = "${n.transliteration} — ${n.meaning}",
                        arabic = n.arabic,
                        body = n.explanation,
                        subtitle = "আসমাউল হুসনা"
                    )
                }
            }

            // 2. Hadith (primary + extended)
            val primary = assets.hadith()
            val extended = assets.extendedHadith()
            (primary.hadiths + extended.items).forEach { h ->
                if (h.title.contains(q, true) || h.bangla.contains(q, true)) {
                    out += ContentCardItem(
                        id = "hadith_${h.id}",
                        title = h.title,
                        arabic = h.arabic,
                        body = h.bangla,
                        reference = h.reference,
                        subtitle = "হাদিস"
                    )
                }
            }

            // 3. Dua
            assets.duas().duas.forEach { d ->
                if (d.title.contains(q, true) || d.bangla.contains(q, true)) {
                    out += ContentCardItem(
                        id = "dua_${d.id}",
                        title = d.title,
                        arabic = d.arabic,
                        subtitle = "দোয়া • ${d.transliteration}",
                        body = d.bangla,
                        reference = d.ref
                    )
                }
            }

            // 4. Stories (prophets + khalifas as arrays)
            val stories = assets.stories()
            (stories.prophets + stories.khalifas + stories.meraj + stories.sirat).forEachIndexed { idx, s ->
                if (s.title.contains(q, true) || s.description.contains(q, true)) {
                    out += ContentCardItem(
                        id = "story_$idx",
                        title = s.title,
                        arabic = s.arabic,
                        body = s.description,
                        subtitle = "ইসলামিক গল্প"
                    )
                }
            }

            // 5. Misconceptions
            val misc = assets.misconceptions()
            (misc.items + misc.categories.values.flatten()).distinctBy { it.id }.forEachIndexed { idx, m ->
                if (m.title.contains(q, true) || m.question.contains(q, true) || m.answer.contains(q, true)) {
                    out += ContentCardItem(
                        id = "misc_$idx",
                        title = m.title.ifEmpty { m.question },
                        body = m.answer,
                        reference = m.reference,
                        subtitle = "ভুল ধারণা"
                    )
                }
            }

            _results.value = out.take(50)
        }
    }
}
