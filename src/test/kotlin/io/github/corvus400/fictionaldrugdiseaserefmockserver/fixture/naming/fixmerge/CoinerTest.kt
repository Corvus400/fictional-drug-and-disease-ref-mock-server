package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoinerTest {
    @Test
    fun `coinName returns deterministic CoinedName for same slot and seed`() {
        val engine = FixmergeEngineFactory.default()
        val first = engine.coinName(slot = NameSlot.DRUG_BRAND, seed = 100L)
        val second = engine.coinName(slot = NameSlot.DRUG_BRAND, seed = 100L)
        assertEquals(
            first.latin,
            second.latin,
            "coinName latin output must be deterministic for the same slot and seed",
        )
        assertEquals(
            first.katakana,
            second.katakana,
            "coinName katakana output must be deterministic for the same slot and seed",
        )
        assertEquals(
            first.pattern,
            second.pattern,
            "coinName pattern output must be deterministic for the same slot and seed",
        )
    }

    @Test
    fun `coinName returns non-blank latin and katakana`() {
        val engine = FixmergeEngineFactory.default()
        val coined = engine.coinName(slot = NameSlot.DISEASE_NAME, seed = 42L)
        assertTrue(
            coined.latin.isNotBlank(),
            "coinName must return non-blank latin text",
        )
        assertTrue(
            coined.katakana.isNotBlank(),
            "coinName must return non-blank katakana text",
        )
    }

    @Test
    fun `coinName respects default pattern per slot`() {
        val engine = FixmergeEngineFactory.default()
        val brand = engine.coinName(slot = NameSlot.DRUG_BRAND, seed = 1L)
        val generic = engine.coinName(slot = NameSlot.DRUG_GENERIC, seed = 1L)
        assertEquals(
            NameSlot.DRUG_BRAND.defaultPattern,
            brand.pattern,
            "DRUG_BRAND coinName must use the slot default pattern",
        )
        assertEquals(
            NameSlot.DRUG_GENERIC.defaultPattern,
            generic.pattern,
            "DRUG_GENERIC coinName must use the slot default pattern",
        )
    }
}
