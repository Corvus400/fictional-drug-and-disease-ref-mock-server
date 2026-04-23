package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderDelimiter
import kotlin.test.Test
import kotlin.test.assertFalse

class DiseaseNestedBuildersTest {
    private val diseaseId: String = "disease_0000"

    @Test
    fun `buildSummary returns string without raw placeholder delimiters`() {
        val result = DiseaseNestedBuilders.buildSummary(id = diseaseId)
        assertFalse(
            DiseasePlaceholderDelimiter.OPEN in result || DiseasePlaceholderDelimiter.CLOSE in result,
            "buildSummary leaks raw placeholder: $result",
        )
    }

    @Test
    fun `buildSeverityGrading entries have placeholder-free criteria and recommendedAction`() {
        val info = DiseaseNestedBuilders.buildSeverityGrading(id = diseaseId)
        for (grade in info.grades) {
            assertFalse(
                DiseasePlaceholderDelimiter.OPEN in grade.criteria ||
                    DiseasePlaceholderDelimiter.CLOSE in grade.criteria,
                "grade.criteria leaks placeholder: ${grade.criteria}",
            )
            assertFalse(
                DiseasePlaceholderDelimiter.OPEN in grade.recommendedAction ||
                    DiseasePlaceholderDelimiter.CLOSE in grade.recommendedAction,
                "grade.recommendedAction leaks placeholder: ${grade.recommendedAction}",
            )
        }
    }

    @Test
    fun `buildPrognosis returns string without raw placeholder delimiters`() {
        val result = DiseaseNestedBuilders.buildPrognosis(id = diseaseId)
        assertFalse(
            DiseasePlaceholderDelimiter.OPEN in result || DiseasePlaceholderDelimiter.CLOSE in result,
            "buildPrognosis leaks raw placeholder: $result",
        )
    }
}
