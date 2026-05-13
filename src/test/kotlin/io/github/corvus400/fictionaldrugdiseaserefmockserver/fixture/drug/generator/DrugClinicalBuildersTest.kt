package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DrugClinicalBuildersTest {
    @Test
    fun `buildWarning returns at least one paragraph`() {
        val dict = buildDict()
        val paragraphs = DrugClinicalBuilders.buildWarning(id = SAMPLE_ID, dict = dict)

        assertTrue(actual = paragraphs.isNotEmpty(), message = "buildWarning must return at least one paragraph")
    }

    @Test
    fun `buildWarning paragraphs contain no raw placeholder delimiters`() {
        val dict = buildDict()
        val paragraphs = DrugClinicalBuilders.buildWarning(id = SAMPLE_ID, dict = dict)

        assertEquals(
            expected = null,
            actual = paragraphs.firstOrNull { "{{" in it.content || "}}" in it.content }?.content,
            message = "buildWarning paragraph must contain no raw '{{...}}' after Dictionary wiring",
        )
    }

    @Test
    fun `buildInteractions returns at least one entry`() {
        val dict = buildDict()
        val interactions = DrugClinicalBuilders.buildInteractions(id = SAMPLE_ID, dict = dict)
        val allEntries = interactions.combinationProhibited + interactions.combinationCaution

        assertTrue(actual = allEntries.isNotEmpty(), message = "buildInteractions must return at least one entry")
    }

    @Test
    fun `buildInteractions clinical symptoms contain no raw placeholder delimiters`() {
        val dict = buildDict()
        val interactions = DrugClinicalBuilders.buildInteractions(id = SAMPLE_ID, dict = dict)
        val allEntries = interactions.combinationProhibited + interactions.combinationCaution

        assertEquals(
            expected = null,
            actual = allEntries.firstOrNull { "{{" in it.clinicalSymptom || "}}" in it.clinicalSymptom }
                ?.clinicalSymptom,
            message = "buildInteractions clinicalSymptom must contain no raw '{{...}}'",
        )
    }

    @Test
    fun `buildInteractions mechanisms contain no raw placeholder delimiters`() {
        val dict = buildDict()
        val interactions = DrugClinicalBuilders.buildInteractions(id = SAMPLE_ID, dict = dict)
        val allEntries = interactions.combinationProhibited + interactions.combinationCaution

        assertEquals(
            expected = null,
            actual = allEntries.firstOrNull { "{{" in it.mechanism || "}}" in it.mechanism }?.mechanism,
            message = "buildInteractions mechanism must contain no raw '{{...}}'",
        )
    }

    private fun buildDict(): DrugPlaceholderDictionary =
        DrugPlaceholderDictionary(
            nameAdapter = FixmergeNameAdapter(),
            diseases = diseaseFixtures(),
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
            revisedAt = "2026-01-01",
        )

    private companion object {
        const val SAMPLE_ID: String = "drug_0001"
    }
}
