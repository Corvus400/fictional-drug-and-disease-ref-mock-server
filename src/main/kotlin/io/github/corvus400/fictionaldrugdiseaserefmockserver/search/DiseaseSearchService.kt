package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease

object DiseaseSearchService {
    fun applyKeyword(
        items: List<Disease>,
        keyword: String?,
        match: KeywordMatch,
        target: DiseaseKeywordTarget,
    ): List<Disease> {
        if (keyword.isNullOrBlank()) return items
        return when (target) {
            DiseaseKeywordTarget.NAME ->
                when (match) {
                    KeywordMatch.PARTIAL ->
                        items.filter { disease ->
                            fieldsFor(target = target, disease = disease).any { field ->
                                field.contains(keyword)
                            }
                        }
                    KeywordMatch.PREFIX -> items
                }
            DiseaseKeywordTarget.NAME_ENGLISH,
            DiseaseKeywordTarget.SYNONYMS,
            ->
                when (match) {
                    KeywordMatch.PREFIX, KeywordMatch.PARTIAL -> items
                }
        }
    }

    /**
     * `target` に対応する検索対象フィールドの集合を返す。
     * 後続フェーズで NAME_ENGLISH / SYNONYMS の検索を駆動する際に再利用するための伏線。
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
