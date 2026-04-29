package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import kotlin.test.Test
import kotlin.test.assertTrue

class DiseaseListFixturesPreconditionTest {
    @Test
    fun `allDiseases has at least 1 with severityGrading != null`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())

        assertTrue(
            actual = diseases.any { it.severityGrading != null },
            message = "Phase 13 has_severity_grading=true Red premise: " +
                "at least 1 disease must populate severityGrading",
        )
    }

    @Test
    fun `allDiseases has at least 1 with severityGrading == null`() {
        val diseases = buildFreshGenerator().generate(blueprints = DiseaseBlueprintFactory.build())

        assertTrue(
            actual = diseases.any { it.severityGrading == null },
            message = "Phase 13 has_severity_grading=false Red premise: " +
                "at least 1 disease must leave severityGrading as default null",
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
