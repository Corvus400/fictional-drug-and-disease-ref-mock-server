package io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.placeholder

/**
 * 段落描画コンテキスト: プレースホルダー解決時の自己参照情報を保持するモデル。
 *
 * `selfName` は描画対象 Disease の表示名で、テンプレート内 `{{disease}}` の
 * 自己参照解決 (`DiseasePlaceholderCategory.B_SELF_REFERENCE`) に使用。
 */
data class DiseaseRenderContext(
    val selfName: String,
)
