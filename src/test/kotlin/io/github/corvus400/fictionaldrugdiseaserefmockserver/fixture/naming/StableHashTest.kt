package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StableHashTest {
    @Test
    fun `stableHash returns bit-identical Long for fixed input across runs`() {
        val h1 = stableHash("drug_0001", slot = 0, index = 0)
        val h2 = stableHash("drug_0001", slot = 0, index = 0)
        assertEquals(
            h1,
            h2,
            "stableHash must return the same Long for identical input",
        )
    }

    @Test
    fun `stableHash keeps the fixed golden value`() {
        val h1 = stableHash("drug_0001", slot = 0, index = 0)

        assertEquals(
            7_587_943_607_124_054_467L /* GOLDEN */,
            h1,
            "stableHash golden value for drug_0001 slot=0 index=0 must not drift",
        )
    }

    @Test
    fun `stableHash differs when slot changes`() {
        val base = stableHash("drug_0001", slot = 0, index = 0)
        assertNotEquals(
            base,
            stableHash("drug_0001", slot = 1, index = 0),
            "stableHash must change when slot changes",
        )
    }

    @Test
    fun `stableHash differs when index changes`() {
        val base = stableHash("drug_0001", slot = 0, index = 0)
        assertNotEquals(
            base,
            stableHash("drug_0001", slot = 0, index = 1),
            "stableHash must change when index changes",
        )
    }
}
