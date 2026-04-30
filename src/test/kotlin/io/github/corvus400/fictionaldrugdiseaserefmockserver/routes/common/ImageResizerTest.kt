package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.common

import kotlin.test.Test
import kotlin.test.assertEquals

class ImageResizerTest {
    @Test
    fun `ImageSize は S M ORIGINAL の 3 値を持つ`() {
        assertEquals(listOf(ImageSize.S, ImageSize.M, ImageSize.ORIGINAL), ImageSize.entries)
    }
}
