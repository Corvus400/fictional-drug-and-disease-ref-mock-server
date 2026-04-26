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
            DiseaseKeywordTarget.NAME,
            DiseaseKeywordTarget.NAME_ENGLISH,
            DiseaseKeywordTarget.SYNONYMS,
            ->
                when (match) {
                    KeywordMatch.PREFIX, KeywordMatch.PARTIAL -> items
                }
        }
    }
}
