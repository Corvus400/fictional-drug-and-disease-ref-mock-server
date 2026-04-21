package io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario

import kotlinx.serialization.Serializable

/**
 * Admin API からのシナリオオーバーライド設定
 * シナリオのデフォルト値を実行時に上書きするために使用
 */
@Serializable
data class ScenarioOverride(
    /** シナリオ名（例: "Default", "ServerError"） */
    val state: String,
    /** レスポンス遅延（ミリ秒）のオーバーライド */
    val delayMs: Long? = null,
    /** HTTPステータスコードのオーバーライド */
    val statusCode: Int? = null,
    /** カスタムヘッダーのオーバーライド */
    val headers: Map<String, String>? = null,
)
