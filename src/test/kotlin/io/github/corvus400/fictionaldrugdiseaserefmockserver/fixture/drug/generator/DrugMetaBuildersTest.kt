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
import kotlin.test.assertNotNull

class DrugMetaBuildersTest {
    @Test
    fun `buildPharmacokinetics fields contain no raw placeholder delimiters`() {
        val dict = buildDict()
        val pk = DrugMetaBuilders.buildPharmacokinetics(id = SAMPLE_ID, dict = dict)
        val textFields =
            listOf(
                "bloodConcentration" to pk.bloodConcentration,
                "absorption" to pk.absorption,
                "distribution" to pk.distribution,
                "metabolism" to pk.metabolism,
                "excretion" to pk.excretion,
            )
        textFields.forEach { (fieldName, nullableValue) ->
            val value = assertNotNull(nullableValue, "buildPharmacokinetics.$fieldName must be non-null")
            assertFalse(
                actual = "{{" in value || "}}" in value,
                message =
                    "buildPharmacokinetics.$fieldName must contain no raw '{{...}}' after " +
                        "Dictionary wiring; got='$value'",
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
