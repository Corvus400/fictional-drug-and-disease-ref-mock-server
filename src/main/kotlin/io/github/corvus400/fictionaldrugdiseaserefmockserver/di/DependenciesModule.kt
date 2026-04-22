package io.github.corvus400.fictionaldrugdiseaserefmockserver.di

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.MockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.loadMockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

fun Application.configureDependencies() {
    val config = loadMockServerConfig()
    // `FixmergeNameAdapter` は drug/disease で共有する singleton。
    // disease → drug の順で生成し、{{disease}} placeholder が参照する
    // DiseaseFixtureProvider を DrugPlaceholderDictionary 経由で DrugGenerator に注入する
    // (Issue #206 カテゴリ C 参照整合性)。
    val adapter = FixmergeNameAdapter()
    val diseases = DiseaseGenerator(adapter = adapter).generate(blueprints = DiseaseBlueprintFactory.build())
    val diseaseProvider = DiseaseFixtureProvider(all = diseases)
    val placeholderDictionary =
        DrugPlaceholderDictionary(nameAdapter = adapter, diseaseProvider = diseaseProvider)
    val drugs =
        DrugGenerator(adapter = adapter, placeholderDictionary = placeholderDictionary)
            .generate(blueprints = DrugBlueprintFactory.build())
    val drugProvider = DrugFixtureProvider(all = drugs)
    dependencies {
        provide<MockServerConfig> { config }
        provide<ScenarioManager> { ScenarioManager(config.defaultScenario) }
        provide<DrugFixtureProvider> { drugProvider }
        provide<DiseaseFixtureProvider> { diseaseProvider }
    }
}
