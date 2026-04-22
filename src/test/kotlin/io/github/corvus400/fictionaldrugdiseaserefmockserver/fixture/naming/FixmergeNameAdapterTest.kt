package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FixmergeNameAdapterTest {
    private val adapter: FixmergeNameAdapter = FixmergeNameAdapter()

    @Test
    fun `coin returns a non-empty CoinedName for every NameSlot`() {
        for (slot in NameSlot.entries) {
            val result = adapter.coin(slot = slot, seed = 0L)
            assertTrue(result.latin.isNotBlank(), "latin is blank for slot=$slot")
            assertTrue(result.katakana.isNotBlank(), "katakana is blank for slot=$slot")
            assertTrue(result.mixedSurface.isNotBlank(), "mixedSurface is blank for slot=$slot")
        }
    }

    @Test
    fun `coin is deterministic for the same seed and slot`() {
        val first = adapter.coin(slot = NameSlot.DRUG_BRAND, seed = 42L)
        val second = adapter.coin(slot = NameSlot.DRUG_BRAND, seed = 42L)
        assertEquals(first, second)
    }

    @Test
    fun `coin never returns a value listed in ForbiddenNames for many seeds`() {
        for (seed in 0L until 100L) {
            val result = adapter.coin(slot = NameSlot.DRUG_BRAND, seed = seed)
            assertFalse(
                ForbiddenNames.contains(name = result.katakana),
                "katakana collision at seed=$seed: '${result.katakana}'",
            )
            assertFalse(
                ForbiddenNames.contains(name = result.latin),
                "latin collision at seed=$seed: '${result.latin}'",
            )
        }
    }
}
