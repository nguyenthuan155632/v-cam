package com.vcam.camera

class SurfaceReadyGate {
    var readySize: Pair<Int, Int>? = null
        private set

    fun markSurfaceTextureReady(width: Int, height: Int): Boolean = markReady(width, height)

    fun markSurfaceSizeChanged(width: Int, height: Int): Boolean = markReady(width, height)

    private fun markReady(width: Int, height: Int): Boolean {
        if (readySize != null || width <= 1 || height <= 1) return false
        readySize = width to height
        return true
    }
}
