package com.vcam.camera

import android.content.Context
import java.io.BufferedReader

/** Parsed .cube LUT — stored as a flat RGB float array (size N^3 * 3, in [0,1]). */
data class CubeLut(val size: Int, val data: FloatArray)

/** Identity LUT used as fallback when no asset is available. */
fun identityCubeLut(size: Int = 17): CubeLut {
    val out = FloatArray(size * size * size * 3)
    var i = 0
    for (b in 0 until size) {
        for (g in 0 until size) {
            for (r in 0 until size) {
                out[i++] = r.toFloat() / (size - 1)
                out[i++] = g.toFloat() / (size - 1)
                out[i++] = b.toFloat() / (size - 1)
            }
        }
    }
    return CubeLut(size, out)
}

/** Minimal .cube parser — supports LUT_3D_SIZE; ignores DOMAIN_MIN/MAX (assumes 0..1). */
fun parseCubeLutFromAssets(context: Context, assetPath: String): CubeLut {
    return runCatching {
        context.assets.open(assetPath).bufferedReader().use(::parseCubeLut)
    }.getOrElse { identityCubeLut() }
}

fun parseCubeLut(reader: BufferedReader): CubeLut {
    var size = 0
    val temp = ArrayList<Float>(35000)
    reader.forEachLine { rawLine ->
        val line = rawLine.trim()
        if (line.isEmpty() || line.startsWith("#")) return@forEachLine
        when {
            line.startsWith("TITLE", ignoreCase = true) -> { /* ignore */ }
            line.startsWith("LUT_3D_SIZE", ignoreCase = true) -> {
                size = line.split(Regex("\\s+")).getOrNull(1)?.toIntOrNull() ?: 0
            }
            line.startsWith("DOMAIN_MIN", ignoreCase = true) -> { /* ignore */ }
            line.startsWith("DOMAIN_MAX", ignoreCase = true) -> { /* ignore */ }
            line.startsWith("LUT_1D_SIZE", ignoreCase = true) -> { /* not supported */ }
            else -> {
                val parts = line.split(Regex("\\s+"))
                if (parts.size >= 3) {
                    val r = parts[0].toFloatOrNull()
                    val g = parts[1].toFloatOrNull()
                    val b = parts[2].toFloatOrNull()
                    if (r != null && g != null && b != null) {
                        temp.add(r); temp.add(g); temp.add(b)
                    }
                }
            }
        }
    }
    val n = if (size > 0) size else 17
    val expected = n * n * n * 3
    val data = if (temp.size == expected) temp.toFloatArray() else identityCubeLut(n).data
    return CubeLut(n, data)
}
