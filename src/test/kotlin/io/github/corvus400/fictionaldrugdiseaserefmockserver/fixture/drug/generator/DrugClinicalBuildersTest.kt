package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DrugClinicalBuildersTest {
    @Test
    fun `buildWarning paragraphs contain no raw placeholder delimiters`() {
        val dict = buildDict()
        val paragraphs = DrugClinicalBuilders.buildWarning(id = SAMPLE_ID, dict = dict)
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
        val dict = buildDict()
        val interactions = DrugClinicalBuilders.buildInteractions(id = SAMPLE_ID, dict = dict)
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

    private fun buildDict(): DrugPlaceholderDictionary =
        DrugPlaceholderDictionary(
            nameAdapter = FixmergeNameAdapter(),
            diseaseProvider = DiseaseFixtureProvider(all = diseaseFixtures()),
        )

    private fun diseaseFixtures(): List<Disease> =
        listOf(
            makeTestDisease(id = "disease_0000", name = "架空疾患甲"),
            makeTestDisease(id = "disease_0001", name = "架空疾患乙"),
        )

    private fun makeTestDisease(
        id: String,
        name: String,
    ): Disease =
        Disease(
            id = id,
            name = name,
            nameKana = "カクウシッカン",
            icd10Chapter = Icd10Chapter.CHAPTER_X,
            medicalDepartment = listOf(MedicalDepartment.INTERNAL_MEDICINE),
            chronicity = Chronicity.CHRONIC,
            infectious = false,
            summary = "テスト用の架空疾患です。",
            etiology = "テスト用の病因です。",
            symptoms = SymptomInfo(mainSymptoms = listOf("テスト症状")),
            diagnosticCriteria = DiagnosticCriteriaInfo(required = listOf("テスト診断基準")),
            treatments = TreatmentInfo(),
            revisedAt = "2026/01/01",
        )

    private companion object {
        const val SAMPLE_ID: String = "drug_0001"
    }
}
