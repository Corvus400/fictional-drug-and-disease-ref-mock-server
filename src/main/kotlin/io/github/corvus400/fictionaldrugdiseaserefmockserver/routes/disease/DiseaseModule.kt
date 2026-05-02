package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointRegistry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseDetailFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.ErrorResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Chronicity
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.ExamCategory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.Icd10Chapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.MedicalDepartment
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.enums.OnsetPattern
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.toSummary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentIdDetailEndpoint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentScenarioEndpoint
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.resolveScenarioWithOverride
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.respondWithScenario
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DiseaseKeywordTarget
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DiseaseSearchService
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.DiseaseSortKey
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.KeywordMatch
import io.github.corvus400.fictionaldrugdiseaserefmockserver.search.SearchDefaults
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
    ScenarioMeta(
        name = "default",
        title = "デフォルト (80件)",
        description = "全 80 件のフィクスマージ語ベース疾患を page_size=${SearchDefaults.DEFAULT_PAGE_SIZE} " +
            "でページング",
    ),
    ScenarioMeta(name = "empty", title = "空レスポンス", description = "0 件の疾患一覧"),
)

val diseaseCatalogEntries: List<EndpointEntry> = listOf(
    diseaseDetailMetadata.toEntry(scenarios = diseaseDetailScenarios),
    diseaseListMetadata.toEntry(scenarios = diseaseListScenarios),
)

fun Application.diseaseModule(scenarioManager: ScenarioManager) {
    val diseases: List<Disease> by dependencies
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
                exampleFixture = diseases.first(),
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
                        " `page` (1-origin) / `page_size` (既定 ${SearchDefaults.DEFAULT_PAGE_SIZE}, " +
                        "上限 ${SearchDefaults.MAX_PAGE_SIZE}) でページング可能。",
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
                                    "${SearchDefaults.DEFAULT_PAGE_SIZE}, 上限 ${SearchDefaults.MAX_PAGE_SIZE})"
                                required = false
                            }
                            queryParameter<String>("icd10_chapter") {
                                description = "ICD-10 章コードの `@SerialName` 値 " +
                                    "(例: `${Icd10Chapter.CHAPTER_I.serialName}`)。" +
                                    "指定時は `icd10_chapter` が一致するものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("department") {
                                description = "診療科の `@SerialName` 値 " +
                                    "(例: `${MedicalDepartment.INTERNAL_MEDICINE.serialName}`)。" +
                                    "指定時は `medical_department` リストに含まれるものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("chronicity") {
                                description = "慢性度の `@SerialName` 値 " +
                                    "(例: `${Chronicity.CHRONIC.serialName}`)。" +
                                    "指定時は `chronicity` が一致するものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("infectious") {
                                description = "感染性の有無 (`true` / `false`)。" +
                                    "指定時は `infectious` が一致するものに絞り込み"
                                required = false
                            }
                            queryParameter<String>("keyword") {
                                description = "検索キーワード。空白区切りで複数語を渡すと AND 結合 " +
                                    "(各語が `keyword_target` の対象フィールドのいずれかにヒットすればよい)。" +
                                    "未指定 / 空文字 / 空白のみは絞り込みなし。"
                                required = false
                            }
                            queryParameter<String>("keyword_match") {
                                description = "一致モード `partial` (既定) / `prefix` (lower-case 厳密)。" +
                                    "未指定 / 空文字 / 不正値は既定値 `partial` にフォールバック。"
                                required = false
                            }
                            queryParameter<String>("keyword_target") {
                                description = "検索対象 `name` (既定、`name` + `name_kana`) / `name_english` / " +
                                    "`synonyms` (lower-case 厳密)。未指定 / 空文字 / 不正値は既定値 `name` " +
                                    "にフォールバック。"
                                required = false
                            }
                            queryParameter<String>("sort") {
                                description = "並び替えキー。未指定は `-revised_at` (改訂日降順)、" +
                                    "`name_kana` (読み仮名昇順) / `icd10_chapter` (ICD-10 章昇順) を許容。" +
                                    "未対応キーは 400 BadRequest。"
                                required = false
                            }
                            queryParameter<String>("symptom_keyword") {
                                description = "主症状キーワード (部分一致)。`symptoms.mainSymptoms[]` を対象に絞り込む。" +
                                    "空または未指定時は絞り込みを行わない。"
                                required = false
                            }
                            queryParameter<String>("onset_pattern") {
                                description = "発症パターン (`OnsetPattern` の enum 名、例: `${OnsetPattern.ACUTE.name}`)。" +
                                    "複数指定時は OR 結合、他フィルタとは AND 結合。未知の値は HTTP 400 + " +
                                    "`ErrorResponse(code=\"INVALID_ONSET_PATTERN\")` を返す。"
                                required = false
                            }
                            queryParameter<String>("exam_category") {
                                description = "検査カテゴリ (`ExamCategory` の enum 名、例: `${ExamCategory.IMAGING.name}`)。" +
                                    "複数指定時は OR 結合、他フィルタとは AND 結合。未知の値は HTTP 400 + " +
                                    "`ErrorResponse(code=\"INVALID_EXAM_CATEGORY\")` を返す。"
                                required = false
                            }
                            queryParameter<String>("has_pharmacological_treatment") {
                                description = "薬物治療の有無 (`true` / `false`)。" +
                                    "指定時は `treatments.pharmacological` の空/非空で絞り込む。"
                                required = false
                            }
                            queryParameter<String>("has_severity_grading") {
                                description = "重症度分類の有無 (`true` / `false`)。" +
                                    "指定時は `severityGrading` の null / non-null で絞り込む。"
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
                        ?: SearchDefaults.DEFAULT_PAGE_SIZE
                    ).coerceAtMost(maximumValue = SearchDefaults.MAX_PAGE_SIZE)
                val chapterParam = call.request.queryParameters["icd10_chapter"]
                    ?.takeIf { it.isNotEmpty() }
                val chapterFilter = chapterParam?.let { Icd10Chapter.fromSerialName(serialName = it) }
                val rejectChapterFilter = chapterParam != null && chapterFilter == null
                val departmentParam = call.request.queryParameters["department"]
                val departmentFilter = departmentParam?.let { MedicalDepartment.fromSerialName(key = it) }
                val rejectDepartmentFilter = departmentParam != null && departmentFilter == null
                val chronicityFilter = call.request.queryParameters["chronicity"]
                val infectiousParam = call.request.queryParameters["infectious"]
                val infectiousFilter = infectiousParam?.toBooleanStrictOrNull()
                val rejectInfectiousFilter = infectiousParam != null && infectiousFilter == null
                val keyword = call.request.queryParameters["keyword"]
                val symptomKeyword = call.request.queryParameters["symptom_keyword"]
                val onsetPatterns = try {
                    call.request.queryParameters
                        .getAll(name = "onset_pattern")
                        .orEmpty()
                        .map { raw -> OnsetPattern.fromQueryOrThrow(raw = raw) }
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse(code = "INVALID_ONSET_PATTERN", message = e.message.orEmpty()),
                    )
                    return@handle
                }
                val examCategories = try {
                    call.request.queryParameters
                        .getAll(name = "exam_category")
                        .orEmpty()
                        .map { raw -> ExamCategory.fromQueryOrThrow(raw = raw) }
                } catch (e: IllegalArgumentException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse(code = "INVALID_EXAM_CATEGORY", message = e.message.orEmpty()),
                    )
                    return@handle
                }
                val hasPharmacologicalTreatmentParam = call.request.queryParameters["has_pharmacological_treatment"]
                val hasPharmacologicalTreatment = hasPharmacologicalTreatmentParam?.toBooleanStrictOrNull()
                val rejectHasPharmacologicalTreatment =
                    hasPharmacologicalTreatmentParam != null && hasPharmacologicalTreatment == null
                val hasSeverityGradingParam = call.request.queryParameters["has_severity_grading"]
                val hasSeverityGrading = hasSeverityGradingParam?.toBooleanStrictOrNull()
                val rejectHasSeverityGrading = hasSeverityGradingParam != null && hasSeverityGrading == null
                val keywordMatch = KeywordMatch.fromQuery(value = call.request.queryParameters["keyword_match"])
                val keywordTarget = DiseaseKeywordTarget.fromQuery(
                    value = call.request.queryParameters["keyword_target"]
                )
                val sortRaw = call.request.queryParameters["sort"]
                val sortKey = try {
                    DiseaseSortKey.fromQuery(raw = sortRaw)
                } catch (_: IllegalArgumentException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse(code = "INVALID_SORT_KEY", message = "Unknown sort key: $sortRaw"),
                    )
                    return@handle
                }
                val resolved = call.resolveScenarioWithOverride(
                    scenarioManager = scenarioManager,
                    endpointName = diseaseListMetadata.endpointName,
                    default = "default",
                    fixtureProvider = { scenario ->
                        val diseasesByScenario = diseaseListFixtures.diseasesByScenario
                        val scenarioDiseases = diseasesByScenario[scenario]
                            ?: diseasesByScenario.values.first()
                        val filtered = if (rejectDepartmentFilter || rejectInfectiousFilter) {
                            emptyList()
                        } else {
                            applyDiseaseListFilters(
                                diseases = scenarioDiseases,
                                chapterFilter = chapterFilter,
                                rejectChapterFilter = rejectChapterFilter,
                                departmentFilter = departmentFilter,
                                chronicitySerialName = chronicityFilter,
                                infectiousFilter = infectiousFilter,
                            )
                        }
                        val keywordFiltered = DiseaseSearchService.applyKeyword(
                            items = filtered,
                            keyword = keyword,
                            match = keywordMatch,
                            target = keywordTarget,
                        )
                        val additionallyFiltered =
                            if (rejectHasPharmacologicalTreatment || rejectHasSeverityGrading) {
                                emptyList()
                            } else {
                                DiseaseSearchService.applyAdditionalFilters(
                                    items = keywordFiltered,
                                    symptomKeyword = symptomKeyword,
                                    onsetPatterns = onsetPatterns,
                                    examCategories = examCategories,
                                    hasPharmacologicalTreatment = hasPharmacologicalTreatment,
                                    hasSeverityGrading = hasSeverityGrading,
                                )
                            }
                        val sorted = DiseaseSearchService.applySort(items = additionallyFiltered, sort = sortKey)
                        diseaseListFixtures.resolve(
                            summaries = sorted.map { it.toSummary() },
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
 * `/diseases` のクエリ由来フィルタを `Disease` 全体に適用する (Phase 11-10b で
 * `DiseaseSummary` 版から差し替え)。
 *
 * `icd10_chapter` / `department` / `chronicity` / `infectious` を AND 合成する。引数の null は
 * 「このフィルタを適用しない」を表す。`rejectChapterFilter=true` は呼び出し元で `icd10_chapter`
 * クエリが提供されたが `Icd10Chapter.fromSerialName` で解決できなかった (旧ローマ数字など) ことを
 * 表し、その場合は即座に空リストを返す。`chronicitySerialName` は `Chronicity.serialName` と生文字列
 * 比較するため、未知キーは `null` ではなくヒット 0 件を返す。`infectiousFilter` は
 * `toBooleanStrictOrNull()` で parse するため `"true"`/`"false"` 以外は `null` に落ちて無視する。
 * Disease 全体を返すのは、後段で `DiseaseSearchService.applyKeyword` が `nameKana` / `nameEnglish` /
 * `synonyms` を参照するため。
 */
private fun applyDiseaseListFilters(
    diseases: List<Disease>,
    chapterFilter: Icd10Chapter?,
    rejectChapterFilter: Boolean,
    departmentFilter: MedicalDepartment?,
    chronicitySerialName: String?,
    infectiousFilter: Boolean?,
): List<Disease> {
    if (rejectChapterFilter) {
        return emptyList()
    }
    var result = diseases
    if (chapterFilter != null) {
        result = result.filter { it.icd10Chapter == chapterFilter }
    }
    if (departmentFilter != null) {
        result = result.filter { disease -> disease.medicalDepartment.any { it == departmentFilter } }
    }
    if (chronicitySerialName != null) {
        result = result.filter { it.chronicity.serialName == chronicitySerialName }
    }
    if (infectiousFilter != null) {
        result = result.filter { it.infectious == infectiousFilter }
    }
    return result
}
