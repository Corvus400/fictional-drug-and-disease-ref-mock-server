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
    ): List<Drug> = TODO(
        "Phase 11-3b 以降で triangulation する: keyword=$keyword, match=$match, target=$target, items=${items.size}",
    )
}
