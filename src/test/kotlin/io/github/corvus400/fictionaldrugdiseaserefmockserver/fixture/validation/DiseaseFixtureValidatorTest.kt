package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
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
    fun `validate detects sequential-id gap when an id is replaced with an out-of-range id`() {
        val original = fullInventory[2]
        val injected = original.copy(id = "disease_0080")
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertTrue(
            actual = violations.any { violation ->
                violation.field == "id" &&
                    violation.message.contains(other = "sequential") &&
                    violation.diseaseId == original.id
            },
            message = "expected sequential-id missing violation for ${original.id} " +
                "but got $violations",
        )
        assertTrue(
            actual = violations.any { violation ->
                violation.field == "id" &&
                    violation.message.contains(other = "out of range") &&
                    violation.diseaseId == "disease_0080"
            },
            message = "expected out-of-range violation for disease_0080 but got $violations",
        )
    }

    @Test
    fun `validate detects duplicate id violation when two diseases share the same id`() {
        val firstDisease = fullInventory[0]
        val secondDisease = fullInventory[1]
        val injected = secondDisease.copy(id = firstDisease.id)
        val diseases = listOf(firstDisease, injected) + fullInventory.drop(n = 2)

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertTrue(
            actual = violations.any { violation ->
                violation.diseaseId == firstDisease.id &&
                    violation.field == "id" &&
                    violation.message.contains(other = "unique")
            },
            message = "expected duplicate-id violation for ${firstDisease.id} but got $violations",
        )
    }

    @Test
    fun `validate detects CHAPTER_V fewer-than-three mainSymptoms violation on an injected disease`() {
        val original = fullInventory.first { disease ->
            disease.icd10Chapter == Icd10Chapter.CHAPTER_V
        }
        val injected = original.copy(
            symptoms = original.symptoms.copy(
                mainSymptoms = original.symptoms.mainSymptoms.take(n = 2),
            ),
        )
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertTrue(
            actual = violations.any { violation ->
                violation.diseaseId == original.id &&
                    violation.field == "symptoms.mainSymptoms" &&
                    violation.message.contains(other = "CHAPTER_V")
            },
            message = "expected CHAPTER_V mainSymptoms<3 violation for ${original.id} " +
                "but got $violations",
        )
    }

    @Test
    fun `validate detects CHAPTER_II missing severityGrading violation on an injected disease`() {
        val original = fullInventory.first { disease ->
            disease.icd10Chapter == Icd10Chapter.CHAPTER_II
        }
        val injected = original.copy(severityGrading = null)
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertTrue(
            actual = violations.any { violation ->
                violation.diseaseId == original.id && violation.field == "severityGrading"
            },
            message = "expected CHAPTER_II severityGrading=null violation for ${original.id} " +
                "but got $violations",
        )
    }

    @Test
    fun `validate detects CHAPTER_I non-infectious violation on an injected disease`() {
        val original = fullInventory.first { disease ->
            disease.icd10Chapter == Icd10Chapter.CHAPTER_I
        }
        val injected = original.copy(infectious = false)
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertTrue(
            actual = violations.any { violation ->
                violation.diseaseId == original.id && violation.field == "infectious"
            },
            message = "expected CHAPTER_I infectious=false violation for ${original.id} " +
                "but got $violations",
        )
    }

    @Test
    fun `validate detects requiredExams empty violation on an injected disease`() {
        val original = fullInventory.first()
        val injected = original.copy(requiredExams = emptyList())
        val diseases = listOf(injected) + fullInventory.drop(n = 1)

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertTrue(
            actual = violations.any { violation ->
                violation.diseaseId == original.id && violation.field == "requiredExams"
            },
            message = "expected requiredExams-empty violation for ${original.id} " +
                "but got $violations",
        )
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
