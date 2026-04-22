package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint

import kotlin.test.Test
import kotlin.test.assertEquals

class DrugBlueprintFactoryTest {
    @Test
    fun `factory produces exactly 120 blueprints`() {
        val blueprints = DrugBlueprintFactory.build()
        assertEquals(120, blueprints.size)
    }

    @Test
    fun `factory distributes blueprints according to ATC first letter counts`() {
        val blueprints = DrugBlueprintFactory.build()
        val distribution = blueprints.groupBy { it.atcFirstLetter }.mapValues { it.value.size }
        val expected =
            mapOf(
                'A' to 20,
                'B' to 6,
                'C' to 20,
                'D' to 8,
                'G' to 4,
                'H' to 4,
                'J' to 18,
                'L' to 4,
                'M' to 3,
                'N' to 18,
                'P' to 1,
                'R' to 12,
                'S' to 1,
                'V' to 1,
            )
        assertEquals(expected, distribution)
    }

    @Test
    fun `each blueprint has a unique sequential id derivable from index in drug_NNNN format`() {
        val blueprints = DrugBlueprintFactory.build()
        val expectedIds = (1..120).map { "drug_%04d".format(it) }
        val actualIds = blueprints.map { "drug_%04d".format(it.index + 1) }
        assertEquals(expectedIds, actualIds)
    }
}
