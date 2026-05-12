package com.vcam.color

import android.content.Context
import com.vcam.camera.CubeLut
import com.vcam.camera.parseCubeLutFromAssets

class LutCache(private val maxEntries: Int = 8) {
    private val map = object : LinkedHashMap<String, CubeLut>(maxEntries, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CubeLut>?): Boolean =
            size > maxEntries
    }

    @Synchronized fun get(key: String): CubeLut? = map[key]

    @Synchronized fun put(key: String, value: CubeLut) {
        map[key] = value
    }

    @Synchronized fun clear() {
        map.clear()
    }

    fun loadForFilter(context: Context, filter: Filter): CubeLut {
        get(filter.id)?.let { return it }
        val lut = parseCubeLutFromAssets(context, filter.lutAsset)
        put(filter.id, lut)
        return lut
    }
}
