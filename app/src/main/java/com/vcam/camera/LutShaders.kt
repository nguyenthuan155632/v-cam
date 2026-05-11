package com.vcam.camera

internal object LutShaders {

    const val VERTEX_SHADER = """
        attribute vec4 aPosition;
        attribute vec4 aTexCoord;
        uniform mat4 uTexMatrix;
        varying vec2 vTexCoord;
        void main() {
            gl_Position = aPosition;
            vTexCoord = (uTexMatrix * aTexCoord).xy;
        }
    """

    /**
     * Samples the camera SurfaceTexture (external OES), then applies an N^3 LUT
     * packed into a 2D texture of size (N*N) x N. Linear interpolation between
     * the two nearest blue slices keeps banding under control.
     */
    const val FRAGMENT_SHADER = """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        varying vec2 vTexCoord;
        uniform samplerExternalOES uCameraTex;
        uniform sampler2D uLutTex;
        uniform float uLutSize;
        uniform float uIntensity;

        vec3 sampleLut(vec3 color) {
            float n = uLutSize;
            float bIndex = color.b * (n - 1.0);
            float b0 = floor(bIndex);
            float b1 = min(b0 + 1.0, n - 1.0);
            float mix01 = bIndex - b0;

            // x within slice: r in [0, n-1] mapped to [0, 1/n] of slice width
            float sliceW = 1.0 / n;
            float rOffset = (color.r * (n - 1.0) + 0.5) / (n * n);
            float gV = (color.g * (n - 1.0) + 0.5) / n;

            vec2 uv0 = vec2(b0 * sliceW + rOffset, gV);
            vec2 uv1 = vec2(b1 * sliceW + rOffset, gV);

            vec3 c0 = texture2D(uLutTex, uv0).rgb;
            vec3 c1 = texture2D(uLutTex, uv1).rgb;
            return mix(c0, c1, mix01);
        }

        void main() {
            vec3 src = texture2D(uCameraTex, vTexCoord).rgb;
            vec3 graded = sampleLut(clamp(src, 0.0, 1.0));
            vec3 outColor = mix(src, graded, clamp(uIntensity, 0.0, 1.0));
            gl_FragColor = vec4(outColor, 1.0);
        }
    """
}
