package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.PrecautionPopulationCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugKeywordTarget
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.KeywordMatch

/**
 * `/drugs` 一覧エンドポイントの絞り込み条件。
 *
 * Phase 11-10a でフィルタが 8 個を超え detekt `LongParameterList` (max 8) に抵触したため、
 * `DrugListFixtures.resolve` のフィルタ系引数を本クラスに集約する。
 * 既存呼び出し側からは `DrugListQuery()` (全 null/既定) を渡せば従来同様のフィルタなし挙動。
 */
data class DrugListQuery(
    val atcPrefix: String? = null,
    val regulatoryClassSerialName: String? = null,
    val routeOfAdministrationSerialName: String? = null,
    val dosageFormSerialName: String? = null,
    val categoryName: String? = null,
    val keyword: String? = null,
    val keywordMatch: KeywordMatch = KeywordMatch.PARTIAL,
    val keywordTarget: DrugKeywordTarget = DrugKeywordTarget.BOTH,
    val adverseReactionKeyword: String? = null,
    val precautionCategories: List<PrecautionPopulationCategory> = emptyList(),
)
