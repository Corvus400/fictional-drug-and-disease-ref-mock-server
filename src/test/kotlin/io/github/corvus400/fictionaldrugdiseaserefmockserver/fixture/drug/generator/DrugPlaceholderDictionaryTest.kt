package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.MedicalVocabularyDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.placeholder.PlaceholderKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.stableHash
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

class DrugPlaceholderDictionaryTest {
    @Test
    fun `PlaceholderKey enum contains exactly 64 keys`() {
        assertEquals(
            PLACEHOLDER_KEY_COUNT,
            PlaceholderKey.values().size,
            "PlaceholderKey must cover exactly $PLACEHOLDER_KEY_COUNT placeholders " +
                "extracted from DrugParagraphTemplates",
        )
    }

    @Test
    fun `resolve returns non-blank value for every placeholder key`() {
        val dict = buildDict()
        PlaceholderKey.values().forEach { key ->
            val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
            val value = dict.resolve(key.jsonKey, seed)
            assertTrue(
                value.isNotBlank(),
                "resolve('${key.jsonKey}', $seed) returned blank; " +
                    "every placeholder key must yield a non-empty substitution string",
            )
        }
    }

    @Test
    fun `resolve delegates every category-A key to MedicalVocabularyDictionary`() {
        val dict = buildDict()
        val seed = stableHash(id = "drug_0001", slot = 0, index = 0)
        PlaceholderKey.values()
            .filter { it.category == PlaceholderCategory.A_MEDICAL_VOCABULARY }
            .forEach { key ->
                assertEquals(
                    MedicalVocabularyDictionary.resolve(key.jsonKey, seed),
                    dict.resolve(key.jsonKey, seed),
                    "Dictionary.resolve('${key.jsonKey}', $seed) must return the same value " +
                        "as MedicalVocabularyDictionary.resolve since this is a category-A key",
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
            makeTestDisease(id = "disease_0002", name = "架空疾患丙"),
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
        const val PLACEHOLDER_KEY_COUNT = 64
    }
}
