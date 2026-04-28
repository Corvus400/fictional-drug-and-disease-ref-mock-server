package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter

/**
 * 疾患フィクスチャ設計図: ID シード `index` と分類軸を保持する中間表現。
 *
 * Generator 層がこの型から条件付き必須フィールドや派生値を生成する。
 * `isInfectious` と `icd10Chapter` は独立した分類軸 (感染症が必ず Chapter I に
 * 入るわけではない) で、Factory 側で分布制御の自由度を残す。
 */
data class DiseaseBlueprint(
    val index: Int,
    val icd10Chapter: Icd10Chapter,
    val chronicity: Chronicity,
    val isInfectious: Boolean,
    val isMentalDisorder: Boolean,
    val isRareDisease: Boolean,
) {
    init {
        require(index >= 0) { "index must be non-negative, got $index" }
    }
}
