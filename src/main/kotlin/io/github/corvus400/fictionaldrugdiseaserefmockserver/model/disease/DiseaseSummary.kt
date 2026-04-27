package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import kotlinx.serialization.Serializable

@Serializable
data class DiseaseSummary(
    val id: String,
    val name: String,
    val icd10Chapter: Icd10Chapter,
    val medicalDepartment: List<MedicalDepartment>,
    val chronicity: Chronicity,
    val infectious: Boolean,
)

/**
 * `Disease` から `/diseases` 一覧用の `DiseaseSummary` を抽出する。
 *
 * Phase 11-10b で `DiseaseSearchService.applyKeyword` (Disease 全体を要求) を Module ハンドラ
 * から呼び出すため、フィルタ後の Disease を一覧 envelope に変換する経路として導入。
 */
fun Disease.toSummary(): DiseaseSummary =
    DiseaseSummary(
        id = id,
        name = name,
        icd10Chapter = icd10Chapter,
        medicalDepartment = medicalDepartment,
        chronicity = chronicity,
        infectious = infectious,
    )
