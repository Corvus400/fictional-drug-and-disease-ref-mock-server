package io.github.corvus400.mockserverbase.plugins

/**
 * OpenAPIドキュメントのタグ定義
 *
 * 全ルートモジュールはこのenumからタグを参照する。
 * フリーフォーマット文字列ではなくenumを使用することで:
 * - typoをコンパイル時に検出できる
 * - OpenAPI.ktのtags設定をApiTag.entriesから自動生成できる
 * - タグの追加・変更が一箇所で完結する
 *
 * コンストラクタパラメータtagName/descriptionはOpenAPI.ktで使用。
 * IntelliJのenum constructor解析の誤検知のため@Suppress("unused")を付与。
 * RedundantSuppression自体もIntelliJ偽陽性。
 */
@Suppress("unused", "RedundantSuppression")
enum class ApiTag(
    val tagName: String,
    val description: String,
) {
    SAMPLE("Sample", "サンプルAPI (テンプレート動作例)"),
    ADMIN("Admin", "Mock Server管理API（シナリオ切替・状態リセット）"),
    SYSTEM("System", "ヘルスチェック等システムAPI"),
}
