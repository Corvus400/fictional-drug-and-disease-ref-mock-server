package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseDetailFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.ErrorResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.DiseaseListResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.DiseaseSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
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
import kotlin.math.ceil

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
    ScenarioMeta(
        name = "default",
        title = "デフォルト (80件)",
        description = "全 80 件のフィクスマージ語ベース疾患を page_size=${DiseaseListFixtures.DEFAULT_PAGE_SIZE} " +
            "でページング",
    ),
    ScenarioMeta(name = "empty", title = "空レスポンス", description = "0 件の疾患一覧"),
)

val diseaseCatalogEntries: List<EndpointEntry> = listOf(
    diseaseDetailMetadata.toEntry(scenarios = diseaseDetailScenarios),
    diseaseListMetadata.toEntry(scenarios = diseaseListScenarios),
)

fun Application.diseaseModule(scenarioManager: ScenarioManager) {
    val provider: DiseaseFixtureProvider by dependencies
    val diseaseListFixtures: DiseaseListFixtures by dependencies
    val diseaseDetailFixtures: DiseaseDetailFixtures by dependencies
    routing {
        get("/diseases/{id}", {
            documentIdDetailEndpoint(
                metadata = diseaseDetailMetadata,
                endpointDescription = "`id` で指定した疾患詳細 Fixture を返す。" +
                    " Admin API `POST /__admin/configs/${diseaseDetailMetadata.endpointName}` で" +
                    " delayMs / statusCode / headers をオーバーライド可能。",
                idParamDescription = "疾患 ID (`disease_NNNN` 形式)",
                exampleFixture = provider.all.first(),
            )
        }) {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse(code = "BAD_REQUEST", message = "id path parameter is required"),
                )
                return@get
            }
            val disease = diseaseDetailFixtures.findById(id = id)
            if (disease == null) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ErrorResponse(code = "NOT_FOUND", message = "Disease not found: $id"),
                )
                return@get
            }
            val resolved = call.resolveScenarioWithOverride(
                scenarioManager = scenarioManager,
                endpointName = diseaseDetailMetadata.endpointName,
                default = "default",
                fixtureProvider = { _ -> disease },
            )
            if (resolved.status == HttpStatusCode.NotFound) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ErrorResponse(code = "NOT_FOUND", message = "Disease not found: $id"),
                )
                return@get
            }
            call.respondWithScenario(resolved = resolved)
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
                        " `page` (1-origin) / `page_size` (既定 ${DiseaseListFixtures.DEFAULT_PAGE_SIZE}, " +
                        "上限 ${DiseaseListFixtures.MAX_PAGE_SIZE}) でページング可能。",
                    tag = diseaseListMetadata.tag,
                    fixtureProvider = diseaseListFixtures,
                    additionalRequestDoc = {
                        request {
                            queryParameter<Int>("page") {
                                description = "1-origin のページ番号 (既定 1)"
                                required = false
                            }
                            queryParameter<Int>("page_size") {
                                description = "1 ページの件数 (既定 " +
                                    "${DiseaseListFixtures.DEFAULT_PAGE_SIZE}, 上限 ${DiseaseListFixtures.MAX_PAGE_SIZE})"
                                required = false
                            }
                        }
                    },
                )
            },
        ) {
            handle {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = (
                    call.request.queryParameters["page_size"]?.toIntOrNull()
                        ?: DiseaseListFixtures.DEFAULT_PAGE_SIZE
                    ).coerceAtMost(maximumValue = DiseaseListFixtures.MAX_PAGE_SIZE)
                val chapterFilter = call.request.queryParameters["icd10_chapter"]
                    ?.let { Icd10Chapter.fromChapterKey(key = it) }
                val departmentFilter = call.request.queryParameters["department"]
                    ?.let { MedicalDepartment.fromSerialName(key = it) }
                val chronicityFilter = call.request.queryParameters["chronicity"]
                val infectiousFilter = call.request.queryParameters["infectious"]?.toBooleanStrictOrNull()
                val resolved = call.resolveScenarioWithOverride(
                    scenarioManager = scenarioManager,
                    endpointName = diseaseListMetadata.endpointName,
                    default = "default",
                    fixtureProvider = { scenario ->
                        val summaries = diseaseListFixtures.summariesByScenario[scenario].orEmpty()
                        val filtered = applyListFilters(
                            summaries = summaries,
                            chapterFilter = chapterFilter,
                            departmentFilter = departmentFilter,
                            chronicitySerialName = chronicityFilter,
                            infectiousFilter = infectiousFilter,
                        )
                        paginate(
                            summaries = filtered,
                            page = page,
                            pageSize = pageSize,
                        )
                    },
                )
                call.respondWithScenario(resolved = resolved)
            }
        }
    }
}

/**
 * `/diseases` のクエリ由来フィルタを `summaries` に適用する。
 *
 * `icd10_chapter` / `department` / `chronicity` / `infectious` を AND 合成する。引数の null は
 * 「このフィルタを適用しない」を表す。`chronicitySerialName` は `Chronicity.serialName` と生文字列
 * 比較するため、未知キーは `null` ではなくヒット 0 件を返す (fromChapterKey / fromSerialName とは
 * 非対称)。`infectiousFilter` は `toBooleanStrictOrNull()` で parse するため `"true"`/`"false"` 以外
 * は `null` に落ちて無視する。
 */
private fun applyListFilters(
    summaries: List<DiseaseSummary>,
    chapterFilter: Icd10Chapter?,
    departmentFilter: MedicalDepartment?,
    chronicitySerialName: String?,
    infectiousFilter: Boolean?,
): List<DiseaseSummary> {
    var result = summaries
    if (chapterFilter != null) {
        result = result.filter { it.icd10Chapter == chapterFilter }
    }
    if (departmentFilter != null) {
        result = result.filter { summary -> summary.medicalDepartment.any { it == departmentFilter } }
    }
    if (chronicitySerialName != null) {
        result = result.filter { it.chronicity.serialName == chronicitySerialName }
    }
    if (infectiousFilter != null) {
        result = result.filter { it.infectious == infectiousFilter }
    }
    return result
}

private fun paginate(
    summaries: List<DiseaseSummary>,
    page: Int,
    pageSize: Int,
): DiseaseListResponse {
    val totalCount = summaries.size
    val totalPages = if (totalCount == 0) 0 else ceil(totalCount.toDouble() / pageSize.toDouble()).toInt()
    val startIndex = (page - 1) * pageSize
    val items = if (startIndex >= totalCount) {
        emptyList()
    } else {
        summaries.drop(n = startIndex).take(n = pageSize)
    }
    return DiseaseListResponse(
        items = items,
        page = page,
        pageSize = pageSize,
        totalPages = totalPages,
        totalCount = totalCount,
    )
}
