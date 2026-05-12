package com.vcam.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Glue between CameraX (Preview + ImageCapture) and the GL renderer. The renderer
 * owns a SurfaceTexture; we wrap it in a Surface and hand it back to CameraX as
 * the Preview's output target. ImageCapture writes JPEGs into MediaStore.
 */
class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
) {
    sealed interface ExposureCompensationState {
        data object Unsupported : ExposureCompensationState
        data class Supported(
            val minIndex: Int,
            val maxIndex: Int,
            val currentIndex: Int,
        ) : ExposureCompensationState
    }

    internal companion object {
        const val imageCaptureJpegQuality = 100
        private const val tag = "CameraController"

        fun resolutionSelectorFor(targetAspectRatio: Int?): ResolutionSelector? = targetAspectRatio?.let {
            ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy(it, AspectRatioStrategy.FALLBACK_RULE_AUTO))
                .build()
        }

        fun canFocusAndMeterAt(x: Float, y: Float, width: Int, height: Int): Boolean =
            width > 0 && height > 0 && x >= 0f && y >= 0f && x < width && y < height

        fun clampExposureIndex(index: Int, min: Int, max: Int): Int = index.coerceIn(min, max)
    }

    private val executor = Executors.newSingleThreadExecutor()
    private val mainExecutor: Executor = ContextCompat.getMainExecutor(context)
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var providerFuture: ListenableFuture<ProcessCameraProvider>? = null

    fun bindToSurfaceTexture(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int,
        flashMode: Int = ImageCapture.FLASH_MODE_AUTO,
        targetAspectRatio: Int? = null,
        onResolutionSelected: ((Int, Int) -> Unit)? = null,
    ) {
        surfaceTexture.setDefaultBufferSize(width.coerceAtLeast(1), height.coerceAtLeast(1))
        val future = ProcessCameraProvider.getInstance(context)
        providerFuture = future
        future.addListener({
            val provider = future.get()
            provider.unbindAll()

            val resolutionSelector = resolutionSelectorFor(targetAspectRatio)
            val previewBuilder = Preview.Builder()
                .setTargetRotation(Surface.ROTATION_0)
            resolutionSelector?.let { previewBuilder.setResolutionSelector(it) }
            val preview = previewBuilder
                .build()
                .also {
                    it.setSurfaceProvider { request: SurfaceRequest ->
                        surfaceTexture.setDefaultBufferSize(
                            request.resolution.width,
                            request.resolution.height,
                        )
                        onResolutionSelected?.invoke(request.resolution.width, request.resolution.height)
                        val surface = Surface(surfaceTexture)
                        request.provideSurface(surface, executor) { surface.release() }
                    }
                }

            val captureBuilder = ImageCapture.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .setFlashMode(flashMode)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setJpegQuality(imageCaptureJpegQuality)
            resolutionSelector?.let { captureBuilder.setResolutionSelector(it) }
            val capture = captureBuilder.build()
            imageCapture = capture

            val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            camera = provider.bindToLifecycle(lifecycleOwner, selector, preview, capture)
        }, mainExecutor)
    }

    fun setFlashMode(mode: Int) {
        imageCapture?.flashMode = mode
    }

    fun rebind(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int,
        targetAspectRatio: Int? = null,
        onResolutionSelected: ((Int, Int) -> Unit)? = null,
    ) {
        bindToSurfaceTexture(
            surfaceTexture,
            width,
            height,
            imageCapture?.flashMode ?: ImageCapture.FLASH_MODE_AUTO,
            targetAspectRatio,
            onResolutionSelected,
        )
    }

    fun flipCamera(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int,
        targetAspectRatio: Int? = null,
        onResolutionSelected: ((Int, Int) -> Unit)? = null,
    ) {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
            CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        rebind(surfaceTexture, width, height, targetAspectRatio, onResolutionSelected)
    }

    fun exposureCompensationState(): ExposureCompensationState {
        val exposureState = camera?.cameraInfo?.exposureState ?: return ExposureCompensationState.Unsupported
        if (!exposureState.isExposureCompensationSupported) return ExposureCompensationState.Unsupported
        return ExposureCompensationState.Supported(
            minIndex = exposureState.exposureCompensationRange.lower,
            maxIndex = exposureState.exposureCompensationRange.upper,
            currentIndex = exposureState.exposureCompensationIndex,
        )
    }

    fun setExposureCompensationIndex(index: Int): Boolean {
        val boundCamera = camera ?: return false
        val state = exposureCompensationState() as? ExposureCompensationState.Supported ?: return false
        val clampedIndex = clampExposureIndex(index, state.minIndex, state.maxIndex)
        boundCamera.cameraControl.setExposureCompensationIndex(clampedIndex)
        return true
    }

    fun focusAndMeterAt(x: Float, y: Float, width: Int, height: Int): Boolean {
        val boundCamera = camera ?: return false
        if (!canFocusAndMeterAt(x, y, width, height)) return false

        val factory = SurfaceOrientedMeteringPointFactory(width.toFloat(), height.toFloat())
        val point = factory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(
            point,
            FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE,
        ).build()
        val result = boundCamera.cameraControl.startFocusAndMetering(action)
        result.addListener({
            runCatching { result.get() }
                .onSuccess { focusMeteringResult ->
                    Log.d(tag, "Focus metering success=${focusMeteringResult.isFocusSuccessful}")
                }
                .onFailure { throwable ->
                    Log.d(tag, "Focus metering failed", throwable)
                }
        }, mainExecutor)
        return true
    }

    /** Capture a JPEG to MediaStore/Pictures/VCam. Returns the inserted Uri string. */
    fun capture(onSaved: (String?) -> Unit) {
        val capture = imageCapture ?: return onSaved(null)
        val name = "VCam_${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/VCam")
            }
        }
        val output = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values,
        ).build()

        capture.takePicture(output, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                mainExecutor.execute { onSaved(results.savedUri?.toString()) }
            }

            override fun onError(exception: ImageCaptureException) {
                mainExecutor.execute { onSaved(null) }
            }
        })
    }

    fun release() {
        providerFuture?.get()?.unbindAll()
        executor.shutdown()
    }
}
