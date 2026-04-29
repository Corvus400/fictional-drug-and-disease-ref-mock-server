package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugDetailFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListQuery
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.ErrorResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.DosageForm
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RegulatoryClass
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.enums.RouteOfAdministration
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentIdDetailEndpoint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentScenarioEndpoint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.resolveScenarioWithOverride
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.respondWithScenario
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugKeywordTarget
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DrugSortKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.KeywordMatch
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.route
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
    ScenarioMeta(
        name = "default",
        title = "デフォルト (120件)",
        description = "全 120 件のフィクスマージ語ベース医薬品を page_size=${DrugListFixtures.DEFAULT_PAGE_SIZE} " +
            "でページング",
    ),
    ScenarioMeta(name = "empty", title = "空レスポンス", description = "0 件の医薬品一覧"),
)

val drugCatalogEntries: List<EndpointEntry> = listOf(
    drugDetailMetadata.toEntry(scenarios = drugDetailScenarios),
    drugListMetadata.toEntry(scenarios = drugListScenarios),
)

fun Application.drugModule(scenarioManager: ScenarioManager) {
    val drugs: List<Drug> by dependencies
    val drugListFixtures: DrugListFixtures by dependencies
    val drugDetailFixtures: DrugDetailFixtures by dependencies
    routing {
        get("/drugs/{id}", {
            documentIdDetailEndpoint(
                metadata = drugDetailMetadata,
                endpointDescription = "`id` で指定した医薬品詳細 Fixture を返す。",
                idParamDescription = "医薬品 ID (`drug_NNNN` 形式)",
                exampleFixture = drugs.first(),
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
            val drug = drugDetailFixtures.findById(id = id)
            if (drug == null) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ErrorResponse(code = "NOT_FOUND", message = "Drug not found: $id"),
                )
                return@get
            }
            val resolved = call.resolveScenarioWithOverride(
                scenarioManager = scenarioManager,
                endpointName = drugDetailMetadata.endpointName,
                default = "default",
                fixtureProvider = { _ -> drug },
            )
            if (resolved.status == HttpStatusCode.NotFound) {
                call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ErrorResponse(code = "NOT_FOUND", message = "Drug not found: $id"),
                )
                return@get
            }
            call.respondWithScenario(resolved = resolved)
        }
    }
    EndpointRegistry.register(
        drugListMetadata.toEntry(scenarios = drugListFixtures.scenarioMetas.values.toList()),
    )
    routing {
        route(
            path = drugListMetadata.path,
            method = drugListMetadata.method,
            builder = {
                documentScenarioEndpoint(
                    summary = drugListMetadata.summary,
                    endpointDescription = "起動時に生成された医薬品 Fixture 一覧を envelope 形式で返す。" +
                        "X-Mock-Scenario ヘッダで `default` (120 件) / `empty` (0 件) を切り替え可能。" +
                        " `page` (1-origin) / `page_size` (既定 ${DrugListFixtures.DEFAULT_PAGE_SIZE}, " +
                        "上限 ${DrugListFixtures.MAX_PAGE_SIZE}) でページング可能。",
                    tag = drugListMetadata.tag,
                    fixtureProvider = drugListFixtures,
                    additionalRequestDoc = {
                        request {
                            queryParameter<Int>("page") {
                                description = "1-origin のページ番号 (既定 1)"
                                required = false
                            }
                            queryParameter<Int>("page_size") {
                                description = "1 ページの件数 (既定 " +
                                    "${DrugListFixtures.DEFAULT_PAGE_SIZE}, 上限 ${DrugListFixtures.MAX_PAGE_SIZE})"
                                required = false
                            }
                            queryParameter<String>("category_atc") {
                                description = "ATC コードの先頭文字 (例: `A`)。指定時は前方一致で絞り込み"
                                required = false
                            }
                            queryParameter<String>("regulatory_class") {
                                description = "規制区分の `@SerialName` 値 " +
                                    "(例: `${RegulatoryClass.PRESCRIPTION_REQUIRED.serialName}`)。" +
                                    "指定時は `regulatory_class` リストに含まれるものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("route") {
                                description = "投与経路の `@SerialName` 値 " +
                                    "(例: `${RouteOfAdministration.ORAL.serialName}`)。" +
                                    "指定時は `route_of_administration` が一致するものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("dosage_form") {
                                description = "剤形の `@SerialName` 値 " +
                                    "(例: `${DosageForm.TABLET.serialName}`)。" +
                                    "指定時は `dosage_form` が一致するものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("category_name") {
                                description = "薬効カテゴリ名 (例: `消化器系および代謝`)。" +
                                    "指定時は `therapeutic_category_name` が完全一致するものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("keyword") {
                                description = "検索キーワード。半角空白区切りで複数指定すると AND 結合 " +
                                    "(各トークンが対象フィールド集合のいずれかに OR で一致する必要)。" +
                                    "空または未指定時は絞り込みを行わない。"
                                required = false
                            }
                            queryParameter<String>("keyword_match") {
                                description = "キーワード一致モード。`partial` (既定) で部分一致、" +
                                    "`prefix` で前方一致。"
                                required = false
                            }
                            queryParameter<String>("keyword_target") {
                                description = "キーワード検索対象。`generic` (一般名) / `brand` (販売名 + " +
                                    "販売名カナ) / `both` (既定。両方)。"
                                required = false
                            }
                            queryParameter<String>("sort") {
                                description = "ソート順。`-revised_at` (既定。改訂日降順) / " +
                                    "`brand_name_kana` (販売名カナ昇順) / `atc_code` (ATC コード昇順) / " +
                                    "`therapeutic_category_name` (薬効カテゴリ名昇順)。"
                                required = false
                            }
                            queryParameter<String>("adverse_reaction_keyword") {
                                description = "副作用キーワード (部分一致)。" +
                                    "`adverseReactions.serious[].name` および " +
                                    "`adverseReactions.other.*` の全副作用名テキストを対象に絞り込む。" +
                                    "空または未指定時は絞り込みを行わない。"
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
                        ?: DrugListFixtures.DEFAULT_PAGE_SIZE
                    ).coerceAtMost(maximumValue = DrugListFixtures.MAX_PAGE_SIZE)
                val atcPrefix = call.request.queryParameters["category_atc"]
                val regulatoryClass = call.request.queryParameters["regulatory_class"]
                val route = call.request.queryParameters["route"]
                val dosageForm = call.request.queryParameters["dosage_form"]
                val categoryName = call.request.queryParameters["category_name"]
                val keyword = call.request.queryParameters["keyword"]
                val keywordMatch = KeywordMatch.fromQuery(
                    value = call.request.queryParameters["keyword_match"],
                )
                val keywordTarget = DrugKeywordTarget.fromQuery(
                    value = call.request.queryParameters["keyword_target"],
                )
                val adverseReactionKeyword = call.request.queryParameters["adverse_reaction_keyword"]
                val sortKey = try {
                    DrugSortKey.fromQuery(raw = call.request.queryParameters["sort"])
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse(code = "INVALID_SORT_KEY", message = e.message.orEmpty()),
                    )
                    return@handle
                }
                val resolved = call.resolveScenarioWithOverride(
                    scenarioManager = scenarioManager,
                    endpointName = drugListMetadata.endpointName,
                    default = "default",
                    fixtureProvider = { scenario ->
                        drugListFixtures.resolve(
                            scenario = scenario,
                            page = page,
                            pageSize = pageSize,
                            query = DrugListQuery(
                                atcPrefix = atcPrefix,
                                regulatoryClassSerialName = regulatoryClass,
                                routeOfAdministrationSerialName = route,
                                dosageFormSerialName = dosageForm,
                                categoryName = categoryName,
                                keyword = keyword,
                                keywordMatch = keywordMatch,
                                keywordTarget = keywordTarget,
                                adverseReactionKeyword = adverseReactionKeyword,
                            ),
                            sortKey = sortKey,
                        )
                    },
                )
                call.respondWithScenario(resolved = resolved)
            }
        }
    }
}
