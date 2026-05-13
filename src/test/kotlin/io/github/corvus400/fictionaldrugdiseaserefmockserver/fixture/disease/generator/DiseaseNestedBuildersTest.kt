package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseasePlaceholderDelimiter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder.DiseaseRenderContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DiseaseNestedBuildersTest {
    private val diseaseId: String = "disease_0000"
    private val dict: DiseasePlaceholderDictionary = DiseasePlaceholderDictionary()
    private val context: DiseaseRenderContext = DiseaseRenderContext(selfName = "架空疾患テスト")

    @Test
    fun `buildSummary returns string without raw placeholder delimiters`() {
        val result = DiseaseNestedBuilders.buildSummary(id = diseaseId, dict = dict, context = context)
        assertFalse(
            DiseasePlaceholderDelimiter.OPEN in result || DiseasePlaceholderDelimiter.CLOSE in result,
            "buildSummary leaks raw placeholder: $result",
        )
    }

    @Test
    fun `buildSeverityGrading entries have placeholder-free criteria and recommendedAction`() {
        val info = DiseaseNestedBuilders.buildSeverityGrading(
            id = diseaseId,
            dict = dict,
            context = context,
        )
        val violations = info.grades.flatMap { grade ->
            listOfNotNull(
                "grade.criteria leaks placeholder: ${grade.criteria}".takeIf {
                    DiseasePlaceholderDelimiter.OPEN in grade.criteria ||
                        DiseasePlaceholderDelimiter.CLOSE in grade.criteria
                },
                "grade.recommendedAction leaks placeholder: ${grade.recommendedAction}".takeIf {
                    DiseasePlaceholderDelimiter.OPEN in grade.recommendedAction ||
                        DiseasePlaceholderDelimiter.CLOSE in grade.recommendedAction
                },
            )
        }

        assertTrue(actual = violations.isEmpty(), message = "severity grading placeholder violations: $violations")
    }

    @Test
    fun `buildPrognosis returns string without raw placeholder delimiters`() {
        val result = DiseaseNestedBuilders.buildPrognosis(id = diseaseId, dict = dict, context = context)
        assertFalse(
            DiseasePlaceholderDelimiter.OPEN in result || DiseasePlaceholderDelimiter.CLOSE in result,
            "buildPrognosis leaks raw placeholder: $result",
        )
    }
}
