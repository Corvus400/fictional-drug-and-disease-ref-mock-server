package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming

/**
 * 決定論的に生成されたフィクスマージ語名。カナ・当て字漢字・採用パターンの組。
 */
data class FictionName(
    val kana: String,
    val kanji: String,
    val pattern: NamePattern,
)

/**
 * Drug / Disease fixture が持つ名前スロット。
 *
 * 各スロットは [NamePattern] に対応付けられ、同一 id であってもスロットが違えば seed が
 * 分離されるため別の名前が生成される。本 Issue (#38) では代表 5 件で着手し、Phase 5 の
 * Generator 層で必要に応じて追加する。
 *
 * `pattern` は SSOT として enum 自体が保持し、`FictionNameGenerator` は `slot.pattern` を
 * 参照するだけで足りる。
 */
enum class NameSlot(val pattern: NamePattern) {
    GENERIC_NAME(pattern = NamePattern.PATTERN_C),
    BRAND_NAME(pattern = NamePattern.PATTERN_A),
    DISEASE_NAME_JA(pattern = NamePattern.PATTERN_B),
    DISEASE_ALIAS(pattern = NamePattern.PATTERN_B),
    SYMPTOM_TERM(pattern = NamePattern.PATTERN_A),
}
