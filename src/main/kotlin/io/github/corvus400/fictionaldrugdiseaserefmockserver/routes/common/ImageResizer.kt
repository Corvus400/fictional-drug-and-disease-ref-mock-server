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
            val targetWidth = originalImage.width / 8
            val targetHeight = (targetWidth / (originalImage.width.toDouble() / originalImage.height)).roundToInt()
            BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        } else if (size == ImageSize.M) {
            BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB)
        } else {
            originalImage
        }
}
