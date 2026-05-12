package com.vcam.camera

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class CameraGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : GLSurfaceView(context, attrs) {

    var renderer: LutRenderer? = null
        private set

    var onPreviewTap: ((x: Float, y: Float, width: Int, height: Int) -> Unit)? = null

    fun bindRenderer(renderer: LutRenderer) {
        setEGLContextClientVersion(2)
        this.renderer = renderer
        renderer.attach(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            performClick()
            onPreviewTap?.invoke(event.x, event.y, width, height)
            return true
        }
        return true
    }
}
