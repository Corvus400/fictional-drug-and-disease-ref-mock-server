package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import java.awt.image.BufferedImage

enum class ImageSize {
    S,
    M,
    ORIGINAL,
}

object ImageResizer {
    fun resize(
        originalImage: BufferedImage,
        size: ImageSize,
    ): BufferedImage = originalImage
}
