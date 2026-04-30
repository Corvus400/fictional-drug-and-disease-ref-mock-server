package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixturesTestSupport.buildFreshGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

class DrugListFixturesRevisedAtSpreadTest {
    @Test
    fun `default scenario revisedAt has expected distinct spread values`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val distinct = drugs.map { drug -> drug.revisedAt }.distinct()

        assertEquals(
            expected = DrugGenerator.REVISED_AT_SPREAD_DAYS,
            actual = distinct.size,
            message = "Drug 120 fixtures must use SPREAD=90 as designed for #313 " +
                "(got ${distinct.size} distinct over ${drugs.size}). " +
                "Tie-break determinism is covered by Phase 12 #118.",
        )
    }
}
