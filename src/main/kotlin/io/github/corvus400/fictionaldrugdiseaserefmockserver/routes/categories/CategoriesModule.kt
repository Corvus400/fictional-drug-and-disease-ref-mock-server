package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.categories

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.categories.CategoriesFixture
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.common.CategoriesResponse
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.documentStatelessEndpoint
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.routing

private val categoriesMetadata = EndpointMetadata(
    path = "/v1/categories",
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
 * ## なぜシナリオ切替の対象外なのか (§基本方針 9)
 *
 * 本モジュールは **シナリオ切替を持たない静的メタデータ API** を提供する。`CategoriesResponse`
 * は起動時に固定 drugs (120 件) と enum 宣言から一度だけ導出され、リクエストごとに常に同じ内容を
 * 返す。したがって以下の機能の **対象外** とする:
 *
 * - シナリオ切替ヘッダ (`/drugs` などで採用) によるレスポンス切替
 * - Admin API (`POST /__admin/configs/...`) によるレスポンスオーバーライド
 * - `scenarioRoute` / `respondWithScenario` の利用
 *
 * `Routing.kt` 上では [ScenarioManager] を渡さない
 * [io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.StatelessModuleRegistration]
 * で登録され、シナリオ切替不要であることを型レベルで明示する。これにより `_ ->` で
 * シナリオ管理オブジェクトを捨てるパターンを排除する。
 *
 * シナリオ非依存性 (§基本方針 9) は `CategoriesModuleTest` の bit-identical pin と
 * `CategoriesModuleSourceTest` の静的検証 (本パッケージ配下に切替ヘッダ名や
 * シナリオ管理パラメータ識別子が出現しないこと) で二重に担保される。
 *
 * ## 返却フィールド (7 種)
 *
 * `atc` / `therapeutic_categories` / `route_of_administration` / `dosage_form` /
 * `regulatory_class` / `icd10_chapters` / `medical_departments`
 *
 * ## キャッシュ戦略
 *
 * `CategoriesResponse` は起動時に 1 回だけ計算し、ハンドラ内では結果を再利用する
 * (リクエストごとに `CategoriesFixture.build()` を呼ばない)。これにより不変性が API レベルで
 * 保証される (シナリオ非依存性のリグレッション余地を物理的に縮小する)。
 */
fun Application.categoriesModule() {
    val categoriesFixture: CategoriesFixture by dependencies
    val cachedResponse = categoriesFixture.build()
    routing {
        get("/v1/categories", {
            documentStatelessEndpoint<CategoriesResponse>(
                metadata = categoriesMetadata,
                endpointDescription = categoriesScenarios.first().description,
                okResponseDescription = "7 カテゴリのメタデータ",
            )
        }) {
            call.respond(message = cachedResponse)
        }
    }
}
