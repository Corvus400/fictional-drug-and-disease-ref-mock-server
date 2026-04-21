package io.github.corvus400.mockserverbase.di

import io.github.corvus400.mockserverbase.config.MockServerConfig
import io.github.corvus400.mockserverbase.config.loadMockServerConfig
import io.github.corvus400.mockserverbase.scenario.ScenarioManager
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

fun Application.configureDependencies() {
    val config = loadMockServerConfig()
    dependencies {
        provide<MockServerConfig> { config }
        provide<ScenarioManager> { ScenarioManager(config.defaultScenario) }
    }
}
