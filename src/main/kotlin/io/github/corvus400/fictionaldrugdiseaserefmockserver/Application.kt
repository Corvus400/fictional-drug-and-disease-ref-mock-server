package io.github.corvus400.fictionaldrugdiseaserefmockserver

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.MockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.di.configureDependencies
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.configureLogging
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.configureOpenAPI
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.configureRouting
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.configureScenarioInterceptor
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.configureSerialization
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.configureStatusPages
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.di.dependencies

fun main() {
    embeddedServer(
        Netty,
        port = System.getenv("PORT")?.toIntOrNull() ?: 8080,
        host = System.getenv("HOST") ?: "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

fun Application.module() {
    // DI設定（最初に実行）
    configureDependencies()

    // Plugins
    configureSerialization()
    configureLogging()
    configureStatusPages()
    configureScenarioInterceptor()
    configureOpenAPI()

    // Routing（DIから取得）
    val scenarioManager: ScenarioManager by dependencies
    val config: MockServerConfig by dependencies
    configureRouting(
        scenarioManager = scenarioManager,
        config = config,
    )
}
