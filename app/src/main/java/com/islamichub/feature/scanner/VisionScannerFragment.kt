package com.islamichub.feature.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.islamichub.databinding.FragmentScannerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * VisionScannerFragment — CameraX-based scanner for Quranic verses / Islamic text.
 *
 * পুরোপুরি নেটিভ — browser getUserMedia/Capacitor Camera নয়।
 * Per conversion plan §18: CameraX Preview → Image Capture → Result UI.
 *
 * v1: live preview + capture button. Recognition pipeline (OCR / AI) on v2 roadmap.
 */
@AndroidEntryPoint
class VisionScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera()
        else binding.statusText.text = "ক্যামেরা অনুমতি প্রয়োজন"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.captureButton.setOnClickListener { takePhoto() }

        if (hasCameraPermission()) startCamera()
        else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun hasCameraPermission() =
        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
                    binding.statusText.text = "ক্যামেরা প্রস্তুত"
                } catch (e: Exception) {
                    binding.statusText.text = "ক্যামেরা চালু করা যায়নি"
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        } catch (e: Exception) {
            binding.statusText.text = "ক্যামেরা ত্রুটি: ${e.message}"
        }
    }

    private fun takePhoto() {
        val imageCapture = this.imageCapture ?: return
        try {
            val photoFile = File(requireContext().cacheDir, "scan_${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        activity?.runOnUiThread {
                            binding.statusText.text = "ছবি সংরক্ষিত হয়েছে। বিশ্লেষণ চলছে…"
                            // v2: send to OCR/AI backend
                        }
                    }
                    override fun onError(exc: ImageCaptureException) {
                        activity?.runOnUiThread {
                            binding.statusText.text = "ছবি তোলা যায়নি: ${exc.message}"
                        }
                    }
                }
            )
        } catch (e: Exception) {
            binding.statusText.text = "ত্রুটি: ${e.message}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            cameraExecutor.shutdown()
        } catch (e: Exception) {
            // Defensive
        }
        _binding = null
    }
}
