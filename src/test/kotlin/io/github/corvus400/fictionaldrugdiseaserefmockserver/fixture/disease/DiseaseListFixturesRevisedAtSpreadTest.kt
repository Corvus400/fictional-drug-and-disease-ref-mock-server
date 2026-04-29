package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import kotlin.test.Test
import kotlin.test.assertEquals

class DiseaseListFixturesRevisedAtSpreadTest {
    @Test
    fun `default scenario revisedAt has expected distinct spread values`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())

        val distinct = diseases.map { disease -> disease.revisedAt }.distinct()

        assertEquals(
            expected = minOf(diseases.size, DiseaseGenerator.REVISED_AT_SPREAD_DAYS),
            actual = distinct.size,
            message = "Disease revisedAt must use the full deterministic spread for Phase 12 REVISED_AT_DESC " +
                "(got ${distinct.size} distinct over ${diseases.size}). See #313 and #73 Phase 6 finding.",
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
