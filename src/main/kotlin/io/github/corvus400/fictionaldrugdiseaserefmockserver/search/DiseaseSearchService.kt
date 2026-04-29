package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern

object DiseaseSearchService {
    /**
     * `keyword` を `target` に対応するフィールド集合に適用してフィルタする。
     *
     * ## 大文字小文字の扱い
     * 内部で `String.contains` / `String.startsWith` を使用しており Kotlin 既定の **case-sensitive** で比較する。
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
        val tokens = keyword.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return items
        return items.filter { disease ->
            matchesAllTokens(
                fields = fieldsFor(target = target, disease = disease),
                tokens = tokens,
                match = match,
            )
        }
    }

    /**
     * 複数トークンを `fields` に対して評価する: per-token は OR across fields、トークン間は AND。
     * `target=SYNONYMS` のような list 系フィールドでも、要素跨ぎでトークンが満たされれば true。
     */
    private fun matchesAllTokens(
        fields: List<String>,
        tokens: List<String>,
        match: KeywordMatch,
    ): Boolean =
        tokens.all { token ->
            fields.any { field ->
                matches(field = field, keyword = token, match = match)
            }
        }

    /**
     * `target` に対応する検索対象フィールドの集合を返す。
     * `match=PARTIAL` / `match=PREFIX` どちらの評価でも共有される。
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

    /**
     * 単一フィールドに対するキーワード一致判定。
     * `match=PARTIAL` は contains、`match=PREFIX` は startsWith で評価する。
     */
    private fun matches(
        field: String,
        keyword: String,
        match: KeywordMatch,
    ): Boolean =
        when (match) {
            KeywordMatch.PARTIAL -> field.contains(keyword)
            KeywordMatch.PREFIX -> field.startsWith(keyword)
        }

    /**
     * `sort` キーに従って `items` を並び替える純関数。
     * 既定キー `REVISED_AT_DESC` は最終改訂日 (`revisedAt`) の降順で並び替え、
     * 同 `revisedAt` 衝突時は `id` 降順で tie-break する (#118 と対称な API 契約)。
     * 他キーは後続サイクルで triangulation により段階的に拡張する。
     */
    fun applySort(
        items: List<Disease>,
        sort: DiseaseSortKey,
    ): List<Disease> = when (sort) {
        DiseaseSortKey.REVISED_AT_DESC ->
            items.sortedWith(
                compareByDescending<Disease> { it.revisedAt }
                    .thenByDescending { it.id },
            )
        DiseaseSortKey.NAME_KANA_ASC -> items.sortedBy { it.nameKana }
        DiseaseSortKey.ICD10_CHAPTER_ASC -> items.sortedBy { it.icd10Chapter.ordinal }
    }

    /**
     * 追加フィルタ — `keyword` / `sort` 後段に適用される副次的な絞り込み群。
     *
     * 本フェーズ (Phase 13-12) までで `symptomKeyword` と `onsetPatterns` を受け付ける。
     * `onsetPatterns` はクエリ層で 0/1 個に解決される単値フィルタ前提だが、本層では
     * `DrugSearchService.applyAdditionalFilters` の `precautionCategories` と同じく
     * 集合扱いにして将来の複数値拡張に備える。引数無指定時は素通しする純関数。
     * 後続フェーズで `examCategory` / `hasPharmacologicalTreatment` /
     * `hasSeverityGrading` を triangulation で順次追加する (issue #74)。
     */
    fun applyAdditionalFilters(
        items: List<Disease>,
        symptomKeyword: String? = null,
        onsetPatterns: List<OnsetPattern> = emptyList(),
    ): List<Disease> {
        var result = items
        if (!symptomKeyword.isNullOrBlank()) {
            result = result.filter { disease ->
                disease.symptoms.mainSymptoms.any { it.contains(symptomKeyword) }
            }
        }
        if (onsetPatterns.isNotEmpty()) {
            result = result.filter { disease ->
                disease.symptoms.onsetPattern in onsetPatterns
            }
        }
        return result
    }
}
