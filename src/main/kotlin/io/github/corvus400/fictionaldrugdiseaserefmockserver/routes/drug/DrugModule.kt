package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
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

private val defaultScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(name = "default", title = "デフォルト", description = "全 120 件のフィクスマージ語ベース医薬品"),
)

val drugCatalogEntries: List<EndpointEntry> = listOf(
    drugDetailMetadata.toEntry(scenarios = defaultScenarios),
    drugListMetadata.toEntry(scenarios = defaultScenarios),
)

@Suppress("UnusedParameter")
fun Application.drugModule(scenarioManager: ScenarioManager) {
    val provider: DrugFixtureProvider by dependencies
    routing {
        get("/drugs/{id}", {
            summary = drugDetailMetadata.summary
            tags(drugDetailMetadata.tag.tagName)
            description = "`id` で指定した医薬品詳細 Fixture を返す。"
            request {
                pathParameter<String>("id") {
                    description = "医薬品 ID (`drug_NNNN` 形式)"
                }
            }
            response {
                code(HttpStatusCode.OK) { body<Drug> { description = "医薬品詳細" } }
                code(HttpStatusCode.NotFound) { description = "指定 id が存在しない" }
            }
        }) {
            val id = call.parameters["id"].orEmpty()
            val drug = provider.getById(id = id)
            if (drug == null) {
                call.respond(status = HttpStatusCode.NotFound, message = mapOf("error" to "drug not found: $id"))
            } else {
                call.respond(drug)
            }
        }
        get("/drugs", {
            summary = drugListMetadata.summary
            tags(drugListMetadata.tag.tagName)
            description = "起動時に生成された全医薬品 Fixture を配列で返す。"
            response {
                code(HttpStatusCode.OK) { body<List<Drug>> { description = "医薬品一覧" } }
            }
        }) {
            call.respond(provider.all)
        }
    }
}
