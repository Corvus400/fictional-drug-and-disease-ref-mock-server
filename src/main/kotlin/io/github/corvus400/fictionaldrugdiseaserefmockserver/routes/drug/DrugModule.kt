package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.drug

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
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
        get("/drugs/{id}") {
            val id = call.parameters["id"].orEmpty()
            val drug = provider.getById(id = id)
            if (drug == null) {
                call.respond(status = HttpStatusCode.NotFound, message = mapOf("error" to "drug not found: $id"))
            } else {
                call.respond(drug)
            }
        }
        get("/drugs") {
            call.respond(provider.all)
        }
    }
}
