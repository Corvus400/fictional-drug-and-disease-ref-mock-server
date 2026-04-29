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
            expected = minOf(drugs.size, DrugGenerator.REVISED_AT_SPREAD_DAYS),
            actual = distinct.size,
            message = "Drug revisedAt must use the full deterministic spread for Phase 12 REVISED_AT_DESC " +
                "(got ${distinct.size} distinct over ${drugs.size}). See #313 and #73 Phase 6 finding.",
        )
    }
}
