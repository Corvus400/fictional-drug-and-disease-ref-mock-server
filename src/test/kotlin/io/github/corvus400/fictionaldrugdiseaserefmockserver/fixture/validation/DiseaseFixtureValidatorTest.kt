package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DiseaseFixtureValidatorTest {
    private val fullInventory: List<Disease> = generateFullInventory()

    @Test
    fun `validate returns empty violations for the full 80-disease factory inventory`() {
        val violations = DiseaseFixtureValidator.validate(diseases = fullInventory)
        assertEquals(emptyList(), violations)
    }

    @Test
    fun `validate detects mainSymptoms empty violation on an injected disease`() {
        val original = fullInventory.first()
        val injected = original.copy(
            symptoms = original.symptoms.copy(mainSymptoms = emptyList()),
        )
        val diseases = listOf(injected) + fullInventory.drop(n = 1)

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertTrue(
            actual = violations.any { violation ->
                violation.diseaseId == original.id && violation.field == "symptoms.mainSymptoms"
            },
            message = "expected mainSymptoms-empty violation for ${original.id} " +
                "but got $violations",
        )
    }

    private fun generateFullInventory(): List<Disease> {
        val generator = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        )
        return generator.generate(blueprints = DiseaseBlueprintFactory.build())
    }
}
