package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixturesTestSupport.buildFreshGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import kotlin.test.Test
import kotlin.test.assertTrue

class DrugListFixturesRevisedAtSpreadTest {
    @Test
    fun `default scenario revisedAt has more than one distinct value`() {
        val drugs = buildFreshGenerator().generate(blueprints = DrugBlueprintFactory.build())

        val distinct = drugs.map { drug -> drug.revisedAt }.distinct()

        assertTrue(
            actual = distinct.size > 1,
            message = "Drug revisedAt must vary across fixtures for Phase 12 REVISED_AT_DESC " +
                "(got ${distinct.size} distinct over ${drugs.size}). See #73 Phase 6 finding.",
        )
    }
}
