package com.vcam.color

import android.graphics.Bitmap
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES20
import android.opengl.GLUtils
import android.os.Handler
import android.os.HandlerThread
import com.vcam.camera.CubeLut
import com.vcam.camera.LutShaders
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class OffscreenLutProcessor {
    private val thread = HandlerThread("VCamLutProcessor").apply { start() }
    private val handler = Handler(thread.looper)
    private var egl: EglState? = null
    private var program = 0
    private var aPositionLoc = 0
    private var aTexCoordLoc = 0
    private var uTexMatrixLoc = 0
    private var uSourceTexLoc = 0
    private var uLutTexLoc = 0
    private var uLutSizeLoc = 0
    private var uIntensityLoc = 0

    private val vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(VERTICES.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply { put(VERTICES); position(0) }

    private val texBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(TEX_COORDS.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply { put(TEX_COORDS); position(0) }

    fun process(bitmap: Bitmap, lut: CubeLut, intensity: Float, callback: (Bitmap?) -> Unit) {
        handler.post {
            val out = runCatching { render(bitmap, lut, intensity.coerceIn(0f, 1f)) }.getOrNull()
            callback(out)
        }
    }

    fun release() {
        handler.post {
            releaseGl()
            thread.quitSafely()
        }
    }

    private fun render(bitmap: Bitmap, lut: CubeLut, intensity: Float): Bitmap {
        ensureGl()
        GLES20.glViewport(0, 0, bitmap.width, bitmap.height)

        val sourceTex = createBitmapTexture(bitmap)
        val lutTex = createLutTexture(lut)
        val colorTex = createOutputTexture(bitmap.width, bitmap.height)
        val fbo = IntArray(1).also { GLES20.glGenFramebuffers(1, it, 0) }[0]

        try {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo)
            GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                colorTex,
                0,
            )
            require(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) == GLES20.GL_FRAMEBUFFER_COMPLETE) {
                "Framebuffer incomplete"
            }

            GLES20.glClearColor(0f, 0f, 0f, 1f)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            GLES20.glUseProgram(program)

            vertexBuffer.position(0)
            GLES20.glEnableVertexAttribArray(aPositionLoc)
            GLES20.glVertexAttribPointer(aPositionLoc, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            texBuffer.position(0)
            GLES20.glEnableVertexAttribArray(aTexCoordLoc)
            GLES20.glVertexAttribPointer(aTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, texBuffer)

            GLES20.glUniformMatrix4fv(uTexMatrixLoc, 1, false, IDENTITY_MATRIX, 0)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sourceTex)
            GLES20.glUniform1i(uSourceTexLoc, 0)

            GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lutTex)
            GLES20.glUniform1i(uLutTexLoc, 1)

            GLES20.glUniform1f(uLutSizeLoc, lut.size.toFloat())
            GLES20.glUniform1f(uIntensityLoc, intensity)

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            GLES20.glDisableVertexAttribArray(aPositionLoc)
            GLES20.glDisableVertexAttribArray(aTexCoordLoc)

            return readBitmap(bitmap.width, bitmap.height)
        } finally {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            GLES20.glDeleteFramebuffers(1, intArrayOf(fbo), 0)
            GLES20.glDeleteTextures(3, intArrayOf(sourceTex, lutTex, colorTex), 0)
        }
    }

    private fun ensureGl() {
        if (egl == null) egl = createEgl()
        if (program == 0) {
            program = buildProgram()
            aPositionLoc = GLES20.glGetAttribLocation(program, "aPosition")
            aTexCoordLoc = GLES20.glGetAttribLocation(program, "aTexCoord")
            uTexMatrixLoc = GLES20.glGetUniformLocation(program, "uTexMatrix")
            uSourceTexLoc = GLES20.glGetUniformLocation(program, "uSourceTex")
            uLutTexLoc = GLES20.glGetUniformLocation(program, "uLutTex")
            uLutSizeLoc = GLES20.glGetUniformLocation(program, "uLutSize")
            uIntensityLoc = GLES20.glGetUniformLocation(program, "uIntensity")
        }
    }

    private fun createEgl(): EglState {
        val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        require(display != EGL14.EGL_NO_DISPLAY) { "No EGL display" }
        val version = IntArray(2)
        require(EGL14.eglInitialize(display, version, 0, version, 1)) { "EGL initialize failed" }

        val config = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        require(
            EGL14.eglChooseConfig(
                display,
                intArrayOf(
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_NONE,
                ),
                0,
                config,
                0,
                1,
                numConfigs,
                0,
            ),
        ) { "EGL config failed" }

        val context = EGL14.eglCreateContext(
            display,
            config[0],
            EGL14.EGL_NO_CONTEXT,
            intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE),
            0,
        )
        require(context != EGL14.EGL_NO_CONTEXT) { "EGL context failed" }

        val surface = EGL14.eglCreatePbufferSurface(
            display,
            config[0],
            intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE),
            0,
        )
        require(surface != EGL14.EGL_NO_SURFACE) { "EGL surface failed" }
        require(EGL14.eglMakeCurrent(display, surface, surface, context)) { "EGL make current failed" }
        return EglState(display, context, surface)
    }

    private fun buildProgram(): Int {
        val vs = compile(GLES20.GL_VERTEX_SHADER, LutShaders.VERTEX_SHADER)
        val fs = compile(GLES20.GL_FRAGMENT_SHADER, LutShaders.OFFSCREEN_FRAGMENT)
        val p = GLES20.glCreateProgram()
        GLES20.glAttachShader(p, vs)
        GLES20.glAttachShader(p, fs)
        GLES20.glLinkProgram(p)
        GLES20.glDeleteShader(vs)
        GLES20.glDeleteShader(fs)
        return p
    }

    private fun compile(type: Int, src: String): Int {
        val s = GLES20.glCreateShader(type)
        GLES20.glShaderSource(s, src)
        GLES20.glCompileShader(s)
        return s
    }

    private fun createBitmapTexture(bitmap: Bitmap): Int {
        val tex = genTexture()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex)
        setTextureParams()
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        return tex
    }

    private fun createOutputTexture(width: Int, height: Int): Int {
        val tex = genTexture()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex)
        setTextureParams()
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            width,
            height,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            null,
        )
        return tex
    }

    private fun createLutTexture(lut: CubeLut): Int {
        val n = lut.size
        val width = n * n
        val height = n
        val bytes = ByteBuffer.allocateDirect(width * height * 3).order(ByteOrder.nativeOrder())
        var i = 0
        for (b in 0 until n) {
            for (g in 0 until n) {
                for (r in 0 until n) {
                    bytes.put((lut.data[i] * 255f).toInt().toByte())
                    bytes.put((lut.data[i + 1] * 255f).toInt().toByte())
                    bytes.put((lut.data[i + 2] * 255f).toInt().toByte())
                    i += 3
                }
            }
        }
        bytes.position(0)

        val tex = genTexture()
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex)
        setTextureParams()
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGB,
            width,
            height,
            0,
            GLES20.GL_RGB,
            GLES20.GL_UNSIGNED_BYTE,
            bytes,
        )
        return tex
    }

    private fun readBitmap(width: Int, height: Int): Bitmap {
        val buffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder())
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer)
        buffer.position(0)

        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val r = buffer.get().toInt() and 0xff
                val g = buffer.get().toInt() and 0xff
                val b = buffer.get().toInt() and 0xff
                val a = buffer.get().toInt() and 0xff
                pixels[(height - 1 - y) * width + x] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun genTexture(): Int {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        return ids[0]
    }

    private fun setTextureParams() {
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    }

    private fun releaseGl() {
        if (program != 0) {
            GLES20.glDeleteProgram(program)
            program = 0
        }
        egl?.let {
            EGL14.eglMakeCurrent(it.display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(it.display, it.surface)
            EGL14.eglDestroyContext(it.display, it.context)
            EGL14.eglTerminate(it.display)
        }
        egl = null
    }

    private data class EglState(
        val display: EGLDisplay,
        val context: EGLContext,
        val surface: EGLSurface,
    )

    companion object {
        private val VERTICES = floatArrayOf(
            -1f, -1f,
             1f, -1f,
            -1f,  1f,
             1f,  1f,
        )
        private val TEX_COORDS = floatArrayOf(
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
        )
        private val IDENTITY_MATRIX = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f,
        )
    }
}
