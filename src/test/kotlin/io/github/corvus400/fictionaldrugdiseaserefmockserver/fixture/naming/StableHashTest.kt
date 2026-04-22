package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StableHashTest {
    @Test
    fun `stableHash returns bit-identical Long for fixed input across runs`() {
        val h1 = stableHash("drug_0001", slot = 0, index = 0)
        val h2 = stableHash("drug_0001", slot = 0, index = 0)
        assertEquals(h1, h2)
        assertEquals(7_587_943_607_124_054_467L /* GOLDEN */, h1)
    }

    @Test
    fun `stableHash differs across slot and index perturbations`() {
        val base = stableHash("drug_0001", slot = 0, index = 0)
        assertNotEquals(base, stableHash("drug_0001", slot = 1, index = 0))
        assertNotEquals(base, stableHash("drug_0001", slot = 0, index = 1))
    }
}
