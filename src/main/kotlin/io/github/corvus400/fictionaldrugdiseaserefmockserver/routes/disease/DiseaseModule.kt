package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentIdDetailEndpoint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentScenarioEndpoint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.resolveScenarioWithOverride
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.respondWithScenario
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.routing

private val diseaseDetailMetadata = EndpointMetadata(
    path = "/diseases/{id}",
    method = HttpMethod.Get,
    endpointName = "diseaseDetail",
    tag = ApiTag.DISEASE,
    summary = "疾患詳細を id で取得する",
)

private val diseaseListMetadata = EndpointMetadata(
    path = "/diseases",
    method = HttpMethod.Get,
    endpointName = "diseaseList",
    tag = ApiTag.DISEASE,
    summary = "疾患一覧を取得する",
)

private val diseaseDetailScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(name = "default", title = "デフォルト", description = "全 80 件のフィクスマージ語ベース疾患"),
)

/**
 * `/diseases` (一覧) のカタログ用シナリオメタデータ。
 *
 * `DiseaseListFixtures` は DI チェーンで生成されるため module 読込時には instance を取得できない。
 * したがって `scenarioTitles` と同じ内容を手動で宣言する。`DiseaseListFixtures` 側でタイトルを変更した
 * 際はここも同期する必要がある (FixtureProviderConsistencyTest がキー一致は検証する)。
 */
private val diseaseListScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(name = "default", title = "デフォルト (80件)", description = "全 80 件のフィクスマージ語ベース疾患"),
    ScenarioMeta(name = "empty", title = "空レスポンス", description = "0 件の疾患一覧"),
)

val diseaseCatalogEntries: List<EndpointEntry> = listOf(
    diseaseDetailMetadata.toEntry(scenarios = diseaseDetailScenarios),
    diseaseListMetadata.toEntry(scenarios = diseaseListScenarios),
)

private const val DISEASE_PAGE_MIN = 1
private const val DISEASE_PAGE_SIZE_MAX = 100
private const val DISEASE_LIST_DEFAULT_SCENARIO = "default"

fun Application.diseaseModule(scenarioManager: ScenarioManager) {
    val provider: DiseaseFixtureProvider by dependencies
    val diseaseListFixtures: DiseaseListFixtures by dependencies
    routing {
        get("/diseases/{id}", {
            documentIdDetailEndpoint(
                metadata = diseaseDetailMetadata,
                endpointDescription = "`id` で指定した疾患詳細 Fixture を返す。",
                idParamDescription = "疾患 ID (`disease_NNNN` 形式)",
                exampleFixture = provider.all.first(),
            )
        }) {
            val id = call.parameters["id"].orEmpty()
            val disease = provider.getById(id = id)
            if (disease == null) {
                call.respond(status = HttpStatusCode.NotFound, message = mapOf("error" to "disease not found: $id"))
            } else {
                call.respond(disease)
            }
        }
    }

    EndpointRegistry.register(
        diseaseListMetadata.toEntry(scenarios = diseaseListFixtures.scenarioMetas.values.toList()),
    )
    routing {
        route(
            path = diseaseListMetadata.path,
            method = diseaseListMetadata.method,
            builder = {
                documentScenarioEndpoint(
                    summary = diseaseListMetadata.summary,
                    endpointDescription = "起動時に生成された疾患 Fixture 一覧を envelope 形式で返す。" +
                        "X-Mock-Scenario ヘッダで `default` (80 件) / `empty` (0 件) を切り替え可能。" +
                        "`page` と `page_size` 両方指定時のみページング結果を返す (page_size は 1..100 にクランプ)。",
                    tag = diseaseListMetadata.tag,
                    fixtureProvider = diseaseListFixtures,
                )
            },
        ) {
            handle {
                val resolved = call.resolveScenarioWithOverride(
                    scenarioManager = scenarioManager,
                    endpointName = diseaseListMetadata.endpointName,
                    default = DISEASE_LIST_DEFAULT_SCENARIO,
                    fixtureProvider = diseaseListFixtures::getByScenario,
                )
                val pageParam = call.request.queryParameters["page"]?.toIntOrNull()
                val pageSizeParam = call.request.queryParameters["page_size"]?.toIntOrNull()
                if (pageParam == null || pageSizeParam == null) {
                    call.respondWithScenario(resolved)
                } else {
                    val envelope = resolved.fixture
                    val safePage = pageParam.coerceAtLeast(minimumValue = DISEASE_PAGE_MIN)
                    val safePageSize = pageSizeParam.coerceIn(
                        minimumValue = DISEASE_PAGE_MIN,
                        maximumValue = DISEASE_PAGE_SIZE_MAX,
                    )
                    val totalCount = envelope.items.size
                    val totalPages = if (totalCount == 0) {
                        0
                    } else {
                        (totalCount + safePageSize - 1) / safePageSize
                    }
                    val startIndex = (safePage - 1) * safePageSize
                    val endIndex = (startIndex + safePageSize).coerceAtMost(maximumValue = totalCount)
                    val slicedItems = if (startIndex < totalCount) {
                        envelope.items.subList(fromIndex = startIndex, toIndex = endIndex)
                    } else {
                        emptyList()
                    }
                    call.respond(
                        message = envelope.copy(
                            items = slicedItems,
                            page = safePage,
                            pageSize = safePageSize,
                            totalPages = totalPages,
                            totalCount = totalCount,
                        ),
                    )
                }
            }
        }
    }
}
