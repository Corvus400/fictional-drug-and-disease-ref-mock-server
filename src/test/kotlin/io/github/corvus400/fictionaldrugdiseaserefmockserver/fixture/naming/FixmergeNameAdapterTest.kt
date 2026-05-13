package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.fixmerge.nameslot.NameSlot
import kotlin.test.Test
import kotlin.test.assertEquals

class FixmergeNameAdapterTest {
    private val adapter: FixmergeNameAdapter = FixmergeNameAdapter()

    @Test
    fun `coin returns a non-empty CoinedName for every NameSlot`() {
        val violations = NameSlot.entries.flatMap { slot ->
            val result = adapter.coin(slot = slot, seed = 0L)
            listOfNotNull(
                "latin is blank for slot=$slot".takeIf { result.latin.isBlank() },
                "katakana is blank for slot=$slot".takeIf { result.katakana.isBlank() },
                "mixedSurface is blank for slot=$slot".takeIf { result.mixedSurface.isBlank() },
            )
        }

        assertEquals(expected = emptyList(), actual = violations)
    }

    @Test
    fun `coin is deterministic for the same seed and slot given fresh adapter instances`() {
        val first = FixmergeNameAdapter().coin(slot = NameSlot.DRUG_BRAND, seed = 42L)
        val second = FixmergeNameAdapter().coin(slot = NameSlot.DRUG_BRAND, seed = 42L)
        assertEquals(first, second)
    }

    @Test
    fun `coin never returns a value listed in ForbiddenNames for many seeds`() {
        val violations = (0L until 100L).flatMap { seed ->
            val result = adapter.coin(slot = NameSlot.DRUG_BRAND, seed = seed)
            listOfNotNull(
                "katakana collision at seed=$seed: '${result.katakana}'".takeIf {
                    ForbiddenNames.contains(name = result.katakana)
                },
                "latin collision at seed=$seed: '${result.latin}'".takeIf {
                    ForbiddenNames.contains(name = result.latin)
                },
            )
        }

        assertEquals(expected = emptyList(), actual = violations)
    }
}
