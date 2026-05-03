package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.validation

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.EpidemiologyInfo
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

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "id",
                message = "sequential id missing from 0..79",
            ),
        )
        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = "disease_0080",
                field = "id",
                message = "sequential id out of range 0..79",
            ),
        )
    }

    @Test
    fun `validate detects duplicate id violation when two diseases share the same id`() {
        val firstDisease = fullInventory[0]
        val secondDisease = fullInventory[1]
        val injected = secondDisease.copy(id = firstDisease.id)
        val diseases = listOf(firstDisease, injected) + fullInventory.drop(n = 2)

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = firstDisease.id,
                field = "id",
                message = "id must be unique but appears 2 times",
            ),
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

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "symptoms.mainSymptoms",
                message = "CHAPTER_V disease must have at least 3 mainSymptoms",
            ),
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

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "severityGrading",
                message = "CHAPTER_II disease must have severityGrading populated",
            ),
        )
    }

    @Test
    fun `validate detects severityGrading grades fewer-than-two violation on an injected disease`() {
        val original = fullInventory.first { disease ->
            disease.severityGrading != null
        }
        val injected = original.copy(
            severityGrading = original.severityGrading?.copy(
                grades = original.severityGrading.grades.take(n = 1),
            ),
        )
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "severityGrading.grades",
                message = "severityGrading.grades must have at least 2 entries when severityGrading is non-null",
            ),
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

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "infectious",
                message = "CHAPTER_I disease must have infectious=true",
            ),
        )
    }

    @Test
    fun `validate detects CHAPTER_I missing onsetPattern violation on an injected disease`() {
        val original = fullInventory.first { disease ->
            disease.icd10Chapter == Icd10Chapter.CHAPTER_I
        }
        val injected = original.copy(
            symptoms = original.symptoms.copy(onsetPattern = null),
        )
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "symptoms.onsetPattern",
                message = "CHAPTER_I disease must have non-null onsetPattern",
            ),
        )
    }

    @Test
    fun `validate detects CHAPTER_I empty riskFactors violation on an injected disease`() {
        val original = fullInventory.first { disease ->
            disease.icd10Chapter == Icd10Chapter.CHAPTER_I
        }
        val injected = original.copy(
            epidemiology = original.epidemiology?.copy(riskFactors = emptyList()),
        )
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "epidemiology.riskFactors",
                message = "CHAPTER_I disease must have non-empty riskFactors",
            ),
        )
    }

    @Test
    fun `validate detects CHAPTER_I riskFactors without infection route keyword violation on an injected disease`() {
        val original = fullInventory.first { disease ->
            disease.icd10Chapter == Icd10Chapter.CHAPTER_I
        }
        val injected = original.copy(
            epidemiology = original.epidemiology?.copy(riskFactors = listOf("家族歴", "喫煙")),
        )
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "epidemiology.riskFactors",
                message = "CHAPTER_I disease riskFactors must include an infection route keyword",
            ),
        )
    }

    @Test
    fun `validate detects empty prevention violation on an infectious disease`() {
        val original = fullInventory.first { disease ->
            disease.infectious && disease.prevention.isNotEmpty()
        }
        val injected = original.copy(prevention = emptyList())
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "prevention",
                message = "infectious=true or non-empty riskFactors requires prevention size >= 1",
            ),
        )
    }

    @Test
    fun `validate detects empty prevention violation on a disease with risk factors`() {
        val original = fullInventory.first { disease ->
            !disease.infectious
        }
        val injected = original.copy(
            epidemiology = EpidemiologyInfo(riskFactors = listOf("生活習慣")),
            prevention = emptyList(),
        )
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "prevention",
                message = "infectious=true or non-empty riskFactors requires prevention size >= 1",
            ),
        )
    }

    @Test
    fun `validate detects requiredExams empty violation on an injected disease`() {
        val original = fullInventory.first()
        val injected = original.copy(requiredExams = emptyList())
        val diseases = listOf(injected) + fullInventory.drop(n = 1)

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "requiredExams",
                message = "requiredExams must have at least 1 entry",
            ),
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

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "symptoms.mainSymptoms",
                message = "mainSymptoms must have at least 1 entry",
            ),
        )
    }

    @Test
    fun `validate detects diagnosticCriteria required empty violation on a non-CHAPTER_V disease`() {
        val original = fullInventory.first { disease ->
            disease.icd10Chapter != Icd10Chapter.CHAPTER_V
        }
        val injected = original.copy(
            diagnosticCriteria = original.diagnosticCriteria.copy(required = emptyList()),
        )
        val diseases = fullInventory.map { disease ->
            if (disease.id == original.id) injected else disease
        }

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "diagnosticCriteria.required",
                message = "diagnosticCriteria.required must have at least 1 entry",
            ),
        )
    }

    @Test
    fun `validate detects medicalDepartment empty violation on an injected disease`() {
        val original = fullInventory.first()
        val injected = original.copy(medicalDepartment = emptyList())
        val diseases = listOf(injected) + fullInventory.drop(n = 1)

        val violations = DiseaseFixtureValidator.validate(diseases = diseases)

        assertContainsFixtureViolation(
            violations = violations,
            expected = FixtureViolation(
                entityType = ENTITY_TYPE_DISEASE,
                entityId = original.id,
                field = "medicalDepartment",
                message = "medicalDepartment must have at least 1 entry",
            ),
        )
    }

    private fun generateFullInventory(): List<Disease> {
        val generator = DiseaseGenerator(
            adapter = FixmergeNameAdapter(),
            placeholderDictionary = DiseasePlaceholderDictionary(),
        )
        return generator.generate(blueprints = DiseaseBlueprintFactory.build())
    }

    private companion object {
        const val ENTITY_TYPE_DISEASE: String = "disease"

        fun assertContainsFixtureViolation(
            violations: List<*>,
            expected: FixtureViolation,
        ) {
            assertTrue(
                actual = violations.any { violation -> violation == expected },
                message = "expected $expected to be present but got $violations",
            )
        }
    }
}
