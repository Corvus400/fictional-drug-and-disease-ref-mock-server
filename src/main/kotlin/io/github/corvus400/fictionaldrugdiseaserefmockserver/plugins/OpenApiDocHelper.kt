package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
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

/**
 * `/xxx/{id}` 形式の id 指定詳細取得ルートに OpenAPI ドキュメントを付ける。
 *
 * @param metadata ルートのメタデータ (path/method/endpointName/tag/summary)
 * @param endpointDescription OpenAPI description 本文
 * @param idParamDescription path parameter `id` の説明
 * @param exampleFixture 200 OK レスポンス body の example 値。Fixture の実体を渡す
 */
inline fun <reified T : Any> RouteConfig.documentIdDetailEndpoint(
    metadata: EndpointMetadata,
    endpointDescription: String,
    idParamDescription: String,
    exampleFixture: T,
) {
    this.summary = metadata.summary
    tags(metadata.tag.tagName)
    description = endpointDescription
    request {
        pathParameter<String>("id") {
            description = idParamDescription
        }
    }
    response {
        code(HttpStatusCode.OK) {
            description = "`id` で指定された Fixture"
            body<T> {
                example("default") {
                    value = exampleFixture
                }
            }
        }
        code(HttpStatusCode.NotFound) {
            description = "指定 id が存在しない"
        }
    }
}

/**
 * `/xxx` 形式の一覧取得ルートに OpenAPI ドキュメントを付ける。
 *
 * @param metadata ルートのメタデータ (path/method/endpointName/tag/summary)
 * @param endpointDescription OpenAPI description 本文
 * @param exampleFixtures 200 OK レスポンス body の example 値 (配列全体)
 */
inline fun <reified T : Any> RouteConfig.documentListEndpoint(
    metadata: EndpointMetadata,
    endpointDescription: String,
    exampleFixtures: List<T>,
) {
    this.summary = metadata.summary
    tags(metadata.tag.tagName)
    description = endpointDescription
    response {
        code(HttpStatusCode.OK) {
            description = "Fixture 一覧"
            body<List<T>> {
                example("default") {
                    value = exampleFixtures
                }
            }
        }
    }
}

/**
 * シナリオ切替を持たない静的レスポンス系ルートに OpenAPI ドキュメントを付ける。
 *
 * @param metadata ルートのメタデータ (path/method/endpointName/tag/summary)
 * @param endpointDescription OpenAPI description 本文
 * @param okResponseDescription 200 OK レスポンスの説明
 */
inline fun <reified T : Any> RouteConfig.documentStatelessEndpoint(
    metadata: EndpointMetadata,
    endpointDescription: String,
    okResponseDescription: String,
) {
    this.summary = metadata.summary
    tags(metadata.tag.tagName)
    description = endpointDescription
    response {
        code(HttpStatusCode.OK) {
            description = okResponseDescription
            body<T>()
        }
    }
}
