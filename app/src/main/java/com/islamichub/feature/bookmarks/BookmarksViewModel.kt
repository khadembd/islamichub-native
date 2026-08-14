package com.islamichub.feature.bookmarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.islamichub.core.ui.ContentCardItem
import com.islamichub.data.local.dao.BookmarkDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val dao: BookmarkDao
) : ViewModel() {

    private val _bookmarks = MutableLiveData<List<ContentCardItem>>()
    val bookmarks: LiveData<List<ContentCardItem>> = _bookmarks

    init {
        // asLiveData converts the Room Flow into a LiveData for observation.
        val source = dao.observeAll().asLiveData()
        source.observeForever { entities ->
            _bookmarks.value = entities.map { e ->
                ContentCardItem(
                    id = "${e.itemType}_${e.itemId}",
                    title = e.title,
                    arabic = e.arabic,
                    subtitle = e.subtitle,
                    body = "",
                    reference = e.category
                )
            }
        }
    }
}
