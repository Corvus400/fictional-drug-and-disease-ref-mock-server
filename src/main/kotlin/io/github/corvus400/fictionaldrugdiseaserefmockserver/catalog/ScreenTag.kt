package io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog

/**
 * 画面シナリオカタログ用の画面タグ
 *
 * 各エンドポイントが「どの画面で使われるか」を表す。
 * 1つのエンドポイントが複数画面に属する場合がある（N:M）。
 *
 * コンストラクタパラメータscreenName/descriptionはCatalogHtmlRenderer.ktで使用。
 * IntelliJのenum constructor解析の誤検知のため@Suppress("unused")を付与。
 * RedundantSuppression自体もIntelliJ偽陽性。
 */
@Suppress("unused", "RedundantSuppression")
enum class ScreenTag(
    val screenName: String,
    val description: String,
) {
    SAMPLE("サンプル画面", "テンプレート動作例"),
}
