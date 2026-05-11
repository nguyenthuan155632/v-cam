package com.vcam.camera

import android.content.ContentValues
import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
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
    private val executor = Executors.newSingleThreadExecutor()
    private var imageCapture: ImageCapture? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var providerFuture: ListenableFuture<ProcessCameraProvider>? = null

    fun bindToSurfaceTexture(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int,
        flashMode: Int = ImageCapture.FLASH_MODE_AUTO,
    ) {
        surfaceTexture.setDefaultBufferSize(width.coerceAtLeast(1), height.coerceAtLeast(1))
        val future = ProcessCameraProvider.getInstance(context)
        providerFuture = future
        future.addListener({
            val provider = future.get()
            provider.unbindAll()

            val preview = Preview.Builder()
                .setTargetResolution(Size(width.coerceAtLeast(1), height.coerceAtLeast(1)))
                .build()
                .also {
                    it.setSurfaceProvider { request: SurfaceRequest ->
                        val surface = Surface(surfaceTexture)
                        request.provideSurface(surface, executor) { surface.release() }
                    }
                }

            val capture = ImageCapture.Builder()
                .setFlashMode(flashMode)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            imageCapture = capture

            val selector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            provider.bindToLifecycle(lifecycleOwner, selector, preview, capture)
        }, ContextCompat.getMainExecutor(context))
    }

    fun setFlashMode(mode: Int) {
        imageCapture?.flashMode = mode
    }

    fun flipCamera(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
            CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        bindToSurfaceTexture(surfaceTexture, width, height, imageCapture?.flashMode ?: ImageCapture.FLASH_MODE_AUTO)
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
                onSaved(results.savedUri?.toString())
            }
            override fun onError(exception: ImageCaptureException) {
                onSaved(null)
            }
        })
    }

    fun release() {
        providerFuture?.get()?.unbindAll()
        executor.shutdown()
    }
}
