package com.vcam.baker

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ReferenceImageBaker {
    private const val SIZE = 256
    private const val BAND_HEIGHT = SIZE / 5

    fun bake(out: File) {
        out.parentFile?.mkdirs()
        val img = BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until SIZE) for (y in 0 until BAND_HEIGHT) {
            val v = x * 255 / (SIZE - 1)
            img.setRGB(x, y, rgbInt(v, v, v))
        }

        val third = SIZE / 3
        for (y in BAND_HEIGHT until BAND_HEIGHT * 2) for (x in 0 until SIZE) {
            val c = when {
                x < third -> rgbInt(220, 40, 40)
                x < 2 * third -> rgbInt(40, 200, 60)
                else -> rgbInt(40, 80, 220)
            }
            img.setRGB(x, y, c)
        }

        val skin = intArrayOf(
            rgbInt(245, 220, 195),
            rgbInt(220, 180, 140),
            rgbInt(170, 120, 90),
            rgbInt(105, 70, 50),
        )
        val quarter = SIZE / 4
        for (y in BAND_HEIGHT * 2 until BAND_HEIGHT * 3) for (x in 0 until SIZE) {
            img.setRGB(x, y, skin[(x / quarter).coerceAtMost(3)])
        }

        for (x in 0 until SIZE) for (y in BAND_HEIGHT * 3 until BAND_HEIGHT * 4) {
            val s = x.toFloat() / (SIZE - 1)
            val r = (0.5f + 0.5f * s) * 255
            val g = (0.5f - 0.4f * s) * 255
            val b = (0.5f - 0.4f * s) * 255
            img.setRGB(x, y, rgbInt(r.toInt(), g.toInt(), b.toInt()))
        }

        val wb = intArrayOf(
            rgbInt(255, 180, 100),
            rgbInt(245, 245, 240),
            rgbInt(180, 200, 240),
        )
        for (y in BAND_HEIGHT * 4 until SIZE) for (x in 0 until SIZE) {
            img.setRGB(x, y, wb[(x / third).coerceAtMost(2)])
        }

        ImageIO.write(img, "PNG", out)
    }

    private fun rgbInt(r: Int, g: Int, b: Int): Int =
        Color(r.coerceIn(0, 255), g.coerceIn(0, 255), b.coerceIn(0, 255)).rgb
}
