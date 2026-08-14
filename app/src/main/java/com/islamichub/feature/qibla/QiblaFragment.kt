package com.islamichub.feature.qibla

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.islamichub.R
import com.islamichub.databinding.FragmentQiblaBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlin.math.abs

/**
 * QiblaFragment — native compass using SensorManager (TYPE_ROTATION_VECTOR
 * with TYPE_ACCELEROMETER + TYPE_MAGNETIC_FIELD fallback).
 *
 * পুরোপুরি নেটিভ implementation — browser sensor API নয়।
 */
@AndroidEntryPoint
class QiblaFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentQiblaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QiblaViewModel by viewModels()

    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null
    private var accelSensor: Sensor? = null
    private var magnetSensor: Sensor? = null

    private val gravity = FloatArray(3)
    private val geomag = FloatArray(3)
    private var lastBearing = 0f

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.any { it }
        if (granted) viewModel.refreshLocation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQiblaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        viewModel.bearing.observe(viewLifecycleOwner) { bearing -> updateCompass(bearing) }
        viewModel.distanceLabel.observe(viewLifecycleOwner) { binding.qiblaDistanceLabel.text = it }
        viewModel.bearingLabel.observe(viewLifecycleOwner) { binding.qiblaBearingLabel.text = it }
        viewModel.status.observe(viewLifecycleOwner) { binding.compassStatus.text = it }

        if (hasLocationPermission()) {
            viewModel.refreshLocation()
        } else {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        binding.calibrateButton.setOnClickListener {
            binding.compassStatus.text = "ফোন ৮ আকৃতিতে ঘোরান ক্যালিব্রেশনের জন্য"
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    override fun onResume() {
        super.onResume()
        rotationSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        if (rotationSensor == null) {
            accelSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            magnetSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rMat = FloatArray(9)
            val orientation = FloatArray(3)
            SensorManager.getRotationMatrixFromVector(rMat, event.values)
            SensorManager.getOrientation(rMat, orientation)
            val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            viewModel.onDeviceAzimuth(azimuth)
        } else {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, gravity, 0, 3)
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, geomag, 0, 3)
            }
            val rMat = FloatArray(9)
            val orientation = FloatArray(3)
            if (SensorManager.getRotationMatrix(rMat, null, gravity, geomag)) {
                SensorManager.getOrientation(rMat, orientation)
                val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                viewModel.onDeviceAzimuth(azimuth)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            binding.compassStatus.text = when (accuracy) {
                SensorManager.SENSOR_STATUS_UNRELIABLE -> "কম্পাস ক্যালিব্রেশন দরকার — ফোন ৮ আকৃতিতে ঘোরান"
                SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "কম্পাস নিম্নমান — ক্যালিব্রেট করুন"
                else -> "কম্পাস প্রস্তুত"
            }
        }
    }

    private fun updateCompass(bearing: Float) {
        // Rotate the compass ring opposite to bearing so it always points to True North
        val rotation = RotateAnimation(
            lastBearing, -bearing,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            fillAfter = true
        }
        binding.compassRing.startAnimation(rotation)
        lastBearing = -bearing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
