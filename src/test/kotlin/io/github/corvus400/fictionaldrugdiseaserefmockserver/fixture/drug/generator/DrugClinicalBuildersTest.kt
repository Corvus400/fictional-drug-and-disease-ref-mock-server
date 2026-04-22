package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DrugClinicalBuildersTest {
    @Test
    fun `buildWarning paragraphs contain no raw placeholder delimiters`() {
        val paragraphs = DrugClinicalBuilders.buildWarning(id = SAMPLE_ID)
        assertTrue(paragraphs.isNotEmpty(), "buildWarning must return at least one paragraph")
        paragraphs.forEach { paragraph ->
            assertFalse(
                actual = "{{" in paragraph.content || "}}" in paragraph.content,
                message =
                    "buildWarning paragraph must contain no raw '{{...}}' after Dictionary wiring; " +
                        "got content='${paragraph.content}'",
            )
        }
    }

    @Test
    fun `buildInteractions entries contain no raw placeholder delimiters`() {
        val interactions = DrugClinicalBuilders.buildInteractions(id = SAMPLE_ID)
        val allEntries = interactions.combinationProhibited + interactions.combinationCaution
        assertTrue(allEntries.isNotEmpty(), "buildInteractions must return at least one entry")
        allEntries.forEach { entry ->
            assertFalse(
                actual = "{{" in entry.clinicalSymptom || "}}" in entry.clinicalSymptom,
                message =
                    "buildInteractions clinicalSymptom must contain no raw '{{...}}'; " +
                        "got='${entry.clinicalSymptom}'",
            )
            assertFalse(
                actual = "{{" in entry.mechanism || "}}" in entry.mechanism,
                message =
                    "buildInteractions mechanism must contain no raw '{{...}}'; " +
                        "got='${entry.mechanism}'",
            )
        }
    }

    private companion object {
        const val SAMPLE_ID: String = "drug_0001"
    }
}
