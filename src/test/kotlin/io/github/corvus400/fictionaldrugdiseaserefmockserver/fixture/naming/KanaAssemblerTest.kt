package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KanaAssemblerTest {
    @Test
    fun `assemble returns identical kana for identical seed and pattern`() {
        val seed = stableHash("drug_0001", slot = 0, index = 0)
        val first = KanaAssembler.assemble(seed, NamePattern.PATTERN_B)
        val second = KanaAssembler.assemble(seed, NamePattern.PATTERN_B)
        assertEquals(first, second)
    }

    @Test
    fun `assemble produces 2-3 syllable kana for PATTERN_A`() {
        val kana = KanaAssembler.assemble(
            stableHash("drug_0001", slot = 0, index = 0),
            NamePattern.PATTERN_A,
        )
        assertTrue(
            kana.length in 2..4,
            "PATTERN_A should produce 2-3 syllables but got '$kana'",
        )
    }

    @Test
    fun `assemble produces 3-4 syllable kana for PATTERN_B`() {
        val kana = KanaAssembler.assemble(
            stableHash("drug_0001", slot = 0, index = 0),
            NamePattern.PATTERN_B,
        )
        assertTrue(
            kana.length in 3..5,
            "PATTERN_B should produce 3-4 syllables but got '$kana'",
        )
    }

    @Test
    fun `assemble produces 4-6 syllable kana for PATTERN_C`() {
        val kana = KanaAssembler.assemble(
            stableHash("drug_0001", slot = 0, index = 0),
            NamePattern.PATTERN_C,
        )
        assertTrue(
            kana.length in 4..7,
            "PATTERN_C should produce 4-6 syllables but got '$kana'",
        )
    }

    @Test
    fun `assemble produces different kana for different seeds (collision rate under 5 percent over 120 seeds)`() {
        val kanas = (1..120).map { suffix ->
            KanaAssembler.assemble(
                stableHash("drug_${"%04d".format(suffix)}", slot = 0, index = 0),
                NamePattern.PATTERN_B,
            )
        }
        val unique = kanas.toSet().size
        assertTrue(
            unique >= (120 * 0.95).toInt(),
            "collision rate exceeds 5%: $unique / 120 unique",
        )
    }
}
