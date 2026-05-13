package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.common

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ValueRangeGeneratorTest {
    @Test
    fun `pickCount is deterministic for identical seed and range`() {
        val seed = stableHash("drug_0001", slot = 0, index = 0)
        val first = ValueRangeGenerator.pickCount(seed, 1..3)
        val second = ValueRangeGenerator.pickCount(seed, 1..3)
        assertEquals(first, second)
    }

    @Test
    fun `pickCount always lands within the given range across 1000 seeds`() {
        val range = 1..3
        repeat(ITERATIONS) { iteration ->
            val seed = stableHash("seed_case", slot = 0, index = iteration)
            val value = ValueRangeGenerator.pickCount(seed, range)
            assertTrue(
                value in range,
                "pickCount($seed, $range) = $value is outside $range",
            )
        }
    }

    @Test
    fun `pickInRange always lands within the given range across 1000 seeds`() {
        val range = -5..10
        repeat(ITERATIONS) { iteration ->
            val seed = stableHash("range_case", slot = 1, index = iteration)
            val value = ValueRangeGenerator.pickInRange(seed, range)
            assertTrue(
                value in range,
                "pickInRange($seed, $range) = $value is outside $range",
            )
        }
    }

    @Test
    fun `pickOne is deterministic for identical seed and candidates`() {
        val candidates = listOf("x", "y", "z")
        val seed = stableHash("drug_0007", slot = 2, index = 0)
        val first = ValueRangeGenerator.pickOne(seed, candidates)
        val second = ValueRangeGenerator.pickOne(seed, candidates)
        assertEquals(first, second, "pickOne must return the same candidate for the same seed")
    }

    @Test
    fun `pickOne returns an element of the candidate list`() {
        val candidates = listOf("x", "y", "z")
        val seed = stableHash("drug_0007", slot = 2, index = 0)
        val first = ValueRangeGenerator.pickOne(seed, candidates)
        assertTrue(
            first in candidates,
            "pickOne returned '$first' which is not in $candidates",
        )
    }

    @Test
    fun `pickOne rejects empty candidate list with IllegalArgumentException`() {
        assertFailsWith<IllegalArgumentException> {
            ValueRangeGenerator.pickOne(seed = 0L, candidates = emptyList<String>())
        }
    }

    private companion object {
        const val ITERATIONS = 1000
    }
}
