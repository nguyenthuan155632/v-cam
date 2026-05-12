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

    private const val SHARED_BODY = """
        precision mediump float;
        varying vec2 vTexCoord;
        uniform sampler2D uLutTex;
        uniform float uLutSize;
        uniform float uIntensity;

        vec3 sampleLut(vec3 color) {
            float n = uLutSize;
            float bIndex = color.b * (n - 1.0);
            float b0 = floor(bIndex);
            float b1 = min(b0 + 1.0, n - 1.0);
            float mix01 = bIndex - b0;

            float sliceW = 1.0 / n;
            float rOffset = (color.r * (n - 1.0) + 0.5) / (n * n);
            float gV = (color.g * (n - 1.0) + 0.5) / n;

            vec2 uv0 = vec2(b0 * sliceW + rOffset, gV);
            vec2 uv1 = vec2(b1 * sliceW + rOffset, gV);

            vec3 c0 = texture2D(uLutTex, uv0).rgb;
            vec3 c1 = texture2D(uLutTex, uv1).rgb;
            return mix(c0, c1, mix01);
        }
    """

    const val PREVIEW_FRAGMENT = """
        #extension GL_OES_EGL_image_external : require
        $SHARED_BODY
        uniform samplerExternalOES uCameraTex;
        void main() {
            vec3 src = texture2D(uCameraTex, vTexCoord).rgb;
            vec3 graded = sampleLut(clamp(src, 0.0, 1.0));
            vec3 outColor = mix(src, graded, clamp(uIntensity, 0.0, 1.0));
            gl_FragColor = vec4(outColor, 1.0);
        }
    """

    const val OFFSCREEN_FRAGMENT = """
        $SHARED_BODY
        uniform sampler2D uSourceTex;
        void main() {
            vec3 src = texture2D(uSourceTex, vTexCoord).rgb;
            vec3 graded = sampleLut(clamp(src, 0.0, 1.0));
            vec3 outColor = mix(src, graded, clamp(uIntensity, 0.0, 1.0));
            gl_FragColor = vec4(outColor, 1.0);
        }
    """
}
