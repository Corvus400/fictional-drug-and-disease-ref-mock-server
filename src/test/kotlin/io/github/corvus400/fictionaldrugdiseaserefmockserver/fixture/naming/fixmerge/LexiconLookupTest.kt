package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Lexicon
import kotlin.test.Test
import kotlin.test.assertTrue

class LexiconLookupTest {
    @Test
    fun `Lexicon load returns at least 500 entries`() {
        val lexicon = Lexicon.load()
        assertTrue(lexicon.size >= 500, "expected >=500 entries but was ${lexicon.size}")
    }
}
