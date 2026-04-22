package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MedicalVocabularyDictionaryTest {
    @Test
    fun `resolve returns non-empty string for every category-A key`() {
        CATEGORY_A_KEYS.forEach { key ->
            val seed = stableHash(id = key, slot = 0, index = 0)
            val value = MedicalVocabularyDictionary.resolve(key, seed)
            assertTrue(
                value.isNotBlank(),
                "resolve('$key', $seed) returned blank; every category-A key must yield non-empty vocabulary",
            )
        }
    }

    @Test
    fun `resolve is deterministic for identical key and seed`() {
        val key = "symptom"
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        val first = MedicalVocabularyDictionary.resolve(key, seed)
        val second = MedicalVocabularyDictionary.resolve(key, seed)
        assertEquals(first, second)
    }

    @Test
    fun `resolve yields at least three distinct values across many seeds per key`() {
        CATEGORY_A_KEYS.forEach { key ->
            val values =
                (0 until SEED_SAMPLE_SIZE)
                    .map { index -> stableHash(id = key, slot = 1, index = index) }
                    .map { seed -> MedicalVocabularyDictionary.resolve(key, seed) }
                    .toSet()
            assertTrue(
                values.size >= MIN_ENTRY_COUNT,
                "resolve('$key', ...) produced only ${values.size} distinct value(s) across $SEED_SAMPLE_SIZE seeds; " +
                    "expected at least $MIN_ENTRY_COUNT (vocabulary list must contain >= $MIN_ENTRY_COUNT entries)",
            )
        }
    }

    private companion object {
        const val SEED_SAMPLE_SIZE = 200
        const val MIN_ENTRY_COUNT = 3

        val CATEGORY_A_KEYS =
            listOf(
                "action",
                "adverseReaction",
                "ageGroup",
                "comorbidity",
                "countermeasure",
                "drugCategory",
                "effect",
                "endpoint",
                "enzyme",
                "exam",
                "excretionRoute",
                "frequencyBand",
                "hepaticLevel",
                "insuranceRule",
                "ionizationForm",
                "labResult",
                "mechanism",
                "modelName",
                "modelType",
                "pathway",
                "populationCategory",
                "postMarketingPlan",
                "renalLevel",
                "route",
                "solventPolarity",
                "studyDuration",
                "symptom",
                "tissueType",
                "trainingProgram",
            )
    }
}
