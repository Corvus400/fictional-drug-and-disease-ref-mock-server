package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.surface.LatinToKatakanaConverter
import kotlin.test.Test
import kotlin.test.assertTrue

class LatinToKatakanaConverterTest {
    @Test
    fun `convert returns non-blank katakana for lowercase ascii input`() {
        val converter = LatinToKatakanaConverter.load()
        val result = converter.convert(input = "hungement")
        assertTrue(result.isNotBlank())
    }

    @Test
    fun `convert is deterministic for same input`() {
        val converter = LatinToKatakanaConverter.load()
        val first = converter.convert(input = "WAirTell")
        val second = converter.convert(input = "WAirTell")
        assertTrue(
            first.isNotBlank(),
            "contract assertion failed"
        )
        assertTrue(
            second.isNotBlank(),
            "contract assertion failed"
        )
        kotlin.test.assertEquals(
            expected = first,
            actual = second,
            "contract assertion failed"
        )
    }
}
