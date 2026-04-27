package io.github.corvus400.fictionaldrugdiseaserefmockserver.search

enum class DiseaseKeywordTarget {
    NAME,
    NAME_ENGLISH,
    SYNONYMS,
    ;

    companion object {
        /**
         * `keyword_target` クエリパラメータを `DiseaseKeywordTarget` に解釈する。
         *
         * `null` / 空文字は既定値 `NAME` にフォールバックする (`/diseases` 検索 API 既定挙動)。
         * 受理値は lower-case 厳密一致 (`name` / `name_english` / `synonyms`)。不正値 (例: `NAME`
         * など大文字、`title`) のバリデーションエラー化は別 Issue (#91) で扱う想定で、現状は
         * 安全側に既定値へフォールバックする。Drug 側 PR #286 (`DrugKeywordTarget.fromQuery`) と
         * 仕様統一。
         */
        fun fromQuery(value: String?): DiseaseKeywordTarget = when (value) {
            null, "" -> NAME
            "name" -> NAME
            "name_english" -> NAME_ENGLISH
            "synonyms" -> SYNONYMS
            else -> NAME
        }
    }
}
