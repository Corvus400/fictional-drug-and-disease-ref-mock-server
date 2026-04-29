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
            expected = diseases.size,
            actual = distinct.size,
            message =
            "Disease fixtures must be fully unique within SPREAD=${DiseaseGenerator.REVISED_AT_SPREAD_DAYS} " +
                "for #313 (got ${distinct.size} distinct over ${diseases.size}).",
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
