package com.vcam.ui.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vcam.camera.parseCubeLutFromAssets
import com.vcam.color.FilterCatalog
import com.vcam.color.OffscreenLutProcessor
import com.vcam.data.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PhotoPreviewState(
    val activeFilterId: String = FilterCatalog.all.first().id,
    val intensity: Int = 100,
    val timestamp: String = "",
    val dimensions: String = "",
    val starred: Boolean = false,
    val bitmap: Bitmap? = null,
)

class PhotoPreviewViewModel(
    private val context: Context? = null,
    private val repo: SettingsRepository? = null,
    private val offscreenProcessor: OffscreenLutProcessor? = null,
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoPreviewState())
    val state: StateFlow<PhotoPreviewState> = _state.asStateFlow()
    private var sourceBitmap: Bitmap? = null
    private var renderToken = 0

    init {
        if (repo != null) {
            viewModelScope.launch {
                repo.settings.collect { user ->
                    val resolvedId = FilterCatalog.byId(user.defaultFilterId)?.id ?: FilterCatalog.all.first().id
                    _state.update { it.copy(activeFilterId = resolvedId, intensity = user.defaultIntensity) }
                    renderActiveFilter()
                }
            }
        }
    }

    fun loadPhoto(uriString: String?) {
        val appContext = context ?: return
        if (uriString.isNullOrBlank() || uriString == "preview") return
        viewModelScope.launch {
            val (bmp, w, h) = withContext(Dispatchers.IO) { decode(appContext, uriString) }
            if (bmp != null) {
                sourceBitmap = bmp
                _state.update {
                    it.copy(
                        bitmap = bmp,
                        timestamp = SimpleDateFormat("HH:mm", Locale.US).format(Date()),
                        dimensions = "$w × $h",
                    )
                }
                renderActiveFilter()
            }
        }
    }

    private fun renderActiveFilter() {
        val appContext = context ?: return
        val processor = offscreenProcessor ?: return
        val source = sourceBitmap ?: return
        val filter = FilterCatalog.byId(_state.value.activeFilterId) ?: FilterCatalog.all.first()
        val intensity = _state.value.intensity / 100f
        val token = ++renderToken
        viewModelScope.launch {
            val lut = withContext(Dispatchers.IO) { parseCubeLutFromAssets(appContext, filter.lutAsset) }
            processor.process(source, lut, intensity) { rendered ->
                if (token == renderToken && rendered != null) {
                    _state.update { it.copy(bitmap = rendered) }
                }
            }
        }
    }

    private fun decode(context: Context, uriString: String): Triple<Bitmap?, Int, Int> = runCatching {
        val uri = Uri.parse(uriString)
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        val srcW = bounds.outWidth.takeIf { it > 0 } ?: return@runCatching Triple(null, 0, 0)
        val srcH = bounds.outHeight.takeIf { it > 0 } ?: return@runCatching Triple(null, 0, 0)

        val target = 1600
        var sample = 1
        while ((srcW / sample) > target || (srcH / sample) > target) sample *= 2

        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val raw = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        } ?: return@runCatching Triple(null, srcW, srcH)

        val orientation = resolver.openInputStream(uri)?.use { stream ->
            runCatching { ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) }
                .getOrDefault(ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL

        val rotated = applyOrientation(raw, orientation)
        Triple(rotated, srcW, srcH)
    }.getOrElse { Triple(null, 0, 0) }

    private fun applyOrientation(src: Bitmap, orientation: Int): Bitmap {
        val m = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> m.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> m.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> m.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> m.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> m.postScale(1f, -1f)
            else -> return src
        }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true).also {
            if (it !== src) src.recycle()
        }
    }

    fun setFilterId(id: String) = _state.update {
        val resolvedId = FilterCatalog.byId(id)?.id ?: FilterCatalog.all.first().id
        it.copy(activeFilterId = resolvedId)
    }.also { renderActiveFilter() }

    fun setIntensity(v: Int) = _state.update { it.copy(intensity = v.coerceIn(0, 100)) }
        .also { renderActiveFilter() }

    fun toggleStar() = _state.update { it.copy(starred = !it.starred) }

    class Factory(
        private val context: Context,
        private val repo: SettingsRepository,
        private val offscreenProcessor: OffscreenLutProcessor,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PhotoPreviewViewModel(context.applicationContext, repo, offscreenProcessor) as T
    }
}
