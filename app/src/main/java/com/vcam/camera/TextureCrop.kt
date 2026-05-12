package com.vcam.camera

data class TextureCrop(
    val uMin: Float,
    val uMax: Float,
    val vMin: Float,
    val vMax: Float,
) {
    companion object {
        fun displayedSourceAspect(
            sourceWidth: Int,
            sourceHeight: Int,
            viewportWidth: Int,
            viewportHeight: Int,
        ): Float {
            val sourceAspect = sourceWidth.toFloat() / sourceHeight.toFloat()
            val viewportAspect = viewportWidth.toFloat() / viewportHeight.toFloat()
            return if ((sourceAspect > 1f) != (viewportAspect > 1f)) 1f / sourceAspect else sourceAspect
        }

        fun centerCrop(
            sourceWidth: Int,
            sourceHeight: Int,
            viewportWidth: Int,
            viewportHeight: Int,
        ): TextureCrop = centerCrop(
            sourceAspect = displayedSourceAspect(sourceWidth, sourceHeight, viewportWidth, viewportHeight),
            viewportWidth = viewportWidth,
            viewportHeight = viewportHeight,
        )

        fun centerCrop(
            sourceAspect: Float,
            viewportWidth: Int,
            viewportHeight: Int,
        ): TextureCrop {
            val viewportAspect = viewportWidth.toFloat() / viewportHeight.toFloat()
            return if (sourceAspect > viewportAspect) {
                val visibleWidth = viewportAspect / sourceAspect
                val inset = (1f - visibleWidth) / 2f
                TextureCrop(inset, 1f - inset, 0f, 1f)
            } else {
                val visibleHeight = sourceAspect / viewportAspect
                val inset = (1f - visibleHeight) / 2f
                TextureCrop(0f, 1f, inset, 1f - inset)
            }
        }
    }
}
