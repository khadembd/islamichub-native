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
 * Per migration plan §14: fixes known search bugs.
 * Updated for actual JSON structures.
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
            try {
                delay(180)  // debounce
                val out = mutableListOf<ContentCardItem>()

                // 1. Asmaul Husna
                try {
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
                } catch (_: Exception) {}

                // 2. Hadith (primary + extended topics)
                try {
                    val primary = assets.hadith()
                    primary.hadiths.forEach { h ->
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
                    val extended = assets.extendedHadith()
                    extended.hadith_topics.forEach { t ->
                        if (t.name.contains(q, true) || t.description.contains(q, true)) {
                            out += ContentCardItem(
                                id = "ext_hadith_${t.id}",
                                title = t.name,
                                arabic = t.arabic,
                                body = t.description,
                                subtitle = "হাদিস বিষয়"
                            )
                        }
                    }
                } catch (_: Exception) {}

                // 3. Dua
                try {
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
                } catch (_: Exception) {}

                // 4. Stories
                try {
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
                } catch (_: Exception) {}

                // 5. Misconceptions — categories[].misconceptions[]
                try {
                    val misc = assets.misconceptions()
                    misc.categories.forEach { cat ->
                        cat.misconceptions.forEach { m ->
                            if (m.title.contains(q, true) || m.question.contains(q, true) || m.answer.contains(q, true)) {
                                out += ContentCardItem(
                                    id = "misc_${m.id}",
                                    title = m.title.ifEmpty { m.question },
                                    body = m.answer,
                                    reference = m.reference,
                                    subtitle = "ভুল ধারণা • ${cat.name}"
                                )
                            }
                        }
                    }
                } catch (_: Exception) {}

                _results.value = out.take(50)
            } catch (e: Exception) {
                _results.value = listOf(
                    ContentCardItem(
                        id = "error",
                        title = "সার্চ ত্রুটি",
                        body = e.message.orEmpty()
                    )
                )
            }
        }
    }
}
