package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.roundToInt

enum class ImageSize {
    S,
    M,
    ORIGINAL,
    ;

    companion object {
        fun fromQueryValue(value: String?): ImageSize? =
            when (value) {
                null, "Original" -> ORIGINAL
                "S" -> S
                "M" -> M
                else -> null
            }
    }
}

object ImageResizer {
    fun resize(
        originalImage: BufferedImage,
        size: ImageSize,
    ): BufferedImage {
        if (size == ImageSize.ORIGINAL) {
            return originalImage
        }

        val divisor = when (size) {
            ImageSize.S -> 8
            ImageSize.M -> 4
            ImageSize.ORIGINAL -> 1
        }
        val scale = max(originalImage.width, originalImage.height).toDouble() / divisor
        val longEdge = max(1, scale.roundToInt())
        val aspectRatio = originalImage.width.toDouble() / originalImage.height.toDouble()
        val targetWidth: Int
        val targetHeight: Int
        if (originalImage.width >= originalImage.height) {
            targetWidth = longEdge
            targetHeight = max(1, (longEdge / aspectRatio).roundToInt())
        } else {
            targetHeight = longEdge
            targetWidth = max(1, (longEdge * aspectRatio).roundToInt())
        }

        val resized = BufferedImage(targetWidth, targetHeight, originalImage.type)
        val graphics = resized.createGraphics()
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null)
        graphics.dispose()
        return resized
    }
}
