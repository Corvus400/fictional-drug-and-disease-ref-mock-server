package io.github.corvus400.fictionaldrugdiseaserefmockserver.di

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.MockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.loadMockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugFixtureProvider
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

fun Application.configureDependencies() {
    val config = loadMockServerConfig()
    // `FixmergeNameAdapter` は drug/disease で共有する singleton。
    // drug → disease の順で一括生成し、先着決定論と drug/disease 間の
    // katakana 重複排除 (検証・成功基準 #6) を両立する。
    val adapter = FixmergeNameAdapter()
    val drugs = DrugGenerator(adapter = adapter).generate(blueprints = DrugBlueprintFactory.build())
    val diseases = DiseaseGenerator(adapter = adapter).generate(blueprints = DiseaseBlueprintFactory.build())
    val drugProvider = DrugFixtureProvider(all = drugs)
    val diseaseProvider = DiseaseFixtureProvider(all = diseases)
    dependencies {
        provide<MockServerConfig> { config }
        provide<ScenarioManager> { ScenarioManager(config.defaultScenario) }
        provide<DrugFixtureProvider> { drugProvider }
        provide<DiseaseFixtureProvider> { diseaseProvider }
    }
}
