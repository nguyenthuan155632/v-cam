package com.vcam.color

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThumbnailRenderer(
    private val context: Context,
    private val lutCache: LutCache,
) {
    private val referenceImage: Bitmap by lazy {
        context.assets.open("thumbs/reference.png").use { BitmapFactory.decodeStream(it) }
    }
    private val cache = LruCache<String, Bitmap>(64)

    suspend fun thumbnailFor(filter: Filter): Bitmap = withContext(Dispatchers.Default) {
        cache[filter.id]?.let { return@withContext it }
        val lut = lutCache.loadForFilter(context, filter)
        val out = applyLutCpuTrilinear(referenceImage, lut, intensity = 1f)
        cache.put(filter.id, out)
        out
    }
}
