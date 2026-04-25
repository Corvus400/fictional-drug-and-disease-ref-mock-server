package io.github.corvus400.fictionaldrugdiseaserefmockserver.config

import io.ktor.server.application.Application

/**
 * Mock Serverの設定
 *
 * application.yamlのmockserverセクションから読み込む
 */
data class MockServerConfig(
    /** デフォルトシナリオ名 */
    val defaultScenario: String,
    /** Admin APIを有効にするかどうか */
    val enableAdminApi: Boolean,
)

/**
 * application.yamlまたは環境変数からMockServerConfigを読み込む
 *
 * ## 環境変数の読み取り方法について
 *
 * Ktorのプレースホルダー展開（${ENV:default}形式）は`ktor.*`名前空間に限定されており、
 * カスタム名前空間（`mockserver.*`）では動作しないことが検証で判明した。
 *
 * 検証結果:
 * - `ktor.deployment.port: ${PORT:8080}` → ✅ 正常に展開される
 * - `mockserver.enableAdminApi: ${ENABLE_ADMIN_API:true}` → ❌ 展開されない
 *
 * そのため、カスタム設定は`System.getenv()`で環境変数を直接読み取る方式を採用。
 *
 * 優先順位:
 * 1. 環境変数（DEFAULT_SCENARIO, ENABLE_ADMIN_API）
 * 2. application.yamlの設定
 * 3. デフォルト値
 *
 */
fun Application.loadMockServerConfig(): MockServerConfig {
    // Phase 9-B run 3: source change to defeat UP-TO-DATE
    val config = environment.config
    return MockServerConfig(
        defaultScenario = System.getenv("DEFAULT_SCENARIO")
            ?: config.propertyOrNull("mockserver.defaultScenario")?.getString()
            ?: "default",
        enableAdminApi = System.getenv("ENABLE_ADMIN_API")?.toBoolean()
            ?: config.propertyOrNull("mockserver.enableAdminApi")?.getString()?.toBoolean()
            ?: true,
    )
}
