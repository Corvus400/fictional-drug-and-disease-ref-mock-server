package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.disease

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.ScenarioMeta
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies
import io.ktor.server.response.respond
import io.ktor.server.routing.get
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

private val defaultScenarios: List<ScenarioMeta> = listOf(
    ScenarioMeta(name = "default", title = "デフォルト", description = "全 80 件のフィクスマージ語ベース疾患"),
)

val diseaseCatalogEntries: List<EndpointEntry> = listOf(
    diseaseDetailMetadata.toEntry(scenarios = defaultScenarios),
    diseaseListMetadata.toEntry(scenarios = defaultScenarios),
)

@Suppress("UnusedParameter")
fun Application.diseaseModule(scenarioManager: ScenarioManager) {
    val provider: DiseaseFixtureProvider by dependencies
    routing {
        get("/diseases/{id}") {
            val id = call.parameters["id"].orEmpty()
            val disease = provider.getById(id = id)
            if (disease == null) {
                call.respond(status = HttpStatusCode.NotFound, message = mapOf("error" to "disease not found: $id"))
            } else {
                call.respond(disease)
            }
        }
        get("/diseases") {
            call.respond(provider.all)
        }
    }
}
