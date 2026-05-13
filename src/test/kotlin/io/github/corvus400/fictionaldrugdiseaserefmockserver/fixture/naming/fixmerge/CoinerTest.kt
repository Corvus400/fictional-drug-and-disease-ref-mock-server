package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.lexicon.Pattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import kotlin.test.Test
import kotlin.test.assertEquals

class CoinerTest {
    @Test
    fun `coinName returns deterministic CoinedName for same slot and seed`() {
        val engine = FixmergeEngineFactory.default()
        val first = engine.coinName(slot = NameSlot.DRUG_BRAND, seed = 100L)
        val second = engine.coinName(slot = NameSlot.DRUG_BRAND, seed = 100L)
        assertEquals(
            expected = DeterministicCoinedNameSnapshot(
                latin = first.latin,
                katakana = first.katakana,
                pattern = first.pattern,
            ),
            actual = DeterministicCoinedNameSnapshot(
                latin = second.latin,
                katakana = second.katakana,
                pattern = second.pattern,
            ),
            message = "coinName output must be deterministic for the same slot and seed",
        )
    }

    @Test
    fun `coinName returns non-blank latin and katakana`() {
        val engine = FixmergeEngineFactory.default()
        val coined = engine.coinName(slot = NameSlot.DISEASE_NAME, seed = 42L)
        assertEquals(
            expected = NonBlankCoinedNameSnapshot(latin = true, katakana = true),
            actual = NonBlankCoinedNameSnapshot(
                latin = coined.latin.isNotBlank(),
                katakana = coined.katakana.isNotBlank(),
            ),
            message = "coinName must return non-blank latin and katakana text",
        )
    }

    @Test
    fun `coinName respects default pattern per slot`() {
        val engine = FixmergeEngineFactory.default()
        val brand = engine.coinName(slot = NameSlot.DRUG_BRAND, seed = 1L)
        val generic = engine.coinName(slot = NameSlot.DRUG_GENERIC, seed = 1L)
        assertEquals(
            expected = mapOf(
                NameSlot.DRUG_BRAND to NameSlot.DRUG_BRAND.defaultPattern,
                NameSlot.DRUG_GENERIC to NameSlot.DRUG_GENERIC.defaultPattern,
            ),
            actual = mapOf(
                NameSlot.DRUG_BRAND to brand.pattern,
                NameSlot.DRUG_GENERIC to generic.pattern,
            ),
            message = "coinName must use the slot default pattern",
        )
    }

    private data class DeterministicCoinedNameSnapshot(
        val latin: String,
        val katakana: String,
        val pattern: Pattern,
    )

    private data class NonBlankCoinedNameSnapshot(
        val latin: Boolean,
        val katakana: Boolean,
    )
}
