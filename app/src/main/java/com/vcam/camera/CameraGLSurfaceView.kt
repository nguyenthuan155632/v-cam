package com.vcam.camera

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class CameraGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : GLSurfaceView(context, attrs) {

    var renderer: LutRenderer? = null
        private set

    fun bindRenderer(renderer: LutRenderer) {
        setEGLContextClientVersion(2)
        this.renderer = renderer
        renderer.attach(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}
