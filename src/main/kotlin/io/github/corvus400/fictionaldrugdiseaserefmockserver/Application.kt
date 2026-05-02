package io.github.corvus400.fictionaldrugdiseaserefmockserver

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.Disclaimer
import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.MockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.di.configureDependencies
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.plugins.CrossReferenceInitCheck
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

    // 起動時 fail-fast: fixture の drug↔disease 参照整合性を検証し、
    // dangling があれば IllegalStateException で起動を失敗させる (Issue #49)。
    val drugs: List<Drug> by dependencies
    val diseases: List<Disease> by dependencies
    CrossReferenceInitCheck.run(
        drugs = drugs,
        diseases = diseases,
    )

    // Plugins
    configureSerialization()
    configureLogging()
    environment.log.warn(Disclaimer.FULL_JA_EN)
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
