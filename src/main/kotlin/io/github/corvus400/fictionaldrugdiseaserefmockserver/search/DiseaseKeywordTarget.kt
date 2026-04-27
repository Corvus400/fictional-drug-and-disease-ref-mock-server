package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

enum class DiseaseKeywordTarget {
    NAME,
    NAME_ENGLISH,
    SYNONYMS,
    ;

    companion object {
        /**
         * `/diseases` のクエリパラメータ `keyword_target` を `DiseaseKeywordTarget` に解決する。
         *
         * 受理値は enum 名の case-insensitive (例: `name`, `NAME`, `name_english`, `synonyms`)。
         * null / 不一致は既定値 `NAME` を返す (Phase 11 仕様: 既定の検索対象は名称)。
         */
        fun fromQuery(value: String?): DiseaseKeywordTarget =
            when (value?.uppercase()) {
                "NAME" -> NAME
                "NAME_ENGLISH" -> NAME_ENGLISH
                "SYNONYMS" -> SYNONYMS
                else -> NAME
            }
    }
}
