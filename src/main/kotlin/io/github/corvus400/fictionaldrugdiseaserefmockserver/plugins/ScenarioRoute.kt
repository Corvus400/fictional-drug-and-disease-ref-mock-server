package io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.FixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.route
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

/**
 * シナリオ対応ルートを登録するヘルパー関数
 *
 * この関数を使用することで:
 * 1. fixtureProviderにFixtureProvider<T>実装が必須（コンパイル時チェック）
 * 2. 固定値での実装が不可能（resolveScenarioWithOverride経由のみ）
 * 3. 一貫したシナリオ対応パターンを強制
 * 4. tag/summary/endpointDescriptionを指定するとOpenAPIドキュメントが自動生成される
 *
 * @param metadata エンドポイントメタデータ（path/method/endpointName/tag/summaryの単一定義）
 * @param defaultScenario デフォルトシナリオ名
 * @param fixtureProvider FixtureProvider実装（コンパイル時強制）
 * @param scenarioManager シナリオマネージャー
 * @param endpointDescription OpenAPIエンドポイントの説明
 * @param additionalRequestDoc OpenAPI requestブロックに追加のパラメータ定義を注入するラムダ
 */
inline fun <reified T : Any> Application.scenarioRoute(
    metadata: EndpointMetadata,
    defaultScenario: String,
    fixtureProvider: FixtureProvider<T>,
    scenarioManager: ScenarioManager,
    endpointDescription: String,
    noinline additionalRequestDoc: (RouteConfig.() -> Unit) = {},
) {
    EndpointRegistry.register(
        metadata.toEntry(scenarios = fixtureProvider.scenarioMetas.values.toList()),
    )

    routing {
        route(
            path = metadata.path,
            method = metadata.method,
            builder = {
                documentScenarioEndpoint(
                    summary = metadata.summary,
                    endpointDescription = endpointDescription,
                    tag = metadata.tag,
                    fixtureProvider = fixtureProvider,
                    additionalRequestDoc = additionalRequestDoc,
                )
            },
        ) {
            handle {
                val resolved = call.resolveScenarioWithOverride(
                    scenarioManager = scenarioManager,
                    endpointName = metadata.endpointName,
                    default = defaultScenario,
                    fixtureProvider = fixtureProvider::getByScenario,
                )
                call.respondWithScenario(resolved)
            }
        }
    }
}
