package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageResizerTest {
    @Test
    fun `ImageSize は S M ORIGINAL の 3 値を持つ`() {
        assertEquals(listOf(ImageSize.S, ImageSize.M, ImageSize.ORIGINAL), ImageSize.entries)
    }

    @Test
    fun `ORIGINAL では元画像と同じ解像度を返す`() {
        val resized = ImageResizer.resize(testImage(width = 12, height = 8), ImageSize.ORIGINAL)

        assertEquals(12, resized.width)
        assertEquals(8, resized.height)
    }

    @Test
    fun `正方形 8x8 画像の S は 1x1 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 8, height = 8), ImageSize.S)

        assertEquals(1, resized.width)
        assertEquals(1, resized.height)
    }

    @Test
    fun `正方形 32x32 画像の M は 8x8 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 32, height = 32), ImageSize.M)

        assertEquals(8, resized.width)
        assertEquals(8, resized.height)
    }

    private fun testImage(
        width: Int,
        height: Int,
    ): BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
}
