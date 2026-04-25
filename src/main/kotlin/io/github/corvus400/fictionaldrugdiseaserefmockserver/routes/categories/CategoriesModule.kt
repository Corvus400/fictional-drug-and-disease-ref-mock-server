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
 * scenarioRoute 非対応 (X-Mock-Scenario / Admin API オーバーライドの対象外)。
 * `CategoriesFixture` が起動時に固定 drugs から導出した 7 フィールドの `CategoriesResponse` を返す。
 */
fun Application.categoriesModule() {
    val categoriesFixture: CategoriesFixture by dependencies
    routing {
        get("/categories") {
            call.respond(message = categoriesFixture.build())
        }
    }
}
