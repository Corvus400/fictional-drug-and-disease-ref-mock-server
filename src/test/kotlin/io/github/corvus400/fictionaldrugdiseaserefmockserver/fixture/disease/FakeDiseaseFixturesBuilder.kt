package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.DiagnosticCriteriaInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.SymptomInfo
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.nested.TreatmentInfo

class FakeDiseaseFixturesBuilder {
    private val diseasesByChapter: MutableMap<Icd10Chapter, List<String>> = mutableMapOf()

    fun withChapter(
        chapter: Icd10Chapter,
        ids: List<String>,
    ): FakeDiseaseFixturesBuilder {
        diseasesByChapter[chapter] = ids
        return this
    }

    fun build(): List<Disease> =
        diseasesByChapter.flatMap { (chapter, ids) ->
            ids.map { id -> makeDisease(id = id, chapter = chapter) }
        }

    private fun makeDisease(
        id: String,
        chapter: Icd10Chapter,
    ): Disease =
        Disease(
            id = id,
            name = "テスト疾患$id",
            nameKana = "テストシッカン",
            icd10Chapter = chapter,
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
}
