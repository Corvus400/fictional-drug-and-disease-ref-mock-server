package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.categories.CategoriesFixture
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

private val categoriesMetadata = EndpointMetadata(
    path = "/categories",
    method = HttpMethod.Get,
    endpointName = "categories",
    tag = ApiTag.CATEGORIES,
    summary = "全カテゴリメタデータを取得する",
)

private val categoriesScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(
        name = "default",
        title = "デフォルト",
        description = "ATC / 薬効カテゴリ / 投与経路 / 剤形 / 規制区分 / ICD-10 章 / 診療科の 7 カテゴリを集約",
    ),
)

val categoriesCatalogEntries: List<EndpointEntry> = listOf(
    categoriesMetadata.toEntry(scenarios = categoriesScenarios),
)

/**
 * `GET /categories`: 7 カテゴリのメタデータを単一レスポンスで返す。
 *
 * ## なぜ `ScenarioManager` を引数に取らないのか
 *
 * 本モジュールは **シナリオ切替を持たない静的メタデータ API** を提供する。`CategoriesResponse`
 * は起動時に固定 drugs (120 件) から一度だけ導出され、リクエストごとに常に同じ内容を返す。
 * したがって以下の機能の **対象外** とする:
 *
 * - X-Mock-Scenario ヘッダによるシナリオ切替
 * - Admin API (`POST /__admin/configs/...`) によるレスポンスオーバーライド
 * - `scenarioRoute` / `respondWithScenario` の利用
 *
 * `Routing.kt` 上では `ScenarioModuleRegistration` ではなく
 * [io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.StatelessModuleRegistration]
 * で登録され、シナリオ切替不要であることを型レベルで明示する。これにより `_ ->` で
 * `ScenarioManager` 引数を捨てるパターンを排除する。
 *
 * ## 返却フィールド (7 種)
 *
 * `atc` / `therapeutic_categories` / `route_of_administration` / `dosage_form` /
 * `regulatory_class` / `icd10_chapters` / `medical_departments`
 */
fun Application.categoriesModule() {
    val categoriesFixture: CategoriesFixture by dependencies
    routing {
        get("/categories") {
            call.respond(message = categoriesFixture.build())
        }
    }
}
