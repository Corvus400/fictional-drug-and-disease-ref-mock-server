package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import java.awt.image.BufferedImage
import kotlin.math.roundToInt

enum class ImageSize {
    S,
    M,
    ORIGINAL,
}

object ImageResizer {
    fun resize(
        originalImage: BufferedImage,
        size: ImageSize,
    ): BufferedImage =
        if (size == ImageSize.S) {
            val aspectRatio = originalImage.width.toDouble() / originalImage.height
            val longEdge = maxOf(originalImage.width, originalImage.height) / 8
            val targetWidth = if (originalImage.width >=
                originalImage.height
            ) {
                longEdge
            } else {
                (longEdge * aspectRatio).roundToInt()
            }
            val targetHeight = if (originalImage.width >=
                originalImage.height
            ) {
                (longEdge / aspectRatio).roundToInt()
            } else {
                longEdge
            }
            BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        } else if (size == ImageSize.M) {
            BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB)
        } else {
            originalImage
        }
}
