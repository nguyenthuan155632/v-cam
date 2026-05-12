package com.vcam.camera

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicReference
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Renders the camera SurfaceTexture into the GLSurfaceView and applies a 3D LUT
 * (loaded as a tiled 2D texture) at the chosen intensity. The Surface for CameraX
 * is produced from the underlying SurfaceTexture once the GL context is ready.
 */
class LutRenderer(
    private val onSurfaceTextureReady: (SurfaceTexture, Int, Int) -> Unit,
) : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    @Volatile private var glSurfaceView: GLSurfaceView? = null
    fun attach(view: GLSurfaceView) { glSurfaceView = view }

    private var program = 0
    private var aPositionLoc = 0
    private var aTexCoordLoc = 0
    private var uTexMatrixLoc = 0
    private var uCameraTexLoc = 0
    private var uLutTexLoc = 0
    private var uLutSizeLoc = 0
    private var uIntensityLoc = 0

    private var cameraTexId = 0
    private var lutTexId = 0
    private var surfaceTexture: SurfaceTexture? = null
    private val texMatrix = FloatArray(16).also { Matrix.setIdentityM(it, 0) }

    private val pendingLut = AtomicReference<CubeLut?>(null)
    @Volatile private var currentLutSize: Int = 17
    @Volatile var intensity: Float = 1f

    private var viewportW = 1
    private var viewportH = 1
    private var sourceW = 1
    private var sourceH = 1
    private var textureCrop = TextureCrop(0f, 1f, 0f, 1f)
    private val surfaceReadyGate = SurfaceReadyGate()

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

    fun submitLut(lut: CubeLut) { pendingLut.set(lut); glSurfaceView?.requestRender() }

    fun updateSourceSize(width: Int, height: Int) {
        sourceW = width.coerceAtLeast(1)
        sourceH = height.coerceAtLeast(1)
        updateTextureCrop()
    }

    private fun updateTextureCrop() {
        textureCrop = TextureCrop.centerCrop(sourceW, sourceH, viewportW, viewportH)
        texBuffer.position(0)
        texBuffer.put(
            floatArrayOf(
                textureCrop.uMin, textureCrop.vMin,
                textureCrop.uMax, textureCrop.vMin,
                textureCrop.uMin, textureCrop.vMax,
                textureCrop.uMax, textureCrop.vMax,
            ),
        )
        texBuffer.position(0)
        glSurfaceView?.requestRender()
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        program = buildProgram()
        aPositionLoc = GLES20.glGetAttribLocation(program, "aPosition")
        aTexCoordLoc = GLES20.glGetAttribLocation(program, "aTexCoord")
        uTexMatrixLoc = GLES20.glGetUniformLocation(program, "uTexMatrix")
        uCameraTexLoc = GLES20.glGetUniformLocation(program, "uCameraTex")
        uLutTexLoc = GLES20.glGetUniformLocation(program, "uLutTex")
        uLutSizeLoc = GLES20.glGetUniformLocation(program, "uLutSize")
        uIntensityLoc = GLES20.glGetUniformLocation(program, "uIntensity")

        cameraTexId = createExternalTexture()
        lutTexId = createLutTexture(identityCubeLut())

        val st = SurfaceTexture(cameraTexId).also { it.setOnFrameAvailableListener(this) }
        surfaceTexture = st
        if (surfaceReadyGate.markSurfaceTextureReady(viewportW, viewportH)) {
            onSurfaceTextureReady(st, viewportW, viewportH)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewportW = width
        viewportH = height
        updateTextureCrop()
        GLES20.glViewport(0, 0, width, height)
        surfaceTexture?.let { st ->
            st.setDefaultBufferSize(width, height)
            if (surfaceReadyGate.markSurfaceSizeChanged(width, height)) {
                onSurfaceTextureReady(st, width, height)
            }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        val st = surfaceTexture ?: return
        st.updateTexImage()
        st.getTransformMatrix(texMatrix)

        pendingLut.getAndSet(null)?.let {
            currentLutSize = it.size
            uploadLut(lutTexId, it)
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

        GLES20.glUniformMatrix4fv(uTexMatrixLoc, 1, false, texMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTexId)
        GLES20.glUniform1i(uCameraTexLoc, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lutTexId)
        GLES20.glUniform1i(uLutTexLoc, 1)

        GLES20.glUniform1f(uLutSizeLoc, currentLutSize.toFloat())
        GLES20.glUniform1f(uIntensityLoc, intensity)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aTexCoordLoc)
    }

    override fun onFrameAvailable(st: SurfaceTexture?) {
        glSurfaceView?.requestRender()
    }

    fun release() {
        surfaceTexture?.release()
        surfaceTexture = null
        if (cameraTexId != 0) GLES20.glDeleteTextures(1, intArrayOf(cameraTexId), 0)
        if (lutTexId != 0) GLES20.glDeleteTextures(1, intArrayOf(lutTexId), 0)
        if (program != 0) GLES20.glDeleteProgram(program)
    }

    private fun buildProgram(): Int {
        val vs = compile(GLES20.GL_VERTEX_SHADER, LutShaders.VERTEX_SHADER)
        val fs = compile(GLES20.GL_FRAGMENT_SHADER, LutShaders.FRAGMENT_SHADER)
        val p = GLES20.glCreateProgram()
        GLES20.glAttachShader(p, vs)
        GLES20.glAttachShader(p, fs)
        GLES20.glLinkProgram(p)
        return p
    }

    private fun compile(type: Int, src: String): Int {
        val s = GLES20.glCreateShader(type)
        GLES20.glShaderSource(s, src)
        GLES20.glCompileShader(s)
        return s
    }

    private fun createExternalTexture(): Int {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        val tex = ids[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        return tex
    }

    private fun createLutTexture(lut: CubeLut): Int {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        val tex = ids[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        uploadLut(tex, lut)
        return tex
    }

    private fun uploadLut(tex: Int, lut: CubeLut) {
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
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex)
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
            width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, bytes,
        )
        currentLutSize = n
    }

    companion object {
        private val VERTICES = floatArrayOf(
            -1f, -1f,
             1f, -1f,
            -1f,  1f,
             1f,  1f,
        )
        private val TEX_COORDS = floatArrayOf(
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f,
        )
    }
}
