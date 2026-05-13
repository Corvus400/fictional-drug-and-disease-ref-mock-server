package io.github.corvus400.fictionaldrugdiseaserefmockserver.di

import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.MockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.config.loadMockServerConfig
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.categories.CategoriesFixture
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseDetailFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.DiseaseListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.blueprint.DiseaseBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseaseGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.disease.generator.DiseasePlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugDetailFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.DrugListFixtures
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.blueprint.DrugBlueprintFactory
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugGenerator
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.drug.generator.DrugPlaceholderDictionary
import io.github.corvus400.fictionaldrugdiseaserefmockserver.fixture.naming.FixmergeNameAdapter
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.disease.Disease
import io.github.corvus400.fictionaldrugdiseaserefmockserver.model.drug.Drug
import io.github.corvus400.fictionaldrugdiseaserefmockserver.scenario.ScenarioManager
import io.ktor.server.application.Application
import io.ktor.server.plugins.di.dependencies

fun Application.configureDependencies() {
    val config = loadMockServerConfig()
    // `FixmergeNameAdapter` は drug/disease で共有する singleton。
    // disease → drug の順で生成し、{{disease}} placeholder が参照する
    // 疾患 fixture 一覧を DrugPlaceholderDictionary 経由で DrugGenerator に注入する
    // (Issue #206 カテゴリ C 参照整合性)。
    // Disease 側の self-reference placeholder ({{disease}} 等) は
    // DiseasePlaceholderDictionary 経由で Disease 自身の name を注入する (Issue #215)。
    val adapter = FixmergeNameAdapter()
    val diseasePlaceholderDictionary = DiseasePlaceholderDictionary()
    val diseases =
        DiseaseGenerator(adapter = adapter, placeholderDictionary = diseasePlaceholderDictionary)
            .generate(blueprints = DiseaseBlueprintFactory.build())
    val diseaseListFixtures = DiseaseListFixtures(diseases = diseases)
    val diseaseDetailFixtures = DiseaseDetailFixtures(diseases = diseases)
    val placeholderDictionary =
        DrugPlaceholderDictionary(nameAdapter = adapter, diseases = diseases)
    val drugs =
        DrugGenerator(
            adapter = adapter,
            placeholderDictionary = placeholderDictionary,
            diseases = diseases,
        )
            .generate(blueprints = DrugBlueprintFactory.build())
    val drugListFixtures = DrugListFixtures(drugs = drugs)
    val drugDetailFixtures = DrugDetailFixtures(drugs = drugs)
    val categoriesFixture = CategoriesFixture(drugs = drugs)
    dependencies {
        provide<MockServerConfig> { config }
        provide<ScenarioManager> { ScenarioManager(config.defaultScenario) }
        // 全件 raw fixture は List 型で provide する。
        // 用途別 (List = ページング/検索, Detail = id ルックアップ) は各 Fixtures class が担う。
        provide<List<Drug>> { drugs }
        provide<List<Disease>> { diseases }
        provide<DrugListFixtures> { drugListFixtures }
        provide<DrugDetailFixtures> { drugDetailFixtures }
        provide<DiseaseListFixtures> { diseaseListFixtures }
        provide<DiseaseDetailFixtures> { diseaseDetailFixtures }
        provide<CategoriesFixture> { categoriesFixture }
    }
}
