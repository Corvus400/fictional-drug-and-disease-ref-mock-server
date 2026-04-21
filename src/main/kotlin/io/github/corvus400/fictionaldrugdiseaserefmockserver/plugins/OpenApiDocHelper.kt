package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.ktor.http.HttpStatusCode

/**
 * FixtureProviderからOpenAPIドキュメントを自動生成するヘルパー
 *
 * 以下を自動設定:
 * - summary, tags, description（シナリオ一覧を含む）
 * - X-Mock-Scenario ヘッダーパラメータ
 * - レスポンスbody（型スキーマ + 全シナリオのExample値）
 *
 * @param additionalRequestDoc OpenAPI requestブロックに追加のパラメータ定義を注入するラムダ。
 *   将来クエリパラメータを持つエンドポイントで scenarioRoute() を使用する場合に利用可能。
 */
inline fun <reified T : Any> RouteConfig.documentScenarioEndpoint(
    summary: String,
    endpointDescription: String,
    tag: ApiTag,
    fixtureProvider: FixtureProvider<T>,
    noinline additionalRequestDoc: (RouteConfig.() -> Unit) = {},
) {
    this.summary = summary
    tags(tag.tagName)
    val scenarioList = fixtureProvider.scenarios.keys.joinToString(separator = "\n") { "- `$it`" }
    description = """
        |$endpointDescription
        |
        |## 利用可能シナリオ
        |X-Mock-Scenario ヘッダーまたは Admin API で切り替え可能:
        |$scenarioList
        |
        |デフォルト: `${fixtureProvider.scenarios.keys.first()}`
    """.trimMargin()
    request {
        headerParameter<String>("X-Mock-Scenario") {
            description = "シナリオ名を指定（Admin APIオーバーライドより優先）"
            required = false
        }
    }
    additionalRequestDoc()
    response {
        code(HttpStatusCode.OK) {
            description = "シナリオに応じたレスポンス"
            body<T> {
                fixtureProvider.scenarios.forEach { (name, fixture) ->
                    example(name) {
                        value = fixture
                    }
                }
            }
        }
    }
}
