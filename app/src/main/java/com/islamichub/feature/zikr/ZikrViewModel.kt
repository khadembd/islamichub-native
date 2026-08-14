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

enum class ZikrType(val displayName: String, val arabic: String, val target: Int) {
    SUBHANALLAH("সুবহানাল্লাহ", "سُبْحَانَ اللَّهِ", 33),
    ALHAMDULILLAH("আলহামদুলিল্লাহ", "الْحَمْدُ لِلَّهِ", 33),
    ALLAHU_AKBAR("আল্লাহু আকবার", "اللَّهُ أَكْبَرُ", 34),
    LA_ILAHA("লা ইলাহা ইল্লাল্লাহ", "لَا إِلَٰهَ إِلَّا اللَّهُ", 100),
    ASTAGHFIRULLAH("আস্তাগফিরুল্লাহ", "أَسْتَغْفِرُ اللَّهَ", 100)
}

@HiltViewModel
class ZikrViewModel @Inject constructor(
    private val dao: ZikrSessionDao
) : ViewModel() {

    private val _currentType = MutableStateFlow(ZikrType.SUBHANALLAH)
    val currentType: StateFlow<ZikrType> = _currentType.asStateFlow()

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    // Daily + grand totals observe Room flow, switching when type changes
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
        }
    }

    fun resetSession() {
        if (_count.value > 0) saveSession()
        _count.value = 0
    }

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
                // Defensive: database insert should never crash zikr counter
            }
        }
    }
}
