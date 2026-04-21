package io.github.corvus400.fictionaldrugdiseaserefmockserver.routes.sample

import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.EndpointMetadata
import io.github.corvus400.fictionaldrugdiseaserefmockserver.catalog.toEntry
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.sample.SampleFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.ApiTag
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.scenarioRoute
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application

private val sampleMetadata = EndpointMetadata(
    path = "/api/sample",
    method = HttpMethod.Get,
    endpointName = "sample",
    tag = ApiTag.SAMPLE,
    summary = "テンプレート動作例エンドポイント",
)

val sampleCatalogEntries: List<EndpointEntry> = listOf(
    sampleMetadata.toEntry(scenarios = SampleFixtures.scenarioMetas.values.toList()),
)

fun Application.sampleModule(scenarioManager: ScenarioManager) {
    scenarioRoute(
        metadata = sampleMetadata,
        defaultScenario = "default",
        fixtureProvider = SampleFixtures,
        scenarioManager = scenarioManager,
        endpointDescription = "mock-server-base のテンプレート動作例。X-Mock-Scenario: empty でシナリオ切替可能。",
    )
}
