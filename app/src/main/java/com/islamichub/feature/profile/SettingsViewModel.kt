package com.islamichub.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: AppPreferences
) : ViewModel() {

    val themeMode: StateFlow<String> = prefs.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    val madhab: StateFlow<String> = prefs.madhab
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "shafi")
    val appLanguage: StateFlow<String> = prefs.appLanguage
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "bn")
    val appLockEnabled: StateFlow<Boolean> = prefs.appLockEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val notificationsEnabled: StateFlow<Boolean> = prefs.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setThemeMode(mode: String) = viewModelScope.launch { prefs.setThemeMode(mode) }
    fun setMadhab(m: String) = viewModelScope.launch { prefs.setMadhab(m) }
    fun setLanguage(lang: String) = viewModelScope.launch { prefs.setAppLanguage(lang) }
    fun setAppLock(enabled: Boolean) = viewModelScope.launch { prefs.setAppLockEnabled(enabled) }
    fun setNotifications(enabled: Boolean) = viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
}
