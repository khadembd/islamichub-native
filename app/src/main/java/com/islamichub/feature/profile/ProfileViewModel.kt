package com.islamichub.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.local.dao.BookmarkDao
import com.islamichub.data.local.dao.ZikrSessionDao
import com.islamichub.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ProfileViewModel — per source profile-service.js
 * Manages user profile data + stats (bookmarks, zikr, salah streak).
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: AppPreferences,
    private val bookmarkDao: BookmarkDao,
    private val zikrDao: ZikrSessionDao
) : ViewModel() {

    val userName: StateFlow<String> = prefs.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val userEmail: StateFlow<String> = prefs.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val totalBookmarks: StateFlow<Int> = bookmarkDao.observeAll()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalZikr: StateFlow<Int> = zikrDao.observeRecent()
        .map { sessions -> sessions.sumOf { it.count } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val salahStreak: StateFlow<Int> = prefs.madhab
        .map { 0 } // TODO: compute from SalahTrackerDao
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            prefs.setUserName(name)
            prefs.setUserEmail(email)
        }
    }
}
