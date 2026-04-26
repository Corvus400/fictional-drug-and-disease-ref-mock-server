package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug

/**
 * 医薬品の検索ロジックを提供する純関数オブジェクト。
 *
 * 副作用なし・シナリオ非依存。`scenarioManager` / `FixtureProvider` / `getByScenario` には
 * 依存しない。呼び出し側 (route 層) が解決済みの `items` を渡し、本オブジェクトは
 * クエリパラメータに従って絞り込みのみを行う。
 */
object DrugSearchService {
    /**
     * `keyword` が null/空白なら `items` を素通しする (no-op)。
     * 検索ロジック本体は後続フェーズで triangulation により段階的に追加する。
     */
    fun applyKeyword(
        items: List<Drug>,
        keyword: String?,
        match: KeywordMatch,
        target: DrugKeywordTarget,
    ): List<Drug> {
        if (keyword.isNullOrBlank()) {
            return items
        }
        return filterByKeyword(items = items, keyword = keyword, match = match, target = target)
    }

    private fun filterByKeyword(
        items: List<Drug>,
        keyword: String,
        match: KeywordMatch,
        target: DrugKeywordTarget,
    ): List<Drug> = items.filter { drug ->
        when (target) {
            DrugKeywordTarget.GENERIC, DrugKeywordTarget.BRAND ->
                fieldsFor(target = target, drug = drug).any { field ->
                    matches(field = field, keyword = keyword, match = match)
                }
            DrugKeywordTarget.BOTH -> true
        }
    }

    /**
     * 検索対象 (`target`) に応じて、`drug` のうちキーワード照合に用いるフィールド集合を返す。
     * いずれか 1 フィールドが一致すれば該当扱い (OR 結合)。`BOTH` は後続フェーズで filter 側に統合する伏線。
     */
    private fun fieldsFor(
        target: DrugKeywordTarget,
        drug: Drug,
    ): List<String> = when (target) {
        DrugKeywordTarget.GENERIC -> listOf(drug.genericName)
        DrugKeywordTarget.BRAND -> listOf(drug.brandName, drug.brandNameKana)
        DrugKeywordTarget.BOTH -> listOf(drug.genericName, drug.brandName, drug.brandNameKana)
    }

    /**
     * 単一フィールドに対するキーワード一致判定。`match` の差し替えで PREFIX 対応を後続 triangulation で
     * 段階導入できるよう抽出している。
     */
    private fun matches(
        field: String,
        keyword: String,
        match: KeywordMatch,
    ): Boolean = when (match) {
        KeywordMatch.PARTIAL -> field.contains(keyword)
        KeywordMatch.PREFIX -> field.startsWith(keyword)
    }
}
