package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

object DiseaseSearchService {
    /**
     * `keyword` を `target` に対応するフィールド集合に適用してフィルタする。
     *
     * ## 大文字小文字の扱い
     * 内部で `String.contains` を使用しており Kotlin 既定の **case-sensitive** で比較する。
     * `target=NAME_ENGLISH` でも英語の大文字小文字を区別する (例: `keyword="hyper"` は
     * `nameEnglish="Hypertension"` にヒットしない)。アプリ仕様での明示要件が無いため
     * 既定挙動を採用 (要件確定時は後続 issue で再検討)。
     */
    fun applyKeyword(
        items: List<Disease>,
        keyword: String?,
        match: KeywordMatch,
        target: DiseaseKeywordTarget,
    ): List<Disease> {
        if (keyword.isNullOrBlank()) return items
        return when (match) {
            KeywordMatch.PARTIAL ->
                items.filter { disease ->
                    fieldsFor(target = target, disease = disease).any { field ->
                        field.contains(keyword)
                    }
                }
            KeywordMatch.PREFIX -> items
        }
    }

    /**
     * `target` に対応する検索対象フィールドの集合を返す。
     * `match=PARTIAL` のフィルタ評価および後続フェーズの `PREFIX` 評価で共有される。
     */
    private fun fieldsFor(
        target: DiseaseKeywordTarget,
        disease: Disease,
    ): List<String> =
        when (target) {
            DiseaseKeywordTarget.NAME -> listOf(disease.name, disease.nameKana)
            DiseaseKeywordTarget.NAME_ENGLISH -> listOfNotNull(disease.nameEnglish)
            DiseaseKeywordTarget.SYNONYMS -> disease.synonyms
        }
}
