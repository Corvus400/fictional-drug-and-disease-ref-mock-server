package io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import kotlinx.serialization.Serializable

/**
 * 疾患サマリ: 一覧画面用に主要分類軸のみを抜粋した軽量モデル。
 *
 * `DiseaseListResponse.items` 要素および検索結果として使用。詳細フィールドは含めず、
 * 分類軸 (`icd10Chapter` / `chronicity` / `infectious` / `medicalDepartment`) で絞り込み可能。
 */
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
