package com.islamichub.feature.zikr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.islamichub.data.local.dao.ZikrSessionDao
import com.islamichub.data.local.entities.ZikrSessionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ZikrViewModel — per source zikr-counter.js full feature parity:
 *  - 6 zikr presets with colors + meanings
 *  - Count, sets done, target
 *  - Sound + haptic feedback
 *  - Daily + grand total stats
 *  - Custom zikr support (v2 roadmap)
 */
@HiltViewModel
class ZikrViewModel @Inject constructor(
    private val dao: ZikrSessionDao
) : ViewModel() {

    private val _currentType = MutableStateFlow(ZikrType.SUBHANALLAH)
    val currentType: StateFlow<ZikrType> = _currentType.asStateFlow()

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _setsDone = MutableStateFlow(0)
    val setsDone: StateFlow<Int> = _setsDone.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val dailyTotal: StateFlow<Int> = _currentType
        .flatMapLatest { type -> dao.observeDailyTotal(type.name, LocalDate.now().toString()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val grandTotal: StateFlow<Int> = _currentType
        .flatMapLatest { type -> dao.observeGrandTotal(type.name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setZikrType(type: ZikrType) {
        if (_count.value > 0) saveSession()
        _count.value = 0
        _currentType.value = type
    }

    fun increment() {
        _count.value = _count.value + 1
        if (_count.value >= _currentType.value.target) {
            saveSession()
            _setsDone.value = _setsDone.value + 1
        }
    }

    fun decrement() {
        if (_count.value > 0) _count.value = _count.value - 1
    }

    fun resetSession() {
        if (_count.value > 0) saveSession()
        _count.value = 0
    }

    fun toggleSound() { _soundEnabled.value = !_soundEnabled.value }
    fun toggleHaptic() { _hapticEnabled.value = !_hapticEnabled.value }

    private fun saveSession() {
        viewModelScope.launch {
            try {
                dao.insert(
                    ZikrSessionEntity(
                        zikrType = _currentType.value.name,
                        count = _count.value,
                        target = _currentType.value.target,
                        date = LocalDate.now().toString(),
                        completedAt = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                // Defensive: database errors must not crash zikr counter
            }
        }
    }
}
