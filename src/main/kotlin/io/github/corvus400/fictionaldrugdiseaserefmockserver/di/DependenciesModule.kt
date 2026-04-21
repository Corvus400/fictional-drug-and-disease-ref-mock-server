package io.github.corvus400.fictionaldrugdiseaserefmockserver.di

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.MockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.loadMockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

fun Application.configureDependencies() {
    val config = loadMockServerConfig()
    dependencies {
        provide<MockServerConfig> { config }
        provide<ScenarioManager> { ScenarioManager(config.defaultScenario) }
    }
}
