package com.islamichub.feature.qibla

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.islamichub.data.preferences.AppPreferences
import com.islamichub.services.PrayerTimeCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    private val prefs: AppPreferences
) : ViewModel() {

    private val _bearing = MutableLiveData<Float>()
    val bearing: LiveData<Float> = _bearing

    private val _bearingLabel = MutableLiveData<String>()
    val bearingLabel: LiveData<String> = _bearingLabel

    private val _distanceLabel = MutableLiveData<String>()
    val distanceLabel: LiveData<String> = _distanceLabel

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private var userLat: Double = 23.8103
    private var userLon: Double = 90.4125
    private var qiblaBearing: Float = 0f
    private var deviceAzimuth: Float = 0f

    init {
        viewModelScope.launch {
            userLat = prefs.latitude.first()
            userLon = prefs.longitude.first()
            qiblaBearing = PrayerTimeCalculator.qiblaBearing(userLat, userLon).toFloat()
            val distance = PrayerTimeCalculator.distanceToKaabaKm(userLat, userLon)
            _bearingLabel.value = "কিবলা দিক: ${qiblaBearing.toInt()}°"
            _distanceLabel.value = "মক্কার দূরত্ব: %,d কিমি".format(distance.toInt())
            _status.value = "কম্পাস প্রস্তুত"
        }
    }

    fun onDeviceAzimuth(azimuth: Float) {
        deviceAzimuth = azimuth
        // Bearing from device north to qibla
        val relative = ((qiblaBearing - azimuth + 360) % 360).toFloat()
        _bearing.value = relative
    }

    @SuppressLint("MissingPermission")
    fun refreshLocation() {
        viewModelScope.launch {
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)
                client.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        userLat = loc.latitude
                        userLon = loc.longitude
                        qiblaBearing = PrayerTimeCalculator.qiblaBearing(userLat, userLon).toFloat()
                        val distance = PrayerTimeCalculator.distanceToKaabaKm(userLat, userLon)
                        _bearingLabel.value = "কিবলা দিক: ${qiblaBearing.toInt()}°"
                        _distanceLabel.value = "মক্কার দূরত্ব: %,d কিমি".format(distance.toInt())
                        viewModelScope.launch {
                            prefs.setLocation("GPS: %.2f, %.2f".format(userLat, userLon), userLat, userLon)
                        }
                    }
                }
            } catch (e: SecurityException) {
                _status.value = "লোকেশন অনুমতি প্রয়োজন"
            }
        }
    }
}
