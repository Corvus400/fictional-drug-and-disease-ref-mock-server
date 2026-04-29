package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import kotlin.test.Test
import kotlin.test.assertTrue

class DiseaseListFixturesRevisedAtSpreadTest {
    @Test
    fun `default scenario revisedAt has more than one distinct value`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())

        val distinct = diseases.map { disease -> disease.revisedAt }.distinct()

        assertTrue(
            actual = distinct.size > 1,
            message = "Disease revisedAt must vary across fixtures for Phase 12 REVISED_AT_DESC " +
                "(got ${distinct.size} distinct over ${diseases.size}). See #73 Phase 6 finding.",
        )
    }

    private companion object {
        fun buildFreshGenerator(): DiseaseGenerator =
            DiseaseGenerator(
                adapter = FixmergeNameAdapter(),
                placeholderDictionary = DiseasePlaceholderDictionary(),
            )
    }
}
