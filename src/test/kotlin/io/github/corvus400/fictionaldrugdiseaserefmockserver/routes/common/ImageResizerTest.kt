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

        assertEquals(ImageDimensions(width = 12, height = 8), resized.dimensions())
    }

    @Test
    fun `正方形 8x8 画像の S は 1x1 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 8, height = 8), ImageSize.S)

        assertEquals(ImageDimensions(width = 1, height = 1), resized.dimensions())
    }

    @Test
    fun `正方形 32x32 画像の M は 8x8 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 32, height = 32), ImageSize.M)

        assertEquals(ImageDimensions(width = 8, height = 8), resized.dimensions())
    }

    @Test
    fun `横長 800x400 画像の S は 100x50 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 800, height = 400), ImageSize.S)

        assertEquals(ImageDimensions(width = 100, height = 50), resized.dimensions())
    }

    @Test
    fun `縦長 200x800 画像の S は 25x100 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 200, height = 800), ImageSize.S)

        assertEquals(ImageDimensions(width = 25, height = 100), resized.dimensions())
    }

    @Test
    fun `縦長 200x800 画像の M は 50x200 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 200, height = 800), ImageSize.M)

        assertEquals(ImageDimensions(width = 50, height = 200), resized.dimensions())
    }

    @Test
    fun `極小画像の S は 0 を生まず最小 1x1 を返す`() {
        val resized = ImageResizer.resize(testImage(width = 4, height = 2), ImageSize.S)

        assertEquals(ImageDimensions(width = 1, height = 1), resized.dimensions())
    }

    private fun testImage(
        width: Int,
        height: Int,
    ): BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    private fun BufferedImage.dimensions(): ImageDimensions =
        ImageDimensions(width = width, height = height)

    private data class ImageDimensions(
        val width: Int,
        val height: Int,
    )
}
