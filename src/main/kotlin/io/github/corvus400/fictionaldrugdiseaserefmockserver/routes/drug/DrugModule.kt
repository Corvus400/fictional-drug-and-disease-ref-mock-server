package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentIdDetailEndpoint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.scenarioRoute
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.routing

private val drugDetailMetadata = EndpointMetadata(
    path = "/drugs/{id}",
    method = HttpMethod.Get,
    endpointName = "drugDetail",
    tag = ApiTag.DRUG,
    summary = "医薬品詳細を id で取得する",
)

private val drugListMetadata = EndpointMetadata(
    path = "/drugs",
    method = HttpMethod.Get,
    endpointName = "drugList",
    tag = ApiTag.DRUG,
    summary = "医薬品一覧を取得する",
)

private val drugDetailScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(name = "default", title = "デフォルト", description = "全 120 件のフィクスマージ語ベース医薬品"),
)

/**
 * `/drugs` (一覧) のカタログ用シナリオメタデータ。
 *
 * `DrugListFixtures` は DI チェーンで生成されるため module 読込時には instance を取得できない。
 * したがって `scenarioTitles` と同じ内容を手動で宣言する。`DrugListFixtures` 側でタイトルを変更した
 * 際はここも同期する必要がある (FixtureProviderConsistencyTest がキー一致は検証する)。
 */
private val drugListScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(name = "default", title = "デフォルト (120件)", description = "全 120 件のフィクスマージ語ベース医薬品"),
    ScenarioMeta(name = "empty", title = "空レスポンス", description = "0 件の医薬品一覧"),
)

val drugCatalogEntries: List<EndpointEntry> = listOf(
    drugDetailMetadata.toEntry(scenarios = drugDetailScenarios),
    drugListMetadata.toEntry(scenarios = drugListScenarios),
)

fun Application.drugModule(scenarioManager: ScenarioManager) {
    val provider: DrugFixtureProvider by dependencies
    val drugListFixtures: DrugListFixtures by dependencies
    routing {
        get("/drugs/{id}", {
            documentIdDetailEndpoint(
                metadata = drugDetailMetadata,
                endpointDescription = "`id` で指定した医薬品詳細 Fixture を返す。",
                idParamDescription = "医薬品 ID (`drug_NNNN` 形式)",
                exampleFixture = provider.all.first(),
            )
        }) {
            val id = call.parameters["id"].orEmpty()
            val drug = provider.getById(id = id)
            if (drug == null) {
                call.respond(status = HttpStatusCode.NotFound, message = mapOf("error" to "drug not found: $id"))
            } else {
                call.respond(drug)
            }
        }
    }
    scenarioRoute(
        metadata = drugListMetadata,
        defaultScenario = "default",
        fixtureProvider = drugListFixtures,
        scenarioManager = scenarioManager,
        endpointDescription = "起動時に生成された医薬品 Fixture 一覧を envelope 形式で返す。" +
            "X-Mock-Scenario ヘッダで `default` (120 件) / `empty` (0 件) を切り替え可能。",
    )
}
